package com.example.alpas

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alpas.adapters.AlpasMyThreadsListAdapter
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.Thread
import kotlinx.android.synthetic.main.activity_my_threads.*

class MyThreadsActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_threads)
        setupActionbar()
        getThreadsListFromFireStore()


        fb_add_threads.setOnClickListener {
            val intent = Intent(this@MyThreadsActivity, AddForumActivity::class.java)
            startActivityForResult(intent,Constants.ADD_THREADS_REQUEST_CODE)
        }
    }
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.ADD_THREADS_REQUEST_CODE) {
                getThreadsListFromFireStore()
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "To add the address.")
        }

    }
    private fun setupActionbar() {
        setSupportActionBar(toolbar_my_threads_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_my_threads_activity.setNavigationOnClickListener{ onBackPressed()}
    }

    private fun getThreadsListFromFireStore() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getThreadsList(this)
    }

    fun successThreadsListFromFireStore(threadsList: ArrayList<Thread>) {
        hideProgressDialog()

        if (rv_my_threads_items != null) {
            if (threadsList.size > 0) {
                rv_my_threads_items.visibility = View.VISIBLE
                tv_no_my_threads_found.visibility = View.GONE

                rv_my_threads_items.layoutManager = LinearLayoutManager(this)
                rv_my_threads_items.setHasFixedSize(true)

                val adapterThreads = AlpasMyThreadsListAdapter(this,threadsList, this)
                rv_my_threads_items.adapter = adapterThreads
            } else {
                rv_my_threads_items.visibility = View.GONE
                tv_no_my_threads_found.visibility = View.VISIBLE
            }
        }
    }

    fun deleteThread (threadID: String){
        showAlertDialogToDeleteConsultation(threadID)
    }

    private fun showAlertDialogToDeleteConsultation(threadID: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        builder.setMessage(resources.getString(R.string.delete_dialog_message_thread))
        builder.setIcon(R.drawable.ic_baseline_clear)
        builder.setPositiveButton(resources.getString(R.string.delete_msg_yes)){ dialogInterface,_->
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().deleteThread(this,threadID)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.delete_msg_no)) { dialogInterface,_->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun threadDeleteSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this,
            resources.getString(R.string.thread_delete_successful_msg),
            Toast.LENGTH_SHORT
        ).show()
        getThreadsListFromFireStore()
    }

}