package com.example.alpas

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.Consultation
import com.example.alpas.models.Product
import com.example.alpas.utilsAlpas.GlideLoader
import kotlinx.android.synthetic.main.activity_add_address.*
import kotlinx.android.synthetic.main.activity_add_consultation.*
import kotlinx.android.synthetic.main.activity_add_product.*
import java.io.IOException

class AddConsultationActivity : BaseActivity(), View.OnClickListener {

    private var mSelectedImageFileUri : Uri? = null
    private var mConsultationImageURL : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_consultation)
        setEducationDropdown()
        setupActionbar()
        iv_add_image_consultation.setOnClickListener(this)
        btn_add_consultation_submit.setOnClickListener(this)

    }

    private fun setupActionbar() {
        setSupportActionBar(toolbar_add_consultation_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_add_consultation_activity.setNavigationOnClickListener{ onBackPressed()}
    }

    override fun onClick(v: View?) {
        if (v !=null){

            when (v.id){
                R.id.iv_add_image_consultation -> {
                    if (ContextCompat.checkSelfPermission(
                            this, android.Manifest.permission.READ_EXTERNAL_STORAGE
                        )== PackageManager.PERMISSION_GRANTED)
                    {
                        Constants.showImageChooser(this@AddConsultationActivity)
                    }
                    else{
                        ActivityCompat.requestPermissions(
                            this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
               }
                R.id.btn_add_consultation_submit -> {
                    if(validateAddConsultationDetails()){
                        uploadConsultationImage()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Constants.showImageChooser(this@AddConsultationActivity)
        }
        else{
            Toast.makeText(
                this,
                resources.getString(R.string.read_storage_permission_denied),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE)    {
                if (data!=null){
                    try {
                        iv_add_image_consultation.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_edit))
                        try {
                            mSelectedImageFileUri = data.data!!
                            GlideLoader(this).loadUserImage(mSelectedImageFileUri!!,iv_consultation_image)
                        } catch (e: IOException){
                            e.printStackTrace()
                        }
                    }
                    catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(
                            this@AddConsultationActivity,
                            resources.getString(R.string.image_selector_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun setEducationDropdown() {
        // list of spinner items
        val degreeList = mutableListOf(
            "Bachelor",
            "Master",
            "Doctoral"
        )
        val programOfStudyList = mutableListOf(
            "Chemical Engineering",
            "Chemical Process Technology",
            "Civil Engineering",
            "Computer Engineering",
            "Electrical Engineering",
            "Electronics Engineering",
            "Industrial Engineering",
            "Mechanical Engineering",
            "Mining Engineering",
            "Geology",
            "Petroleum Engineering",
        )

        degreeList.add(0, Constants.SELECT_DEGREE_TYPE)
        programOfStudyList.add(0, Constants.SELECT_PROGRAM_OF_STUDY)
        // initialize an array adapter for spinner

        dropdownAdapter(degreeList,s_degree)
        dropdownAdapter(programOfStudyList,s_program_of_study)
    }

    private fun dropdownAdapter(list: MutableList<String>,spinner: Spinner){
        val context = this
        val adapter: ArrayAdapter<String> = object : ArrayAdapter<String>(
            context,
            android.R.layout.simple_spinner_dropdown_item,
            list
        ) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view: TextView = super.getDropDownView(
                    position,
                    convertView,
                    parent
                ) as TextView

                // set selected item style
                if (position == spinner.selectedItemPosition && position != 0) {
                    view.background = ColorDrawable(Color.parseColor("#F1F1F1"))
                }
                // make hint item color gray
                if (position == 0) {
                    view.setTextColor(Color.LTGRAY)
                }
                else{
                    view.setTextColor(Color.BLACK)
                }
                return view
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view:TextView = super.getView(position, convertView, parent) as TextView

                if (position == 0){
                    view.setTextColor(Color.LTGRAY)
                }
                return view
            }
            override fun isEnabled(position: Int): Boolean {
                // disable first item
                // first item is display as hint
                return position != 0
            }

        }

        // finally, data bind spinner with adapter
        spinner.adapter = adapter
    }

    private fun validateAddConsultationDetails(): Boolean {
        return when {
            mSelectedImageFileUri==null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_consultation_image), true)
                false
            }

            TextUtils.isEmpty(et_add_consultation_title.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_consultation_title), true)
                false
            }
            s_degree.selectedItem.equals(Constants.SELECT_DEGREE_TYPE) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_degree_type), true)
                false
            }
            s_program_of_study.selectedItem.equals(Constants.SELECT_PROGRAM_OF_STUDY) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_program_of_study), true)
                false
            }
            TextUtils.isEmpty(et_add_consultation_price.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_consultation_price), true)
                false
            }
            TextUtils.isEmpty(et_add_consultation_description.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_consultation_description), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun uploadConsultationImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri,Constants.CONSULTATION_IMAGE)
    }

    fun imageUploadSuccess (imageURL:String){
        mConsultationImageURL = imageURL
        uploadConsultationDetails()
    }

    private fun uploadConsultationDetails(){
        val username = this.getSharedPreferences(Constants.ALPAS_PREFERENCES,
            Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_USERNAME,"")!!
        val userImage = this.getSharedPreferences(Constants.ALPAS_PREFERENCES,
            Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_USER_IMAGE,"")!!

        val consultation = Consultation(
            FirestoreClass().getCurrentUserID(),
            username,
            userImage,
            et_add_consultation_title.text.toString().trim{it <= ' '},
            s_degree.selectedItem.toString(),
            s_program_of_study.selectedItem.toString(),
            et_add_consultation_price.text.toString().trim { it <= ' '},
            et_add_consultation_description.text.toString().trim { it <= ' '},
            mConsultationImageURL,
        )
        FirestoreClass().uploadConsultationDetails(this, consultation)
    }
    fun  consultationUploadSuccessful(){
        hideProgressDialog()
        Toast.makeText(
            this@AddConsultationActivity,
            resources.getString(R.string.msg_consulatation_upload_success),
            Toast.LENGTH_SHORT
        ).show()
        setResult(RESULT_OK)
        finish()
    }
}