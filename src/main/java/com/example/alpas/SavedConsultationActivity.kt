package com.example.alpas

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alpas.adapters.AlpasCartItemListAdapter
import com.example.alpas.adapters.AlpasSavedConsultationListAdapter
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.CartItem
import com.example.alpas.models.Consultation
import com.example.alpas.models.Product
import com.example.alpas.models.SavedConsultation
import kotlinx.android.synthetic.main.activity_cart_list.*
import kotlinx.android.synthetic.main.activity_saved_consultation.*

class SavedConsultationActivity : BaseActivity() {

    private lateinit var mConsultationList : ArrayList<Consultation>
    private lateinit var mSavedConsultationItems : ArrayList<SavedConsultation>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_consultation)
        setupActionbar()
    }

    override fun onResume() {
        super.onResume()
        getConsultationList()
    }
    private fun setupActionbar() {
        setSupportActionBar(toolbar_saved_consultation_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_saved_consultation_activity.setNavigationOnClickListener{ onBackPressed()}
    }

    private fun getConsultationList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllConsultationList(this@SavedConsultationActivity)
    }

    fun successConsultationListFromFireStore( consultationList: ArrayList<Consultation>){
        mConsultationList = consultationList
        getSavedConsultationList()
    }
    private fun getSavedConsultationList(){
        FirestoreClass().getSavedConsultationList(this@SavedConsultationActivity)
    }

    fun successSavedConsultationList ( savedConsultationList: ArrayList<SavedConsultation>){
        hideProgressDialog()
        mSavedConsultationItems = savedConsultationList

        if(mSavedConsultationItems.size > 0){
            rv_saved_consultation_items_list.visibility = View.VISIBLE
            tv_no_saved_consultation_items_found.visibility = View.GONE

            rv_saved_consultation_items_list.layoutManager = LinearLayoutManager (this@SavedConsultationActivity)
            rv_saved_consultation_items_list.setHasFixedSize(true)

            val  savedConsultationAdapter = AlpasSavedConsultationListAdapter(this@SavedConsultationActivity,mSavedConsultationItems,this)
            rv_saved_consultation_items_list.adapter = savedConsultationAdapter

        }
        else{
            rv_saved_consultation_items_list.visibility = View.GONE
            tv_no_saved_consultation_items_found.visibility = View.VISIBLE
        }
    }

    fun deleteSavedConsultation (savedConsultationID: String){
        showAlertDialogToDeleteConsultation(savedConsultationID)
    }

    private fun showAlertDialogToDeleteConsultation(savedConsultationID: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        builder.setMessage(resources.getString(R.string.delete_dialog_message_saved_consultation))
        builder.setIcon(R.drawable.ic_baseline_clear)
        builder.setPositiveButton(resources.getString(R.string.delete_msg_yes)){ dialogInterface,_->
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().deleteSavedConsultation(this,savedConsultationID)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.delete_msg_no)) { dialogInterface,_->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun savedConsultationDeleteSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this,
            resources.getString(R.string.consultation_delete_successful_msg),
            Toast.LENGTH_SHORT
        ).show()
        getSavedConsultationList()
    }
}