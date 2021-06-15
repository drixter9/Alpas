package com.example.alpas

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.Consultation
import com.example.alpas.models.Thread
import com.example.alpas.utilsAlpas.GlideLoader
import kotlinx.android.synthetic.main.activity_add_consultation.*
import kotlinx.android.synthetic.main.activity_add_forum.*
import java.io.IOException
import java.util.*


class AddForumActivity : BaseActivity(), View.OnClickListener {

    private var mSelectedImageFileUri : Uri? = null
    private var mThreadImageURL : String = ""

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_forum)
        setupActionbar()
        setFieldOfStudyDropdown()

        iv_add_image_forum_icon.setOnClickListener(this)
        btn_add_thread_to_forum.setOnClickListener(this)

        et_add_forum_description.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                view.parent.requestDisallowInterceptTouchEvent(false)
            }
            return@setOnTouchListener false
        }
    }

    private fun setupActionbar() {
        setSupportActionBar(toolbar_add_forum_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_add_forum_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        if (v !=null){

            when (v.id){
                R.id.iv_add_image_forum_icon -> {
                    if (ContextCompat.checkSelfPermission(
                            this, android.Manifest.permission.READ_EXTERNAL_STORAGE
                        )== PackageManager.PERMISSION_GRANTED)
                    {
                        Constants.showImageChooser(this@AddForumActivity)
                    }
                    else{
                        ActivityCompat.requestPermissions(
                            this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }
                R.id.btn_add_thread_to_forum-> {
                    if(validateAddThreadDetails()){
                        uploadThreadImage()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Constants.showImageChooser(this@AddForumActivity)
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
                        iv_add_image_forum_icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_edit))
                        try {
                            mSelectedImageFileUri = data.data!!
                            GlideLoader(this).loadUserImage(mSelectedImageFileUri!!,iv_add_forum_image)
                        } catch (e: IOException){
                            e.printStackTrace()
                        }
                    }
                    catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(
                            this@AddForumActivity,
                            resources.getString(R.string.image_selector_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun setFieldOfStudyDropdown() {
        // list of spinner items
        val fieldOfStudyList = mutableListOf(
            "Chemical",
            "Civil",
            "Computer",
            "Electrical",
            "Electronics",
            "Industrial",
            "Mechanical",
            "Mining",
            "Geology",
            "Petroleum",
        )
        fieldOfStudyList.add(0, Constants.SELECT_FIELD_OF_STUDY)
        // initialize an array adapter for spinner
        dropdownAdapter(fieldOfStudyList,add_forum_field_of_study_degree)
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
                val view: TextView = super.getView(position, convertView, parent) as TextView

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

    private fun validateAddThreadDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_add_forum_title.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_thread_title), true)
                false
            }

             add_forum_field_of_study_degree.selectedItem.equals(Constants.SELECT_FIELD_OF_STUDY) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_field_of_study), true)
                false
            }
            TextUtils.isEmpty(et_add_forum_topic.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_thread_topic), true)
                false
            }
            TextUtils.isEmpty(et_add_forum_description.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_thread_description), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun uploadThreadImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if (mSelectedImageFileUri != null){
            FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri,Constants.FORUM_IMAGE)
        }
        else{
            uploadThreadDetails()
        }
    }

    fun imageUploadSuccess (imageURL:String){
        mThreadImageURL = imageURL
        uploadThreadDetails()
    }

    private fun uploadThreadDetails() {
        val username = this.getSharedPreferences(Constants.ALPAS_PREFERENCES,
            Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_USERNAME,"")!!
        val userImage = this.getSharedPreferences(Constants.ALPAS_PREFERENCES,
            Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_USER_IMAGE,"")!!
        val thread = Thread(
            FirestoreClass().getCurrentUserID(),
            username,
            userImage,
            et_add_forum_title.text.toString().trim{it <= ' '},
            add_forum_field_of_study_degree.selectedItem.toString(),
            et_add_forum_topic.text.toString().trim { it <= ' '},
            et_add_forum_description.text.toString().trim { it <= ' '},
            mThreadImageURL,
            Constants.DEFAULT_LIKE_QUANTITY,
            Constants.DEFAULT_COMMENT_QUANTITY,
            Calendar.getInstance().timeInMillis,
        )
        FirestoreClass().uploadThreadDetails(this, thread)
    }

    fun threadUploadSuccessful(){
        hideProgressDialog()
        Toast.makeText(
            this@AddForumActivity,
            resources.getString(R.string.msg_thread_upload_success),
            Toast.LENGTH_SHORT
        ).show()
        setResult(RESULT_OK)
        finish()
    }
}

