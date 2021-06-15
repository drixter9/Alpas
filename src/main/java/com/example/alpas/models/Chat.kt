package com.example.alpas.models


data class Chat(
    var senderId:String = "",
    var senderImage:String = "",
    var senderNAme: String= "",
    var receiverId:String = "",
    var receiverImage:String = "",
    var recieverName:String="",
    var message:String = "",
    var date:String=""
)