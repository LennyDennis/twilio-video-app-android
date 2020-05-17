package com.twilio.video.app.model

import java.util.*

sealed class WebSocketMessage {
    abstract val id: String
    abstract val name: String
    abstract val text: String
    abstract val created: Long
    abstract val senderId: String
    abstract val room: String

    data class Basic constructor(
            override val id: String = UUID.randomUUID().toString(),
            override val text: String,
            override val name: String,
            override val created: Long,
            override val senderId: String,
            override val room: String
    ) : WebSocketMessage() {}

    data class System constructor(
            val txt: String
    ) : WebSocketMessage() {
        override val id: String = UUID.randomUUID().toString()
        override val name: String = ""
        override val text: String = txt
        override val created: Long = Date().time
        override val senderId: String = UUID.randomUUID().toString()
        override val room: String = ""
    }

}