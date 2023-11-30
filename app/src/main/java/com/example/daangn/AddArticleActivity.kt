package com.example.daangn

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.daangn.DBKey.Companion.DB_ARTICLES
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddArticleActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val articleDB: CollectionReference by lazy {
        Firebase.firestore.collection(DB_ARTICLES)
    }

    private val titleEditText: EditText by lazy {
        findViewById(R.id.titleEditText)
    }

    private val priceEditText: EditText by lazy {
        findViewById(R.id.priceEditText)
    }

    private val contentEditText: EditText by lazy {
        findViewById(R.id.contentEditText)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_article)

        findViewById<Button>(R.id.submitButton).setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val price = priceEditText.text.toString().trim()
            val content = contentEditText.text.toString().trim()

            if (title.isEmpty() || price.isEmpty() || content.isEmpty()) {
                showToast("모든 항목을 입력하세요.")
            } else {
                showProgress()
                uploadArticle(title, price, content)
            }
        }
    }

    private fun uploadArticle(title: String, price: String, content: String) {
        val sellerId = auth.currentUser?.email.orEmpty()

        val documentId = generateSafeDocumentId(title)

        val model = ArticleModel(documentId, sellerId, title, System.currentTimeMillis(), "$price", false, content)

        articleDB.document(documentId)
            .set(model)
            .addOnSuccessListener {
                hideProgress()
                showToast("게시물이 등록되었습니다.")
                finish()
            }
            .addOnFailureListener {
                showToast("데이터를 저장하는데 실패했습니다.")
                hideProgress()
            }
    }

    private fun generateSafeDocumentId(input: String): String {
        return input.replace(Regex("[^a-zA-Z0-9가-힣_]"), "_").take(50)
    }

    private fun showProgress() {
        findViewById<ProgressBar>(R.id.progressBar).isVisible = true
    }

    private fun hideProgress() {
        findViewById<ProgressBar>(R.id.progressBar).isVisible = false
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
