package com.example.daangn

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditArticleActivity : AppCompatActivity() {

    private lateinit var editPriceEditText: EditText
    private lateinit var editIsSellCheckBox: CheckBox
    private lateinit var editIsSoldCheckBox: CheckBox
    private lateinit var updateButton: Button

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var documentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_article)

        editPriceEditText = findViewById(R.id.editPriceEditText)
        editIsSellCheckBox = findViewById(R.id.editIsSellCheckBox)
        editIsSoldCheckBox = findViewById(R.id.editIsSoldCheckBox)
        updateButton = findViewById(R.id.updateButton)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // 이전 화면에서 넘어온 데이터 받기
        val price = intent.getStringExtra("PRICE")
        val isSold = intent.getBooleanExtra("IS_SOLD", false)

        // 받아온 데이터를 UI에 적용
        editPriceEditText.setText(price)
        editIsSellCheckBox.isChecked = !isSold
        editIsSoldCheckBox.isChecked = isSold

        // Document ID 가져오기
        documentId = intent.getStringExtra("DOCUMENT_ID")

        // CheckBox 클릭 이벤트 처리
        editIsSellCheckBox.setOnClickListener {
            if (editIsSellCheckBox.isChecked) {
                // 판매 중인 경우, 판매 완료 체크 해제
                editIsSoldCheckBox.isChecked = false
            }
        }

        editIsSoldCheckBox.setOnClickListener {
            if (editIsSoldCheckBox.isChecked) {
                // 판매 완료인 경우, 판매 중 체크 해제
                editIsSellCheckBox.isChecked = false
            }
        }

        updateButton.setOnClickListener {
            // EditText에서 가격을 가져오기
            val newPrice = editPriceEditText.text.toString()

            // CheckBox에서 판매 상태 가져오기
            val newIsSold = editIsSoldCheckBox.isChecked

            // 이전 화면에서 넘어온 데이터 받기
            val sellerId = intent.getStringExtra("SELLER_ID").toString()
            val title = intent.getStringExtra("TITLE").toString()
            val imageUrl = intent.getStringExtra("IMAGE_URL").toString()
            val content = intent.getStringExtra("CONTENT").toString()

            if (documentId.isNullOrEmpty()) {
                // 예외 처리: Document ID가 없을 경우
                showToast("문서 ID가 유효하지 않습니다.")
                return@setOnClickListener
            }

            // 기존의 값 중에서 업데이트할 값만 변경
            val updatedModel = ArticleModel(documentId!!, sellerId, title, System.currentTimeMillis(), "$newPrice", newIsSold, content)

            // Firestore에 업데이트 요청
            documentId?.let {
                firestore.collection("Articles").document(it)
                    .set(updatedModel)
                    .addOnSuccessListener {
                        // 업데이트 성공
                        finish()
                    }
                    .addOnFailureListener { e ->
                        // 업데이트 실패
                        showToast("업데이트 실패: $e")
                    }
            }
        }
    }

    private fun showToast(message: String) {
        // 토스트 메시지 표시
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
