package com.example.alpas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.CartItem
import com.example.alpas.models.Consultation
import com.example.alpas.models.Product
import com.example.alpas.models.SavedConsultation
import com.example.alpas.utilsAlpas.GlideLoader
import kotlinx.android.synthetic.main.activity_add_address.*
import kotlinx.android.synthetic.main.activity_consultation_details.*
import kotlinx.android.synthetic.main.activity_product_details.*

class ConsultationDetailsActivity : BaseActivity() {

    private var mConsultationID: String = ""
    private lateinit var mConsultationDetails: Consultation
    private var mConsultationOwnerId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultation_details)
        setupActionbar()

        if (intent.hasExtra(Constants.EXTRA_CONSULTATION_ID)) {
            mConsultationID = intent.getStringExtra(Constants.EXTRA_CONSULTATION_ID)!!
        }
        if (intent.hasExtra(Constants.EXTRA_CONSULTATION_OWNER_ID)) {
            mConsultationOwnerId = intent.getStringExtra(Constants.EXTRA_CONSULTATION_OWNER_ID)!!
        }

        if (FirestoreClass().getCurrentUserID() == mConsultationOwnerId) {
            btn_save_consultation.visibility = View.GONE
            btn_go_to_save_consultation.visibility = View.GONE
        } else {
            btn_save_consultation.visibility = View.VISIBLE
        }

        btn_save_consultation.setOnClickListener {
            addToSavedConsultation()
        }

        btn_go_to_save_consultation.setOnClickListener {
            startActivity(Intent(this@ConsultationDetailsActivity, SavedConsultationActivity::class.java))
        }
        getConsultationDetails()

        iv_message_consultation_owner.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS_CHAT,mConsultationOwnerId)
            startActivity(intent)
        }
    }

    private fun setupActionbar() {
        setSupportActionBar(toolbar_consultation_details)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_consultation_details.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getConsultationDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getConsultationDetails(this, mConsultationID)
    }

    fun consultationDetailsSuccess(consultation: Consultation) {
        mConsultationDetails = consultation
        GlideLoader(this@ConsultationDetailsActivity).loadProductImage(
            consultation.image,
            iv_consultation_details_image
        )
        GlideLoader(this@ConsultationDetailsActivity).loadUserImage(
            consultation.user_image,
            iv_user_image
        )
        tv_consultation_name.text = consultation.user_name
        tv_consultation_title.text = consultation.consultation_title
        tv_consultation_education.text = " ${consultation.consultation_degree} of ${consultation.consultation_program_of_study}"
        tv_consultation_price.text = "Php ${consultation.consultation_price}"
        tv_consultation_description.text = consultation.consultation_description

        if (FirestoreClass().getCurrentUserID() == consultation.user_id) {
            hideProgressDialog()
        } else {
            FirestoreClass().checkIfItemExistInSaveConsultation(this, mConsultationID)
        }
    }

    fun consultationExistInSaved(){
        hideProgressDialog()
        btn_save_consultation.visibility = View.GONE
        btn_go_to_save_consultation.visibility = View.VISIBLE
    }
    private fun addToSavedConsultation(){
        val yourSavedItems = SavedConsultation(
            FirestoreClass().getCurrentUserID(),
            mConsultationOwnerId,
            mConsultationDetails.user_name,
            mConsultationDetails.user_image,
            mConsultationDetails.consultation_title,
            mConsultationDetails.consultation_degree,
            mConsultationDetails.consultation_program_of_study,
            mConsultationDetails.consultation_price,
            mConsultationDetails.consultation_description,
            mConsultationDetails.image,
            mConsultationID,
        )
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addToSavedItems(this, yourSavedItems)
    }

    fun addToSavedSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this@ConsultationDetailsActivity,
            resources.getString(R.string.msg_add_to_saved_success),
            Toast.LENGTH_SHORT
        ).show()
        btn_save_consultation.visibility = View.GONE
        btn_go_to_save_consultation.visibility = View.VISIBLE
    }

}