package com.example.alpas.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.alpas.*
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.UserAlpas
import com.example.alpas.utilsAlpas.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.btn_logout
import kotlinx.android.synthetic.main.fragment_profile.ll_Address

class ProfileFragment : BaseFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        return root
    }

    override fun onResume() {
        super.onResume()
        userInfoProfile(FirestoreClass().getCurrentUserID())
        ib_setting.setOnClickListener {
            startActivity(Intent(activity, SettingsActivity::class.java))
        }

        btn_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        ll_Address.setOnClickListener {
            val intent = Intent(requireActivity(), AddressListActivity::class.java)
            startActivity(intent)
        }

        btn_profile_threads.setOnClickListener {
            val intent = Intent(requireActivity(), MyThreadsActivity::class.java)
            startActivity(intent)
        }
        btn_profile_saved_Threads.setOnClickListener {
            val intent = Intent(requireActivity(), SaveThreadsActivity::class.java)
            startActivity(intent)
        }
        btn_profile_products.setOnClickListener {
            val intent = Intent(requireActivity(), MyProductsActivity::class.java)
            startActivity(intent)
        }
        btn_profile_Orders.setOnClickListener {
            val intent = Intent(requireActivity(), MyOrderActivity::class.java)
            startActivity(intent)
        }
        btn_profile_Sold.setOnClickListener {
            val intent = Intent(requireActivity(), MySoldProductsActivity::class.java)
            startActivity(intent)
        }
        btn_profile_my_consultation.setOnClickListener {
            val intent = Intent(requireActivity(), MyConsultationActivity::class.java)
            startActivity(intent)
        }
        btn_profile_saved_consultation.setOnClickListener {
            val intent = Intent(requireActivity(), SavedConsultationActivity::class.java)
            startActivity(intent)
        }
        btn_profile_terms_and_conditions.setOnClickListener {
            startActivity(Intent(requireActivity(), TermsAndConditionsActivity::class.java))
        }

    }
    private fun userInfoProfile(userID: String){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserInfoProfile(this@ProfileFragment ,userID)
    }
    fun userInfoSuccessProfile(userInfo: UserAlpas){
        GlideLoader(requireContext()).loadUserImage(userInfo.image, iv_user_photo_profile)
        tv_name_profile.text = "${userInfo.firstName} ${userInfo.lastName}"
        tv_email_profile.text = userInfo.email
        hideProgressDialog()
    }
}

