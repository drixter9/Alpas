package com.example.alpas

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.Address
import kotlinx.android.synthetic.main.activity_add_address.*
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_address_list.*
import kotlinx.android.synthetic.main.activity_register.*

class AddAddressActivity : BaseActivity() {
    //Add Address
    private var mAddressDetails: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)) {
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)
        }

        setupActionbar()

        if (mAddressDetails != null) {
            if (mAddressDetails!!.id.isNotEmpty()) {

                tv_add_address_title.text = resources.getString(R.string.title_edit_address)
                btn_add_address.text = resources.getString(R.string.btn_lbl_update)

                et_full_name_add_address.setText(mAddressDetails?.name)
                et_phone_number_add_address.setText(mAddressDetails?.mobile)
                et_address_add_address.setText(mAddressDetails?.address)
                et_zip_code_add_address.setText(mAddressDetails?.zipCode)
                et_additional_note_add_address.setText(mAddressDetails?.additional_note)

                when (mAddressDetails?.address_type) {
                    Constants.HOME -> {
                        rb_home_add_address.isChecked = true
                    }
                    Constants.OFFICE -> {
                        rb_office_add_address.isChecked = true
                    }
                    else -> {
                        rb_other_add_address.isChecked = true
                        til_other_details_add_address.visibility = View.VISIBLE
                        et_other_details_add_address.setText(mAddressDetails?.other_details)
                    }
                }
            }
        }

        btn_add_address.setOnClickListener {
            saveAddressToFireStore()
        }

        rg_address.setOnCheckedChangeListener { _, checkId ->
            if (checkId == R.id.rb_other_add_address) {
                til_other_details_add_address.visibility = View.VISIBLE
            } else {
                til_other_details_add_address.visibility = View.GONE
            }
        }

    }

    private fun setupActionbar() {
        setSupportActionBar(toolbar_add_address_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_add_address_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateAddressDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_full_name_add_address.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name),
                    true
                )
                false
            }
            TextUtils.isEmpty(et_phone_number_add_address.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_phone_number),
                    true
                )
                false
            }
            TextUtils.isEmpty(et_address_add_address.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address), true)
                false
            }
            TextUtils.isEmpty(et_zip_code_add_address.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            rb_other_add_address.isChecked && TextUtils.isEmpty(
                et_other_details_add_address.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_other_details),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    private fun saveAddressToFireStore() {
        val fullName: String = et_full_name_add_address.text.toString().trim { it <= ' ' }
        val phoneNumber: String = et_phone_number_add_address.text.toString().trim { it <= ' ' }
        val address: String = et_address_add_address.text.toString().trim { it <= ' ' }
        val zipCode: String = et_zip_code_add_address.text.toString().trim { it <= ' ' }
        val additionalNote: String =
            et_additional_note_add_address.text.toString().trim { it <= ' ' }
        val otherDetails: String = et_other_details_add_address.text.toString().trim { it <= ' ' }

        if (validateAddressDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))

            val addressType: String = when {
                rb_home_add_address.isChecked -> {
                    Constants.HOME
                }
                rb_office_add_address.isChecked -> {
                    Constants.OFFICE
                }
                else -> {
                    Constants.OTHER
                }
            }
            val addressModel = Address(
                FirestoreClass().getCurrentUserID(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails,
            )

            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
                FirestoreClass().updateAddress(
                    this@AddAddressActivity,
                    addressModel,
                    mAddressDetails!!.id
                )
            } else {
                FirestoreClass().addAddress(this@AddAddressActivity, addressModel)
            }
        }

    }

    fun addAddressSuccess() {
        hideProgressDialog()

        val notifySuccessMessage: String =
            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
                resources.getString(R.string.msg_your_address_updated_successfully)
            } else {
                resources.getString(R.string.msg_add_address_success)
            }

        Toast.makeText(
            this@AddAddressActivity,
            notifySuccessMessage,
            Toast.LENGTH_SHORT
        ).show()

        setResult(RESULT_OK)
        finish()
    }
}