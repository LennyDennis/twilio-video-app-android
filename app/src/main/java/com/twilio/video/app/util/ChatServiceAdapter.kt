package com.twilio.video.app.util

import com.google.gson.Gson
import com.twilio.video.app.model.WebSocketMessage
import okhttp3.*
import okio.ByteString
import timber.log.Timber

class ChatServiceAdapter(listener: ChatServiceListener, room: String): WebSocketListener() {

    var ws: WebSocket? = null
    var retry: Retry = Retry.GO
    var listener: ChatServiceListener? = null
    var room: String? = null
    final val JSON = MediaType.parse("application/json; charset=utf-8")

    init{
        this.listener = listener
        this.room = room
        setupWebSocket()
    }

    fun setupWebSocket(){
        val client = OkHttpClient.Builder().retryOnConnectionFailure(false).build()
        val request = Request.Builder().url("wss://chat.starlineventures.com/subscribe/" + room)
                .build()
        ws = client.newWebSocket(request, this)
        client.dispatcher().executorService().shutdown()
    }

    fun sendMessage(text: String, room: String, callback: Callback){
        val client = OkHttpClient.Builder().build()
        val body = RequestBody.create(JSON, text)
        val request = Request.Builder()
                .url("https://chat.starlineventures.com/publish/" + room)
                .post(body)
                .build()
        client.newCall(request).enqueue(callback)
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Timber.i("on Closed")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        Timber.i("on Message bytes")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        try {
            val v = Gson().fromJson(text, WebSocketMessage.Basic::class.java)
            listener?.hasNewMessage(v)
            Timber.i(v.toString())
        } catch(ex: Exception){
            Timber.i(ex.message)

            try {
                val v = WebSocketMessage.System(text)
                listener?.hasNewMessage(v)
            }catch(exception: Exception){
                Timber.i(ex.message)
            }
        }

    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        closeOnFailure(webSocket)
        if(retry == Retry.GO){
            setupWebSocket()
        }

        Timber.i("on Failure")
    }

    private fun closeOnFailure(webSocket: WebSocket){
        webSocket.close(1000, "Failure")
        close()
    }

    fun closeAndStop(){
        retry = Retry.STOP
        close()
    }

    private fun close(){

        if(ws != null) {
            ws?.close(1000, "closed on purpose")
        }
    }

    enum class Retry {
        STOP, GO
    }

}