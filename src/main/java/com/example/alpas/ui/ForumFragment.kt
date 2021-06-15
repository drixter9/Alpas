package com.example.alpas.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alpas.*
import com.example.alpas.R
import com.example.alpas.adapters.AlpasConsultationListAdapter
import com.example.alpas.adapters.AlpasThreadsAllListAdapter
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.*
import com.example.alpas.utilsAlpas.RetrofitInstance
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_consultation_details.*
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.activity_thread_details.*
import kotlinx.android.synthetic.main.fragment_consultation.*
import kotlinx.android.synthetic.main.fragment_forum.*
import kotlinx.android.synthetic.main.fragment_product_list.*
import kotlinx.android.synthetic.main.item_forum_list.*
import kotlinx.android.synthetic.main.item_forum_list.btn_go_to_save_forum
import kotlinx.android.synthetic.main.item_forum_list.btn_save_forum_list
import kotlinx.android.synthetic.main.item_forum_list.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class ForumFragment : BaseFragment() {

    private lateinit var mSavedThreadDetails: ArrayList<SavedThread>
    private lateinit var mThreadItemsList: ArrayList<Thread>
    private var mThreadID = ""
    private var mLikeID = ""
    private var mNotificationID = ""
    private var mTitle = ""
    private var mThreadUserID = ""
    private var threadHashMap = HashMap<String, Any>()
    lateinit var adapter: AlpasThreadsAllListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_forum, container, false)
        return root
    }

    override fun onResume() {
        super.onResume()

        getThreadItemList()

        fb_add_thread.setOnClickListener {
            val intent = Intent(activity, MyThreadsActivity::class.java)
            startActivity(intent)
        }

        ib_save_thread.setOnClickListener {
            val intent = Intent(activity, SaveThreadsActivity::class.java)
            startActivity(intent)
        }
        ib_chat_forum.setOnClickListener {
            val intent = Intent(activity, MessageActivity::class.java)
            startActivity(intent)
        }
        sv_forum_search.setQuery("",false)
        forum_fragment.requestFocus()

        if (sv_forum_search != null) {
            sv_forum_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    sv_forum_search.clearFocus()
                    return true
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter.filter(newText)
                    return true
                }
            })
        }

    }

    private fun getThreadItemList(){
        FirestoreClass().getThreadItemsList(this@ForumFragment)
    }
    fun successThreadItemsList(threadList: ArrayList<Thread>){
        mThreadItemsList = threadList
        checkSavedThread()
    }

    private fun checkSavedThread(){
        FirestoreClass().getSavedThreadsListToCheck(this)
    }

    fun successGettingSavedThreads(savedThread: ArrayList<SavedThread>){
        mSavedThreadDetails = savedThread
        mThreadItemsList.sortByDescending { it.thread_date }
        if (rv_thread_list != null) {
            if (mThreadItemsList.size > 0) {
                rv_thread_list.visibility = View.VISIBLE
                tv_no_threads_items_found.visibility = View.GONE

                rv_thread_list.layoutManager = LinearLayoutManager(requireActivity())

                rv_thread_list.setHasFixedSize(true)

                adapter = AlpasThreadsAllListAdapter(
                    requireActivity(),
                    mThreadItemsList,
                    mSavedThreadDetails,
                    this
                )
                rv_thread_list.adapter = adapter

            } else {
                rv_thread_list.visibility = View.GONE
                tv_no_threads_items_found.visibility = View.VISIBLE
            }
        }
    }

    fun saveThread(savedThread: SavedThread){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addToSavedThreadsItems(this, savedThread)
    }
    fun addToSavedSuccess(){
        hideProgressDialog()
        Toast.makeText(
            requireActivity(),
            resources.getString(R.string.msg_add_to_saved_success_thread),
            Toast.LENGTH_SHORT
        ).show()
        btn_save_forum_list.visibility = View.GONE
        btn_go_to_save_forum.visibility = View.VISIBLE
        getThreadItemList()
    }

    fun saveLike(like: Like,threadID:String,title:String,threadUserID:String,addLike:Boolean){
        mThreadID = threadID
        mTitle = title
        mThreadUserID = threadUserID
        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
        reference.child(Constants.LIKE).push()
            .setValue(like)
            .addOnSuccessListener {
                FirestoreClass().getThreadDetailFragment(this,mThreadID,addLike)
        }
    }


    fun deleteLike(likeId: String,threadID:String,addLike:Boolean,notificationID:String){
        showProgressDialog(resources.getString(R.string.please_wait))
        mThreadID = threadID
        mNotificationID = notificationID
        mLikeID=likeId
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(Constants.LIKE).child(mLikeID)
        databaseReference.removeValue().addOnSuccessListener {
            FirestoreClass().getThreadDetailFragment(this,mThreadID,addLike)
        }
    }
    fun successThreadLike(thread:Thread, addLike:Boolean){
        if (addLike) {
            val username = requireActivity().getSharedPreferences(Constants.ALPAS_PREFERENCES,
                Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_USERNAME,"")!!
            val userImage = requireActivity().getSharedPreferences(Constants.ALPAS_PREFERENCES,
                Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_USER_IMAGE,"")!!

            val topic = "/topics/${mThreadUserID}"
            val message= "$username like your thread."
            val notification = NotificationData(FirestoreClass().getCurrentUserID(),userImage,mThreadID,mTitle,message,Constants.NOTIFICATION_THREADS,mThreadUserID,System.currentTimeMillis())
            PushNotification(
                 notification,
                topic).also {
                FirebaseService().sendNotification(it)
            }
            val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
            reference.child(Constants.NOTIFICATION).push()
                .setValue(notification)
                .addOnSuccessListener {
                    threadHashMap[Constants.THREAD_lIKE] = thread.thread_like + 1
                    FirestoreClass().updateThreadLike(this, mThreadID, threadHashMap)
                }
        }

        else{
            val databaseReference: DatabaseReference =
                FirebaseDatabase.getInstance().getReference(Constants.NOTIFICATION).child(mNotificationID)
            databaseReference.removeValue().addOnSuccessListener {
                threadHashMap[Constants.THREAD_lIKE]= thread.thread_like - 1
                FirestoreClass().updateThreadLike(this@ForumFragment,mThreadID,threadHashMap)
            }
        }
    }
    fun updateLikeToThreadSuccess(){
        hideProgressDialog()
        getThreadItemList()
    }


}