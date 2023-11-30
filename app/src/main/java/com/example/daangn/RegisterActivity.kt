package com.example.daangn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val signUp = findViewById<Button>(R.id.btn_register)
        signUp.setOnClickListener {
            performSignUp()
        }
    }

    private fun performSignUp() {
        val userEmail = findViewById<EditText>(R.id.edit_id)
        val userPassword = findViewById<EditText>(R.id.edit_pw)
        val userName = findViewById<EditText>(R.id.name)
        val userBirth = findViewById<EditText>(R.id.birthday)

        val inputEmail = userEmail.text.toString()
        val inputPassword = userPassword.text.toString()
        val inputName = userName.text.toString()
        val inputBirth = userBirth.text.toString()

        if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
            Toast.makeText(this, "올바른 이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (inputEmail.isEmpty() || inputPassword.isEmpty() || inputName.isEmpty() || inputBirth.isEmpty()) {
            Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (inputPassword.length < 6) {
            Toast.makeText(this, "비밀번호는 6자리 이상이어야 합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(inputEmail, inputPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    val uid = user?.uid

                    val userData = hashMapOf(
                        "email" to inputEmail,
                        "name" to inputName,
                        "password" to inputPassword,
                        "birthday" to inputBirth
                    )

                    if (uid != null) {
                        // Firestore에 사용자 정보 추가
                        firestore.collection("Users")
                            .document(uid)
                            .set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "회원가입 및 로그인 완료", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Log.e("RegisterActivity", "Firestore 데이터 추가 실패", e)
                                Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                            }
                    }

                    startMainActivity()
                }
            }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
