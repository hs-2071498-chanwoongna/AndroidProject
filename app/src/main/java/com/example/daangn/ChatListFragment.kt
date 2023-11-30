package com.example.daangn

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daangn.databinding.ChatListBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatListFragment : Fragment(R.layout.chat_list) {

    private var binding: ChatListBinding? = null
    private lateinit var chatItemAdapter: ChatItemAdapter
    private val chatRoomList = mutableListOf<ChatItem>()

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val chatDB: CollectionReference by lazy {
        Firebase.firestore.collection(DBKey.DB_CHATS)
    }

    private fun getUserEmailFromUID(userId: String, onCompleteListener: OnCompleteListener<DocumentSnapshot>) {
        firestore.collection("Users").document(userId).get()
            .addOnCompleteListener(onCompleteListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentChatListBinding = ChatListBinding.bind(view)
        binding = fragmentChatListBinding

        chatItemAdapter = ChatItemAdapter()

        chatRoomList.clear()

        fragmentChatListBinding.chatListRecyclerView.adapter = chatItemAdapter
        fragmentChatListBinding.chatListRecyclerView.layoutManager = LinearLayoutManager(context)

        if (auth.currentUser == null) {
            return
        }
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

        firestore.collection("Chats")
            .whereEqualTo("receiverId", currentUserEmail)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // 예외 처리
                    return@addSnapshotListener
                }

                // 데이터가 변경될 때마다 호출
                val updatedList = snapshot?.toObjects(ChatItem::class.java)
                updatedList?.let {
                    // 최신순으로 정렬
                    it.sortByDescending { chatItem -> chatItem.timestamp }

                    // 메시지를 보낸 사용자의 이메일을 받아와서 ChatItem에 저장
                    val fetchCount = it.size
                    var fetchedCount = 0

                    for (chatItem in it) {
                        getUserEmailFromUID(chatItem.senderId) { task ->
                            if (task.isSuccessful) {
                                val senderEmail = task.result?.getString("email")

                                // 수정된 부분: 현재 사용자의 이메일과 메시지의 receiverId를 비교하여 조건을 만족하는 경우에만 리스트에 추가
                                if (senderEmail == currentUserEmail || chatItem.receiverId == currentUserEmail) {
                                    chatItem.receiverId = senderEmail ?: "Unknown"
                                    chatRoomList.add(chatItem)
                                }
                            } else {
                                // 에러 처리
                            }

                            fetchedCount++
                            if (fetchedCount == fetchCount) {
                                // 마지막 아이템까지 처리되었을 때만 어댑터에 데이터 업데이트
                                chatItemAdapter.submitList(it)
                            }
                        }
                    }
                }
            }
    }

    override fun onResume() {
        super.onResume()
        chatItemAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
