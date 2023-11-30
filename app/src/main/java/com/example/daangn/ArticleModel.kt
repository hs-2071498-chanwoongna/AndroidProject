package com.example.daangn

data class ArticleModel(
    val documentId: String,
    val sellerId: String,
    val title: String,
    val createdAt: Long,
    var price: String,
    var sold: Boolean,
    val content: String
) {
    constructor() : this("", "", "", 0, "",  false, "")
}
