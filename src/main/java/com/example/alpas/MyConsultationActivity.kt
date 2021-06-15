package com.example.alpas

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alpas.adapters.AlpasMyConsultationAdapter
import com.example.alpas.adapters.AlpasProductListAdapter
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.Consultation
import com.example.alpas.models.Product
import kotlinx.android.synthetic.main.activity_my_consultation.*
import kotlinx.android.synthetic.main.activity_my_products.*
import kotlinx.android.synthetic.main.fragment_consultation.*

class MyConsultationActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_consultation)
        setupActionbar()
        getConsultationListFromFireStore()

        fb_add_consultation.setOnClickListener {
            val intent = Intent(this@MyConsultationActivity, AddConsultationActivity::class.java)
            startActivityForResult(intent,Constants.ADD_CONSULTATION_REQUEST_CODE)

        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.ADD_CONSULTATION_REQUEST_CODE) {
                getConsultationListFromFireStore()
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "To add the address.")
        }
    }

    private fun setupActionbar() {
        setSupportActionBar(toolbar_my_consultation_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_my_consultation_activity.setNavigationOnClickListener{ onBackPressed()}
    }

    private fun getConsultationListFromFireStore(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getConsultationList(this)
    }

    fun successConsultationListFromFireStore(consultationList: ArrayList<Consultation>){
        hideProgressDialog()
        if (rv_my_consultation_items != null) {
            if (consultationList.size > 0) {
                rv_my_consultation_items.visibility = View.VISIBLE
                tv_no_my_consultation_found.visibility = View.GONE

                rv_my_consultation_items.layoutManager = LinearLayoutManager(this)
                rv_my_consultation_items.setHasFixedSize(true)

                val adapterConsultation = AlpasMyConsultationAdapter(this, consultationList, this)
                rv_my_consultation_items.adapter = adapterConsultation
            } else {
                rv_my_consultation_items.visibility = View.GONE
                tv_no_my_consultation_found.visibility = View.VISIBLE
            }
        }
    }

    fun deleteConsultation (consultationID: String){
        showAlertDialogToDeleteConsultation(consultationID)
    }

    private fun showAlertDialogToDeleteConsultation(consultationID: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        builder.setMessage(resources.getString(R.string.delete_dialog_message_consultation))
        builder.setIcon(R.drawable.ic_baseline_clear)
        builder.setPositiveButton(resources.getString(R.string.delete_msg_yes)){ dialogInterface,_->
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().deleteConsultation(this,consultationID)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.delete_msg_no)) { dialogInterface,_->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun consultationDeleteSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this,
            resources.getString(R.string.consultation_delete_successful_msg),
            Toast.LENGTH_SHORT
        ).show()
        getConsultationListFromFireStore()
    }
}