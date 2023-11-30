package com.example.daangn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_fragment)

        auth = FirebaseAuth.getInstance()

        findViewById<Button>(R.id.LoginButton)?.setOnClickListener {
            val userEmail = findViewById<EditText>(R.id.emailEditText)?.text.toString()
            val password = findViewById<EditText>(R.id.passwordEditText)?.text.toString()

            if (userEmail.isBlank() || password.isBlank()) {
                Toast.makeText(
                    this,
                    "이메일과 비밀번호를 모두 입력해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                doLogin(userEmail, password)
            }
        }

        // 회원가입 버튼 클릭 이벤트
        findViewById<Button>(R.id.btn_register)?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun doLogin(userEmail: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Log.w("LoginActivity", "signInWithEmail", it.exception)
                    Toast.makeText(
                        this,
                        "이메일 또는 비밀번호가 올바르지 않습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
