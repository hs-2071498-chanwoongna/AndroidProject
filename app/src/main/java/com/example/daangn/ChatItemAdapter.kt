package com.example.daangn

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.daangn.databinding.ItemChatBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ChatItemAdapter : ListAdapter<ChatItem, ChatItemAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatItem: ChatItem) {
            try {
                // 뷰 연결
                binding.senderTextView.text = chatItem.receiverId
                binding.messageTextView.text = chatItem.text

                val timeZone = TimeZone.getTimeZone("Asia/Seoul") // 서울 시간대로 설정
                val format = SimpleDateFormat("MM월 dd일 HH:mm", Locale("ko", "KR"))
                format.timeZone = timeZone

                val formattedDate = format.format(Date(chatItem.timestamp))
                binding.timeTextView.text = formattedDate

            } catch (e: Exception) {
                // 예외 처리: 뷰 연결 중 오류 발생
                showToast("뷰 연결 중 오류가 발생했습니다.")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return try {
            ViewHolder(
                ItemChatBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } catch (e: Exception) {
            // 예외 처리: ViewHolder 생성 중 오류 발생
            showToast("ViewHolder 생성 중 오류가 발생했습니다.")
            throw e
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder.bind(currentList[position])
        } catch (e: Exception) {
            // 예외 처리: ViewHolder 바인딩 중 오류 발생
            showToast("ViewHolder 바인딩 중 오류가 발생했습니다.")
        }
    }

    private fun showToast(message: String) {
        // 예외 처리에 사용되는 토스트 메시지 표시
        // 이 함수를 호출하는 환경에 따라 적절한 처리를 추가하세요.
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatItem>() {

            override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                return oldItem.timestamp == newItem.timestamp
            }

            override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
