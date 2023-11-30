package com.example.daangn

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daangn.DBKey.Companion.DB_ARTICLES
import com.example.daangn.databinding.HomeFragmentBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(R.layout.home_fragment) {

    private var binding: HomeFragmentBinding? = null
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var firestore: FirebaseFirestore

    private val articleList = mutableListOf<ArticleModel>()

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    companion object {
        const val EDIT_ARTICLE_REQUEST_CODE = 123
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHomeBinding = HomeFragmentBinding.bind(view)
        binding = fragmentHomeBinding

        articleList.clear()

        firestore = Firebase.firestore
        articleAdapter = ArticleAdapter(onItemClicked = { articleModel ->
            if (auth.currentUser != null) {
                // Handle item click
                handleItemClick(articleModel)
            }
        })

        fragmentHomeBinding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.articleRecyclerView.adapter = articleAdapter

        val notSoldCheckBox = fragmentHomeBinding.notsold
        val soldCheckBox = fragmentHomeBinding.sold

        notSoldCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                soldCheckBox.isChecked = false //다른 체크박스 해제
                filterArticles(false)
            } else {
                //체크 해제 시 전체 아이템 보여주기
                articleAdapter.submitList(articleList)
            }
        }

        soldCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                notSoldCheckBox.isChecked = false //다른 체크박스 해제
                filterArticles(true)
            } else {
                //체크 해제 시 전체 아이템 보여주기
                articleAdapter.submitList(articleList)
            }
        }

        fragmentHomeBinding.addFloatingButton.setOnClickListener {
            if (auth.currentUser != null) {
                val intent = Intent(requireContext(), AddArticleActivity::class.java)
                startActivity(intent)
            } else {
                Snackbar.make(view, "로그인 후 사용해주세요", Snackbar.LENGTH_LONG).show()
            }
        }

        firestore.collection(DB_ARTICLES).addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Handle error
                return@addSnapshotListener
            }

            articleList.clear()
            for (document in snapshot!!) {
                val articleModel = document.toObject(ArticleModel::class.java)
                articleList.add(articleModel)
            }

            articleAdapter.notifyDataSetChanged()
        }
    }

    private fun handleItemClick(articleModel: ArticleModel) {
        // 사용자가 클릭한 아이템에 대한 처리
        if (auth.currentUser != null) {
            if (articleModel.sellerId == auth.currentUser?.email) {
                //현재 로그인한 사용자의 이메일과 판매자의 이메일이 일치하면 EditArticleActivity로 이동
                navigateToEditArticle(articleModel)
            } else {
                //일치하지 않으면 ArticleChatActivity로 이동
                navigateToArticleChat(articleModel)
            }
        }
    }

    private fun navigateToEditArticle(articleModel: ArticleModel) {
        val intent = Intent(requireContext(), EditArticleActivity::class.java).apply {
            putExtra("DOCUMENT_ID", articleModel.documentId)
            putExtra("SELLER_ID", articleModel.sellerId)
            putExtra("TITLE", articleModel.title)
            putExtra("PRICE", articleModel.price)
            putExtra("IS_SOLD", articleModel.sold)
            putExtra("CONTENT", articleModel.content)
        }
        startActivityForResult(intent, EDIT_ARTICLE_REQUEST_CODE)
    }




    private fun navigateToArticleChat(articleModel: ArticleModel) {
        val intent = Intent(requireContext(), ArticleChatActivity::class.java)
        intent.putExtra("SELLER_ID", articleModel.sellerId)
        intent.putExtra("TITLE", articleModel.title)
        intent.putExtra("PRICE", articleModel.price)
        intent.putExtra("IS_SOLD", articleModel.sold)
        intent.putExtra("CONTENT", articleModel.content)
        startActivity(intent)
    }

    private fun filterArticles(isSold: Boolean) {
        val filteredList = articleList.filter { it.sold == isSold }
        articleAdapter.submitList(filteredList)
    }


    override fun onResume() {
        super.onResume()
        //articleAdapter.notifyDataSetChanged()
        fetchArticleData()
    }

    private fun fetchArticleData() {
        firestore.collection(DB_ARTICLES).addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Handle error
                return@addSnapshotListener
            }

            articleList.clear()
            for (document in snapshot!!) {
                val articleModel = document.toObject(ArticleModel::class.java)
                articleList.add(articleModel)
            }

            articleAdapter.submitList(articleList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove Firestore snapshot listener
    }
}