package com.example.alpas

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alpas.adapters.AlpasAddressListAdapter
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.Address
import com.myshoppal.utils.SwipeToDelete
import com.myshoppal.utils.SwipeToEdit
import kotlinx.android.synthetic.main.activity_address_list.*
import kotlinx.android.synthetic.main.activity_settings.*

class AddressListActivity : BaseActivity() {


    private var mSelectedAddress: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)
        setupActionbar()


        tv_add_address.setOnClickListener {
            val intent = Intent(this@AddressListActivity, AddAddressActivity::class.java)
            startActivityForResult(intent,Constants.ADD_ADDRESS_REQUEST_CODE)
        }

        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)){
            mSelectedAddress = intent.getBooleanExtra(Constants.EXTRA_SELECTED_ADDRESS,false)
        }

        if (mSelectedAddress){
            tv_title_address_list.text = resources.getString(R.string.title_select_address)

        }
        getAddressList()

    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.ADD_ADDRESS_REQUEST_CODE) {
                getAddressList()
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "To add the address.")
        }
    }

    private fun setupActionbar() {
        setSupportActionBar(toolbar_address_list_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_address_list_activity.setNavigationOnClickListener{ onBackPressed()}
    }

    fun successAddressListFromFirestore(addressList: ArrayList<Address>){
        hideProgressDialog()

            if (addressList.size > 0 ){

                rv_address_list.visibility = View.VISIBLE
                tv_no_address_found.visibility = View.GONE

                rv_address_list.layoutManager = LinearLayoutManager(this)
                rv_address_list.setHasFixedSize(true)

                val addressAdapter = AlpasAddressListAdapter(this , addressList,mSelectedAddress)
                rv_address_list.adapter = addressAdapter


                if (!mSelectedAddress ){

                    val editToSwipeHandler = object : SwipeToEdit(this){
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            val adapter = rv_address_list.adapter as AlpasAddressListAdapter
                            adapter.notifyEditItem(
                                this@AddressListActivity,
                                viewHolder.adapterPosition
                            )
                        }

                    }

                    val editItemTouchHelper = ItemTouchHelper(editToSwipeHandler)
                    editItemTouchHelper.attachToRecyclerView(rv_address_list)

                    val deleteToSwipeHandler = object : SwipeToDelete(this) {
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            showProgressDialog(resources.getString(R.string.please_wait))
                            FirestoreClass().deleteAddress(this@AddressListActivity,
                                addressList[viewHolder.adapterPosition].id)

                        }
                    }
                    val deleteItemTouchHelper = ItemTouchHelper(deleteToSwipeHandler)
                    deleteItemTouchHelper.attachToRecyclerView(rv_address_list)

                }

            }
            else{
                rv_address_list.visibility = View.GONE
                tv_no_address_found.visibility = View.VISIBLE
            }


    }

    private fun getAddressList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAddressesList(this)
    }

    fun deleteAddressSuccess(){
        hideProgressDialog()

        Toast.makeText(
            this@AddressListActivity,
            resources.getString(R.string.msg_your_address_is_successfully_deleted),
            Toast.LENGTH_SHORT
        ).show()
        getAddressList()
    }
}