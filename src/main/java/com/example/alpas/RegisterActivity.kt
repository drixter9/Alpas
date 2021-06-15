package com.example.alpas

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.UserAlpas
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*
import java.util.regex.Pattern


class RegisterActivity : BaseActivity() {
    private val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^" +  "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +  //any letter
                "(?=.*[!#$%&'()*+,-./:;<=>?@^_`{|}~])" +  //at least 1 special character
                "(?=\\S+$)" +  //no white spaces
                ".{6,}" +  //at least 4 characters
                "$"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setupActionbar()

        tv_terms_and_conditions.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, TermsAndConditionsActivity::class.java))
        }

        et_birthday.setOnClickListener  { view->
            clickDatePicker(view)
        }

        tv_go_to_login_register.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }
        btn_register.setOnClickListener{
            checkEmailExistsOrNot()
        }
    }
    private fun setupActionbar() {
        setSupportActionBar(toolbar_register_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
            actionBar.setDisplayShowTitleEnabled(false)
        }
        toolbar_register_activity.setNavigationOnClickListener{
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }
    }
   private fun clickDatePicker(view: View) {
            val myCalendar = Calendar.getInstance()
            val year = myCalendar.get(Calendar.YEAR)
            val month = myCalendar.get(Calendar.MONTH)
            val day = myCalendar.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(
                this,
                { _, selectedyear, selectedmonth, selecteddayOfMonth ->
                    val selectedDate = "$selecteddayOfMonth/${selectedmonth + 1}/$selectedyear"
                    val birthday = findViewById<TextView>(R.id.et_birthday)
                    birthday.text = selectedDate
                },
                year,
                month,
                day
            ).show()
        }
    private fun validateRegistrationDetails(): Boolean{
        return when{
            TextUtils.isEmpty(et_first_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_first_name), true)
                false
            }
            TextUtils.isEmpty(et_last_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_last_name), true)
                false
            }
            TextUtils.isEmpty(et_birthday.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_birthday), true)
                false
            }
            TextUtils.isEmpty(et_create_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_email), true)
                false
            }
            TextUtils.isEmpty(et_create_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password), true)
                false
            }

            et_create_password.text.toString().trim { it <= ' ' }.length < 6 -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_length), true)
                false
            }


            !PASSWORD_PATTERN.matcher(et_create_password.text.toString()).matches() -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_pattern), true)
                false
            }

            TextUtils.isEmpty(et_confirm_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_confirm_password), true)
                false
            }
            et_confirm_password.text.toString().trim{ it <= ' '} != et_create_password.text.toString() -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_confirm_password_mismatch),
                    true
                )
                false
            }
            !cb_terms_and_condition.isChecked -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_agree_terms_and_conditions),
                    true
                )
                false
            }
             !Patterns.EMAIL_ADDRESS.matcher(et_create_email.text.toString()).matches() -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_email_patter), true)
                false
            }

            else ->{
                true
            }
        }
    }

    private fun checkEmailExistsOrNot() {
        if (validateRegistrationDetails()){
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(et_create_email.text.toString()).addOnCompleteListener { task ->
            if (task.result?.signInMethods.toString() == "[]") {
               registerUser()
            } else {
                showErrorSnackBar("Email is already taken.", true)
            }
        }.addOnFailureListener { e ->
            showErrorSnackBar(e.printStackTrace().toString(), true)
        }
        }
    }
    private fun registerUser(){
            showProgressDialog(resources.getString(R.string.please_wait))
            val email : String = et_create_email.text.toString().trim { it <= ' '}
            val password : String = et_create_password.text.toString().trim { it <= ' '}

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result!!.user!!
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            val user = UserAlpas(
                                firebaseUser.uid,
                                et_first_name.text.toString().trim { it <= ' ' },
                                et_last_name.text.toString().trim { it <= ' ' },
                                et_create_email.text.toString().trim { it <= ' ' },
                                et_birthday.text.toString().trim { it <= ' ' }
                            )
                            //Register the user in the FireStore With the Information
                            FirestoreClass().registerUser(this@RegisterActivity, user)
                        } else {
                            hideProgressDialog()
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
                }
    }
   fun userSendVerification(){
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                sendVerificationEmail();
            } else {
                showErrorSnackBar("Send Verification Failed", true)
            }
    }

    private fun sendVerificationEmail() {
        val user = FirebaseAuth.getInstance().currentUser
        user!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // email sent
                    // after email is sent just logout the user and finish this activity
                    hideProgressDialog()
                    Toast.makeText(
                        this@RegisterActivity,
                        resources.getString(R.string.register_success),
                        Toast.LENGTH_LONG
                    ).show()
                    FirebaseAuth.getInstance().signOut()
                    finish()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))

                } else {
                    // email not sent, so display message and restart the activity or do whatever you wish to do
                    hideProgressDialog()
                    //restart this activity
                    FirebaseAuth.getInstance().signOut()
                    showErrorSnackBar("Send Verification Failed", true)
                }
            }
    }
}