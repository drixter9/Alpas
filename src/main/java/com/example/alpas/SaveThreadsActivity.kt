package com.example.alpas

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alpas.adapters.AlpasSavedConsultationListAdapter
import com.example.alpas.adapters.AlpasSavedThreadListAdapter
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.SavedConsultation
import com.example.alpas.models.SavedThread
import kotlinx.android.synthetic.main.activity_save_threads.*
import kotlinx.android.synthetic.main.activity_saved_consultation.*
import com.example.alpas.models.Thread

class SaveThreadsActivity : BaseActivity() {

    private lateinit var mThreadList : ArrayList<Thread>
    private lateinit var mSavedThreadItems : ArrayList<SavedThread>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_threads)
        setupActionbar()
        getThreadList()
    }

    private fun setupActionbar() {
        setSupportActionBar(toolbar_saved_threads_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_saved_threads_activity.setNavigationOnClickListener{ onBackPressed()}
    }

    private fun getThreadList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllThreadList(this@SaveThreadsActivity)
    }

    fun successThreadsListFromFireStore( threadList: ArrayList<Thread>){
        hideProgressDialog()
        mThreadList = threadList
        getSavedThreadList()
    }

    private fun getSavedThreadList(){
        FirestoreClass().getSavedThreadsList(this@SaveThreadsActivity)
    }

    fun successSavedThreadList ( savedThreadList: ArrayList<SavedThread>){
        hideProgressDialog()
        mSavedThreadItems = savedThreadList

        if(mSavedThreadItems.size > 0){
            rv_saved_threads_items_list.visibility = View.VISIBLE
            tv_no_saved_threads_items_found.visibility = View.GONE

            rv_saved_threads_items_list.layoutManager = LinearLayoutManager (this)
            rv_saved_threads_items_list.setHasFixedSize(true)

            val  savedThreadsAdapter = AlpasSavedThreadListAdapter(this,mSavedThreadItems,this)
            rv_saved_threads_items_list.adapter = savedThreadsAdapter
        }
        else{
            rv_saved_threads_items_list.visibility = View.GONE
            tv_no_saved_threads_items_found.visibility = View.VISIBLE
        }
    }
    fun deleteSavedThread (savedThreadID: String){
        showAlertDialogToDeleteThread(savedThreadID)
    }

    private fun showAlertDialogToDeleteThread(savedThreadID: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        builder.setMessage(resources.getString(R.string.delete_dialog_message_saved_thread))
        builder.setIcon(R.drawable.ic_baseline_clear)
        builder.setPositiveButton(resources.getString(R.string.delete_msg_yes)){ dialogInterface,_->
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().deleteSavedThread(this,savedThreadID)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.delete_msg_no)) { dialogInterface,_->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun savedThreadDeleteSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this,
            resources.getString(R.string.thread_delete_successful_msg),
            Toast.LENGTH_SHORT
        ).show()
        getSavedThreadList()
    }
}