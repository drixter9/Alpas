package com.example.alpas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alpas.adapters.AlpasCommentAdapter
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.*
import com.example.alpas.utilsAlpas.GlideLoader
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_consultation_details.*
import kotlinx.android.synthetic.main.activity_thread_details.*
import kotlinx.android.synthetic.main.activity_thread_details.btn_go_to_save_forum
import kotlinx.android.synthetic.main.activity_thread_details.btn_save_forum_list
import kotlinx.android.synthetic.main.item_chat_left.view.*
import kotlinx.android.synthetic.main.item_forum_list.*
import kotlinx.android.synthetic.main.item_forum_list.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.set
import kotlin.math.abs


class ThreadDetailsActivity : BaseActivity() {

    private var mThreadDetails = Thread()
    private var mThreadID: String = ""
    private var mCommentID: String = ""
    private var mUserID: String = ""
    private var mUserName = ""
    private var mNotificationID = ""
    private var mLikedID: String = ""
    private var commentList = ArrayList<Comment>()
    private var mNotificationData = NotificationData()
    private var threadHashMap = HashMap<String, Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread_details)
        setupActionbar()

        mUserName = this.getSharedPreferences(Constants.ALPAS_PREFERENCES, Context.MODE_PRIVATE)
            .getString(Constants.LOGGED_IN_USERNAME, "")!!

        rv_comment_list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        if (intent.hasExtra(Constants.EXTRA_THREAD_DETAILS)) {
            mThreadID = intent.getStringExtra(Constants.EXTRA_THREAD_DETAILS)!!
        }

        if (intent.hasExtra(Constants.EXTRA_SAVED_THREAD_DETAILS)) {
            mUserID = intent.getStringExtra(Constants.EXTRA_SAVED_THREAD_DETAILS)!!
            if (mUserID == FirestoreClass().getCurrentUserID()) {
                btn_save_forum_list.visibility = View.GONE
                btn_go_to_save_forum.visibility = View.VISIBLE
            }
        } else {
            btn_save_forum_list.visibility = View.VISIBLE
            btn_go_to_save_forum.visibility = View.GONE
        }
        getThreadDetails()
        btn_send_comment.setOnClickListener {
            val message: String = et_comment_message.text.toString()
            if (message.isEmpty()) {
                Toast.makeText(applicationContext, "message is empty", Toast.LENGTH_SHORT).show()
            } else {
                sendComment()

            }
            readComment()
            et_comment_message.setText("")
            sv_comments.post(Runnable { sv_comments.fullScroll(ScrollView.FOCUS_DOWN) })

        }
        tb_like_thread_details.setOnClickListener {
            if (tb_like_thread_details.isChecked) {
                val like = Like(mThreadID, FirestoreClass().getCurrentUserID())
                saveLike(like, mThreadID)
            } else {
                deleteLike(mLikedID, mThreadID)
            }
        }

        btn_save_forum_list.setOnClickListener {
            val saveThread = SavedThread(
            FirestoreClass().getCurrentUserID(),
            mThreadDetails.user_id,
            mThreadDetails.user_name,
            mThreadDetails.user_image,
            mThreadDetails.thread_title,
            mThreadDetails.thread_field_of_study,
            mThreadDetails.thread_topic,
            mThreadDetails.thread_description,
            mThreadDetails.thread_image,
            mThreadDetails.thread_like,
            mThreadDetails.thread_comments,
            mThreadDetails.thread_date,
            mThreadDetails.thread_id,
            )
            saveThread(saveThread)
        }
        btn_go_to_save_forum.setOnClickListener {
            val intent = Intent(this, SaveThreadsActivity::class.java)
            startActivity(intent)
        }

        iv_user_image_thread_details.setOnClickListener {
            userInfo(mThreadDetails.user_id)
        }
        readComment()
    }

    private fun setupActionbar() {
        setSupportActionBar(toolbar_thread_details_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_thread_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getThreadDetails() {
        FirestoreClass().getThreadDetail(this, mThreadID)
    }

    fun threadDetailsSuccess(thread: Thread) {
        mThreadDetails = thread
        GlideLoader(this).loadUserImage(mThreadDetails.user_image, iv_user_image_thread_details)
        tv_thread_title_details.text = mThreadDetails.thread_title
        tv_thread_type_engineering_details.text = mThreadDetails.thread_field_of_study
        tv_threads_topic_details.text = mThreadDetails.thread_topic

        if ((mThreadDetails.thread_date - System.currentTimeMillis()) >= TimeUnit.HOURS.toMillis(24)) {
            val dateFormat = "dd/MM/yyyy"
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = mThreadDetails.thread_date
            val dateMonthYear = formatter.format(calendar.time)
            tv_thread_date_details.text = dateMonthYear
        } else {
            val dateFormat = "HH:mm"
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = mThreadDetails.thread_date
            val dateHour = formatter.format(calendar.time)
            tv_thread_date_details.text = dateHour
        }
        tv_threads_description_details.text = mThreadDetails.thread_description

        if (mThreadDetails.thread_image != "") {
            GlideLoader(this).loadProductImage(mThreadDetails.thread_image, iv_thread_details_image)
            iv_thread_details_image.visibility = View.VISIBLE
        } else {
            iv_thread_details_image.visibility = View.GONE
        }
        tv_threads_description_details.text = mThreadDetails.thread_description
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(Constants.LIKE)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val like = dataSnapShot.getValue(Like::class.java)

                    if (like!!.user_id == FirestoreClass().getCurrentUserID() && like.thread_id == mThreadID) {
                        mLikedID = dataSnapShot.key.toString()
                        tb_like_thread_details.isChecked = true
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        val numberString = when {
            (abs(mThreadDetails.thread_like / 1000000)) > 1 -> (mThreadDetails.thread_like / 1000000).toString() + "m"
            (abs(mThreadDetails.thread_like / 1000) > 1) -> (mThreadDetails.thread_like / 1000).toString() + "k"
            else -> mThreadDetails.thread_like.toString()
        }
        tb_like_thread_details.textOn = numberString
        tb_like_thread_details.text = numberString
    }

    fun saveThread(savedThread: SavedThread){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addToSavedThreadsItemsActivity(this, savedThread)
    }

    fun addToSavedSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this,
            resources.getString(R.string.msg_add_to_saved_success_thread),
            Toast.LENGTH_SHORT
        ).show()
        btn_save_forum_list.visibility = View.GONE
        btn_go_to_save_forum.visibility = View.VISIBLE
        getThreadDetails()
    }

    private fun sendComment() {
        val reference: DatabaseReference? = FirebaseDatabase.getInstance().reference
        val userImage = this.getSharedPreferences(
            Constants.ALPAS_PREFERENCES,
            Context.MODE_PRIVATE
        ).getString(Constants.LOGGED_IN_USER_IMAGE, "")!!

        val comment = Comment(
            mThreadID,
            FirestoreClass().getCurrentUserID(),
            mUserName,
            userImage,
            et_comment_message.text.toString(),
            0,
            Calendar.getInstance().timeInMillis,
        )
        notificationThread(Constants.NOTIFICATION_COMMENTS)
        val notification: DatabaseReference = FirebaseDatabase.getInstance().reference
        notification.child(Constants.NOTIFICATION).push().setValue(mNotificationData)
        reference!!.child(Constants.COMMENT).push().setValue(comment)
    }

    private fun readComment() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(Constants.COMMENT)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                commentList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val comment = dataSnapShot.getValue(Comment::class.java)
                    if (comment!!.thread_id == mThreadID) {
                        comment.comment_id = dataSnapShot.key.toString()
                        commentList.add(comment)
                    }
                }
                val commentAdapter = AlpasCommentAdapter(
                    this@ThreadDetailsActivity,
                    commentList,
                    this@ThreadDetailsActivity
                )
                rv_comment_list.adapter = commentAdapter
            }
        })
    }

    fun saveLike(like: Like, threadID: String) {
        mThreadID = threadID
        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
        reference.child(Constants.LIKE).push()
            .setValue(like)
            .addOnSuccessListener {
                FirestoreClass().getThreadDetailActivity(this, mThreadID, true)
            }
    }

    fun deleteLike(likeId: String, threadID: String) {
        mThreadID = threadID
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(Constants.LIKE).child(likeId)
        databaseReference.removeValue().addOnSuccessListener {
            FirestoreClass().getThreadDetailActivity(this, mThreadID, false)
        }
    }

    fun successThreadLike(thread: Thread, addLike: Boolean) {
        if (addLike) {
            notificationThread(Constants.NOTIFICATION_THREADS)
            val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
            reference.child(Constants.NOTIFICATION).push()
                .setValue(mNotificationData)
                .addOnSuccessListener {
                    threadHashMap[Constants.THREAD_lIKE] = thread.thread_like + 1
                    FirestoreClass().updateThreadLikeActivity(this, mThreadID, threadHashMap)
                }
        } else {
            val databaseReferenceNotification: DatabaseReference =
                FirebaseDatabase.getInstance().getReference(Constants.NOTIFICATION)
            databaseReferenceNotification.addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapShot: DataSnapshot in snapshot.children) {
                        val notif = dataSnapShot.getValue(NotificationData::class.java)
                        if (notif != null) {
                            if (notif.user_id == FirestoreClass().getCurrentUserID() && notif.topic_id == mThreadDetails.thread_id && notif.type == 1) {
                                mNotificationID = dataSnapShot.key.toString()

                            }
                        }
                    }
                    val databaseReference: DatabaseReference =
                        FirebaseDatabase.getInstance().getReference(Constants.NOTIFICATION)
                            .child(mNotificationID)
                    databaseReference.removeValue().addOnSuccessListener {
                        threadHashMap[Constants.THREAD_lIKE] = thread.thread_like - 1
                        FirestoreClass().updateThreadLikeActivity(
                            this@ThreadDetailsActivity,
                            mThreadID,
                            threadHashMap
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    fun updateLikeToThreadSuccess() {
        getThreadDetails()
    }

    fun saveLikeComment(like: Like, commentID: String) {
        mCommentID = commentID
        var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
        databaseReference.child(Constants.LIKE).push()
            .setValue(like)
            .addOnSuccessListener {
                databaseReference =
                    FirebaseDatabase.getInstance().getReference(Constants.COMMENT).child(commentID)
                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val comment = snapshot.getValue(Comment::class.java)
                        if (comment != null) {
                            successCommentLike(comment, true)
                        }
                    }
                })
            }
    }


    fun deleteLikeComment(likeId: String, commentID: String) {
        mCommentID = commentID
        var databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(Constants.LIKE).child(likeId)
        databaseReference.removeValue().addOnSuccessListener {
            databaseReference =
                FirebaseDatabase.getInstance().getReference(Constants.COMMENT).child(commentID)
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val comment = snapshot.getValue(Comment::class.java)
                    if (comment != null) {
                        successCommentLike(comment, false)
                    }
                }
            })
        }
    }

    fun successCommentLike(comment: Comment, addLike: Boolean) {
        if (addLike) {
            threadHashMap[Constants.COMMENT_LIKE] = comment.comment_like + 1
            val databaseReference: DatabaseReference =
                FirebaseDatabase.getInstance().getReference(Constants.COMMENT).child(mCommentID)
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    databaseReference.updateChildren(threadHashMap)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }
            )

            notificationThread(Constants.NOTIFICATION_COMMENTS_LIKE)
            val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
            reference.child(Constants.NOTIFICATION).push().setValue(mNotificationData)

        } else {
            val databaseReferenceNotification: DatabaseReference =
                FirebaseDatabase.getInstance().getReference(Constants.NOTIFICATION)
            databaseReferenceNotification.addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapShot: DataSnapshot in snapshot.children) {
                        val notif = dataSnapShot.getValue(NotificationData::class.java)
                        if (notif != null) {
                            if (notif.user_id == FirestoreClass().getCurrentUserID() && notif.topic_id == mThreadDetails.thread_id && notif.type == Constants.NOTIFICATION_COMMENTS_LIKE) {
                                mNotificationID = dataSnapShot.key.toString()
                            }
                        }
                    }

                    val databaseReferenceDeleteNotification: DatabaseReference =
                        FirebaseDatabase.getInstance().getReference(Constants.NOTIFICATION)
                            .child(mNotificationID)
                    databaseReferenceDeleteNotification.removeValue().addOnSuccessListener {

                        threadHashMap[Constants.COMMENT_LIKE] = comment.comment_like - 1
                        val databaseReference: DatabaseReference =
                            FirebaseDatabase.getInstance().getReference(Constants.COMMENT).child(
                                mCommentID
                            )
                        databaseReference.addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                databaseReference.updateChildren(threadHashMap)
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        }
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })



        }
    }

    private fun notificationThread(type: Int) {
        val userImage = this.getSharedPreferences(
            Constants.ALPAS_PREFERENCES,
            Context.MODE_PRIVATE
        ).getString(Constants.LOGGED_IN_USER_IMAGE, "")!!
        val topic = "/topics/${mThreadDetails.user_id}"
        var message = ""
        when (type) {
            1 -> {
                message = "$mUserName like your thread."
            }
            4 -> {
                message = "$mUserName comment on your thread."
            }
            5 -> {
                message = "$mUserName like your comment."
            }
        }
        mNotificationData = NotificationData(
            FirestoreClass().getCurrentUserID(),
            userImage,
            mThreadID,
            mThreadDetails.thread_title,
            message,
            type,
            mThreadDetails.user_id,
            System.currentTimeMillis()
        )
        PushNotification(
            mNotificationData,
            topic
        ).also {
            FirebaseService().sendNotification(it)
        }
    }
}