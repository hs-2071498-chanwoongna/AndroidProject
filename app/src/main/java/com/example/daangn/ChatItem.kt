package com.example.daangn

data class ChatItem(
    var timestamp: Long = 0,
    var receiverId: String = "",
    var senderId: String = "",
    var senderEmail: String = "",
    var text: String = ""
) {
    constructor(senderId: String, text: String, timestamp: String) : this(0, "", "", "", "")
}
