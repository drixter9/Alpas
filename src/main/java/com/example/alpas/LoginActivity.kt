package com.example.alpas

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.UserAlpas
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            // User is signed in (getCurrentUser() will be null if not signed in)
            val intent = Intent(this, DashboardActivity::class.java);
            startActivity(intent);
            finish()
        }
        tv_register.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_login.setOnClickListener {
            if (auth.currentUser != null) {
                auth.signOut()
                loginRegisteredUser()
            } else {
                loginRegisteredUser()
            }

        }

        tv_forgot_password.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }


    }


    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_email), true)
                false
            }
            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun loginRegisteredUser() {

        if (validateLoginDetails()) {

            showProgressDialog(resources.getString(R.string.please_wait))

            val email = et_email.text.toString().trim { it <= ' ' }
            val password = et_password.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FirebaseAuth.getInstance().currentUser!!.reload()
                            if (!FirebaseAuth.getInstance().currentUser!!.isEmailVerified) {
                                hideProgressDialog()
                                Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT)
                                    .show()
                                FirebaseAuth.getInstance().signOut()
                            } else {
                                if (FirebaseAuth.getInstance().currentUser != null){
                                    FirestoreClass().getUserDetailsLogin(this@LoginActivity)
                                }
                            }
                    } else {
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    fun userLoggedInSuccess(user: UserAlpas) {
        hideProgressDialog()
        if (user.profileCompleted == 0) {
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
            finish()
        } else {
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
            finish()
        }

    }
}
