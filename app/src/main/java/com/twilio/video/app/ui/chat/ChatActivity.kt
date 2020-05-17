package com.twilio.video.app.ui.chat

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.google.gson.Gson
import com.twilio.video.app.R
import com.twilio.video.app.base.BaseActivity
import com.twilio.video.app.data.Preferences
import com.twilio.video.app.model.BaseMessage
import com.twilio.video.app.model.MemberData
import com.twilio.video.app.model.WebSocketMessage
import com.twilio.video.app.util.ChatServiceAdapter
import com.twilio.video.app.util.ChatServiceListener
import com.twilio.video.app.util.toMD5
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import java.util.*
import javax.inject.Inject

class ChatActivity : BaseActivity() {
    @Inject
    internal lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Add the preference fragment
        val chatFragment = ChatFragment()
        val b = Bundle()
        b.putString(Preferences.DISPLAY_NAME, sharedPreferences.getString(Preferences.DISPLAY_NAME, null))
        b.putString(Preferences.ROOM_NAME, sharedPreferences.getString(Preferences.ROOM_NAME, null))

        chatFragment.arguments = b
        val commit = supportFragmentManager
                .beginTransaction()
                .replace(android.R.id.content, chatFragment)
                .commit()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    class ChatFragment : Fragment() {
        private var mMessageAdapter: MessageListAdapter? = null
        private var mMessageRecycler: RecyclerView? = null
        private var webSocketService: ChatServiceAdapter? = null
        private var chatServiceListener: ChatServiceListener? = null
        private var currentMember: MemberData? = null
        private var displayName: String? = null
        private var roomName: String? = null
        private var chatText: EditText? = null


        override fun onCreate(savedInstanceState: Bundle?){
            super.onCreate(savedInstanceState)
            chatServiceListener = object: ChatServiceListener {
                override fun hasNewMessage(message: WebSocketMessage) {
                    val b = BaseMessage(message.id, message.text, message.name, message.senderId, message.created, message.room)
                    activity?.runOnUiThread(Runnable() {
                        mMessageAdapter?.addMessage(b)
                        mMessageAdapter?.notifyDataSetChanged()
                        mMessageRecycler!!.smoothScrollToPosition(mMessageAdapter!!.itemCount - 1)
                    })

                }
            }
            displayName = arguments?.get(Preferences.DISPLAY_NAME) as String?
            roomName = arguments?.get(Preferences.ROOM_NAME) as String?
            webSocketService = ChatServiceAdapter(chatServiceListener as ChatServiceListener, roomName!!.toMD5())
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            super.onCreateView(inflater, container, savedInstanceState)
            val view = inflater.inflate(R.layout.chat_room, container, false)


            currentMember = MemberData(displayName, UUID.randomUUID().toString())
            val welcome = WebSocketMessage.Basic(UUID.randomUUID().toString(),
                    "Welcome " + displayName + ", You have joined " + roomName,
                    "",
                    Date().time,
                    UUID.randomUUID().toString(),
                    roomName!!.toMD5())
            mMessageAdapter = MessageListAdapter(context, currentMember)
            mMessageRecycler = view.findViewById<View>(R.id.chat_message_list) as RecyclerView
            mMessageRecycler!!.adapter = mMessageAdapter
            mMessageRecycler!!.layoutManager = LinearLayoutManager(activity)
            chatText = view.findViewById<EditText>(R.id.edittext_chatbox)
            sendChatMessage(welcome, roomName!!)
            view.findViewById<Button>(R.id.button_chatbox_send).setOnClickListener(View.OnClickListener {
                val text = chatText?.text.toString()

                val chatMessage =  WebSocketMessage.Basic(UUID.randomUUID().toString(),
                        text,
                        currentMember!!.name,
                        Date().time,
                        currentMember!!.id,
                        roomName!!.toMD5())
                sendChatMessage(chatMessage, roomName!!)

            })

            return view;
        }

        private fun sendChatMessage(chatMessage: WebSocketMessage.Basic, room: String){
            try {

                webSocketService?.sendMessage(Gson().toJson(chatMessage), room.toMD5(), object: Callback {
                    override fun onResponse(call: Call?, response: Response) {
                        chatText?.setText("")
                        Timber.i("success")
                    }

                    override fun onFailure(call: Call?, e: IOException?) {
                        Timber.i("Activity Failure.")
                    }
                })


            } catch(ex: Exception){
                Timber.i(ex.toString())
            }
        }



        override fun onAttach(context: Context) {
            AndroidSupportInjection.inject(this)
            super.onAttach(context)
        }

        override fun onDestroyView() {
            webSocketService?.closeAndStop()
            super.onDestroyView()

        }

    }

}
