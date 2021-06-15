package com.example.alpas

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.core.graphics.toColor
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alpas.adapters.AlpasCartItemListAdapter
import com.example.alpas.adapters.AlpasChatAdapter
import com.example.alpas.adapters.AlpasMessageListAdapter
import com.example.alpas.adapters.AlpasProductAllListAdapter
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.Chat
import com.example.alpas.models.Product
import com.example.alpas.models.UserAlpas
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_cart_list.*
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.fragment_consultation.*
import kotlinx.android.synthetic.main.fragment_product_list.*


class MessageActivity : BaseActivity() {

    private lateinit var mUserList: ArrayList<UserAlpas>
    private var chatList = ArrayList<Chat>()
    private var chatListLast = ArrayList<Chat>()
    lateinit var adapter: AlpasMessageListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            FirebaseService.token = it.token
        }
        setupActionbar()
        getUserList()

        if (sv_message_search != null) {
            sv_message_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    sv_message_search.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter.filter(newText)
                    return true
                }
            })
        }
        ib_search_user.setOnClickListener {
            startActivity(Intent(this, SearchUserChatActivity::class.java))
        }

    }

    private fun setupActionbar() {
        setSupportActionBar(toolbar_message_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_message_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getUserList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserList(this@MessageActivity)
    }

    fun successUserListFromFireStore(userList: ArrayList<UserAlpas>) {
        mUserList = userList
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                chatListLast.clear()
                for (i in mUserList) {
                    chatList.clear()
                    for (dataSnapShot: DataSnapshot in snapshot.children) {
                        val chat = dataSnapShot.getValue(Chat::class.java)

                        if (chat!!.senderId == i.uid && chat.receiverId == FirestoreClass().getCurrentUserID() ||
                            chat.senderId == FirestoreClass().getCurrentUserID() && chat.receiverId == i.uid
                        ) {
                            chatList.add(chat)
                        }
                    }
                    if (chatList.size > 0) {
                        chatListLast.add(chatList.last())
                    }
                }
                chatListLast.sortByDescending { it.date }
                rv_message_RecyclerView.layoutManager = LinearLayoutManager(this@MessageActivity)
                rv_message_RecyclerView.setHasFixedSize(true)

                adapter = AlpasMessageListAdapter(this@MessageActivity, chatListLast)
                rv_message_RecyclerView.adapter = adapter
                hideProgressDialog()
            }
        })
    }
}