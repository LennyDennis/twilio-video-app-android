package com.twilio.video.app.util

import com.twilio.video.app.model.WebSocketMessage

interface ChatServiceListener {
    fun hasNewMessage(message: WebSocketMessage)
}