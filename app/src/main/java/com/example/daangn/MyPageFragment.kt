package com.example.daangn

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class MyPageFragment : Fragment(R.layout.mypage_fragment) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logoutButton: Button = view.findViewById(R.id.LogoutButton)
        logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        auth.signOut()

        // LoginActivity로 이동하는 Intent 생성
        val intent = Intent(activity, LoginActivity::class.java)

        // 모든 백 스택을 제거하고 새로운 태스크 시작
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // LoginActivity 시작
        startActivity(intent)

        // 현재 Activity를 종료
        activity?.finish()
    }
}