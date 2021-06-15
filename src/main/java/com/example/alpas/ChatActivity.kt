package com.example.alpas

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alpas.adapters.AlpasChatAdapter
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.Chat
import com.example.alpas.models.NotificationData
import com.example.alpas.models.PushNotification
import com.example.alpas.models.UserAlpas
import com.example.alpas.utilsAlpas.GlideLoader
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*
import kotlin.collections.HashMap

class ChatActivity : BaseActivity() {

    private lateinit var mUserDetailsReceiver: UserAlpas
    private var chatList = ArrayList<Chat>()
    private var userName: String = ""
    private var userIDReceiver: String = ""
    private val userID: String = FirestoreClass().getCurrentUserID()
    private var topic = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setupActionbar()
        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS_CHAT)) {
           userIDReceiver = intent.getStringExtra(Constants.EXTRA_USER_DETAILS_CHAT)!!
            getUserDetails(userIDReceiver)
        }

    }
    private fun getUserDetails(userID:String){
        FirestoreClass().getUserDetailsChat(this@ChatActivity,userID)
    }
    fun successUserDetails(user: UserAlpas){
        mUserDetailsReceiver = user

        val userImageSender = this.getSharedPreferences(
            Constants.ALPAS_PREFERENCES,
            Context.MODE_PRIVATE
        ).getString(Constants.LOGGED_IN_USER_IMAGE, "")!!

        userName = "${mUserDetailsReceiver.firstName} ${mUserDetailsReceiver.lastName}"
        GlideLoader(this@ChatActivity).loadUserImage(
            mUserDetailsReceiver.image,
            iv_img_profile_chat
        )
        tv_user_name.text = userName

        btn_send_message.setOnClickListener {
            val title = this.getSharedPreferences(Constants.ALPAS_PREFERENCES, Context.MODE_PRIVATE)
                .getString(Constants.LOGGED_IN_USERNAME, "")!!
            val message: String = et_message_chat.text.toString()

            if (message.isEmpty()) {
                Toast.makeText(applicationContext, "message is empty", Toast.LENGTH_SHORT).show()
                et_message_chat.setText("")
            } else {
                sendMessage(
                    userID,
                    userImageSender,
                    mUserDetailsReceiver.uid,
                    mUserDetailsReceiver.image,
                    message
                )
                topic = "/topics/${mUserDetailsReceiver.uid}"
                et_message_chat.setText("")
                PushNotification(
                    NotificationData(
                        FirestoreClass().getCurrentUserID(),
                        mUserDetailsReceiver.uid,
                        userImageSender,
                        title,
                        message,
                        0,
                        mUserDetailsReceiver.uid
                        ,System.currentTimeMillis()
                    ),
                    topic
                ).also {
                    FirebaseService().sendNotification(it)
                }
            }

        }
        readMessage(userID, mUserDetailsReceiver.uid)
    }


    private fun setupActionbar() {
        setSupportActionBar(toolbar_chat_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_chat_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun sendMessage(
        senderId: String,
        senderImage: String,
        receiverId: String,
        receiverImage: String,
        message: String
    ) {
        val senderName = this.getSharedPreferences(
            Constants.ALPAS_PREFERENCES,
            Context.MODE_PRIVATE
        ).getString(Constants.LOGGED_IN_USERNAME, "")!!

        val reference: DatabaseReference? = FirebaseDatabase.getInstance().reference
        val chat= Chat(
            senderId,
            senderImage,
            senderName,
            receiverId,
            receiverImage,
            userName,
            message,
            Calendar.getInstance().timeInMillis.toString()
        )
        reference!!.child(Constants.CHAT).push().setValue(chat)
    }

    private fun readMessage(senderId: String, receiverId: String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)

                    if (chat!!.senderId == senderId && chat.receiverId == receiverId ||
                        chat.senderId == receiverId && chat.receiverId == senderId
                    ) {
                        chatList.add(chat)
                    }
                }
                rv_chatRecyclerView.layoutManager = LinearLayoutManager(
                    this@ChatActivity,
                    RecyclerView.VERTICAL,
                    true
                )

                chatList.reverse()
                val chatAdapter = AlpasChatAdapter(this@ChatActivity, chatList)
                rv_chatRecyclerView.adapter = chatAdapter
                rv_chatRecyclerView.smoothScrollToPosition(0)
            }
        })
    }
}