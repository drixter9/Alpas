package com.example.alpas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alpas.adapters.AlpasMessageListAdapter
import com.example.alpas.adapters.AlpasSearchUserListAdapter
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.Chat
import com.example.alpas.models.UserAlpas
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.activity_search_user_chat.*

class SearchUserChatActivity : BaseActivity() {

    private lateinit var mUserList: ArrayList<UserAlpas>
    lateinit var adapter: AlpasSearchUserListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_user_chat)
        setupActionbar()
        getUserList()

        if (sv_user_search != null) {
            sv_user_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    sv_user_search.clearFocus()
                    return true
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter.filter(newText)
                    return true
                }
            })
        }
    }

    private fun setupActionbar() {
        setSupportActionBar(toolbar_search_user_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_search_user_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getUserList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserList(this@SearchUserChatActivity)
    }

    fun successUserListFromFireStoreSearch( userList: ArrayList<UserAlpas>) {
        mUserList = userList

        rv_user_RecyclerView.layoutManager = LinearLayoutManager(this)
        rv_user_RecyclerView.setHasFixedSize(true)

        adapter = AlpasSearchUserListAdapter(this, mUserList)
        rv_user_RecyclerView.adapter = adapter
        hideProgressDialog()
    }

}