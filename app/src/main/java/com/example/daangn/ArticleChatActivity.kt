package com.example.daangn

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ArticleChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.article_chat)

        val sellerId = intent.getStringExtra("SELLER_ID")
        val title = intent.getStringExtra("TITLE")
        val price = intent.getStringExtra("PRICE")
        val isSold = intent.getBooleanExtra("IS_SOLD", false)
        val content = intent.getStringExtra("CONTENT")

        val sellerIdTextView: TextView = findViewById(R.id.sellerIdText)
        val titleTextView: TextView = findViewById(R.id.titleText)
        val priceTextView: TextView = findViewById(R.id.priceText)
        val isSoldTextView: TextView = findViewById(R.id.isSoldText)
        val contentTextView: TextView = findViewById(R.id.contentText)

        sellerIdTextView.text = sellerId
        titleTextView.text = title
        priceTextView.text = price
        isSoldTextView.text = if (isSold) "판매완료" else "판매중"
        contentTextView.text = content

        val sendmessagebutton: FloatingActionButton = findViewById(R.id.floatingActionButton)
        sendmessagebutton.setOnClickListener {
            startComposeMessageActivity()
        }
    }

    private fun startComposeMessageActivity() {
        val dialogView = layoutInflater.inflate(R.layout.compose_message, null)

        val messageEditText = dialogView.findViewById<EditText>(R.id.messageEditText)
        val sendMessageButton = dialogView.findViewById<Button>(R.id.sendMessageButton)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.show()

        sendMessageButton.setOnClickListener {
            val messageText = messageEditText.text.toString().trim()

            if (messageText.isEmpty()) {
                Toast.makeText(this, "메시지를 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val firestore = FirebaseFirestore.getInstance()
            val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val receiverId = intent.getStringExtra("SELLER_ID")


            val messageData = hashMapOf(
                "text" to messageText,
                "senderId" to currentUserUid,
                "receiverId" to receiverId,
                "timestamp" to System.currentTimeMillis()
            )

            firestore.collection("Chats")
                .add(messageData)
                .addOnSuccessListener {
                    Toast.makeText(this, "메시지 전송 성공", Toast.LENGTH_SHORT).show()
                    alertDialog.dismiss()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "메시지 전송 실패: $e", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
