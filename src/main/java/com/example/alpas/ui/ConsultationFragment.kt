package com.example.alpas.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.example.alpas.*
import com.example.alpas.adapters.AlpasConsultationListAdapter
import com.example.alpas.adapters.AlpasProductAllListAdapter
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.Consultation
import com.example.alpas.models.Product
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.fragment_consultation.*
import kotlinx.android.synthetic.main.fragment_forum.*
import kotlinx.android.synthetic.main.fragment_product_list.*

class ConsultationFragment : BaseFragment() {

    lateinit var adapter: AlpasConsultationListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_consultation, container, false)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onResume() {
        super.onResume()
        fb_my_consultation.setOnClickListener {
            val intent = Intent(activity, MyConsultationActivity::class.java)
            startActivity(intent)
        }
        ib_save_consult.setOnClickListener {
            val intent = Intent(activity, SavedConsultationActivity::class.java)
            startActivity(intent)
        }
        ib_chat_consult.setOnClickListener {
            val intent = Intent(activity, MessageActivity::class.java)
            startActivity(intent)
        }

        sv_consultation_search.setQuery("",false)
        consultation_fragment.requestFocus()
        getConsultationItemList()

        if (sv_consultation_search != null) {
            sv_consultation_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    sv_consultation_search.clearFocus()
                    return true
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter.filter(newText)
                    return true
                }
            })
        }

    }

    private fun getConsultationItemList(){
        FirestoreClass().getConsultationItemsList(this@ConsultationFragment)

    }

    fun successConsultationDashboardItemsList(consultationItemsList: ArrayList<Consultation>){
        if (rv_consultation_list != null) {

            if (consultationItemsList.size > 0) {
                rv_consultation_list.visibility = View.VISIBLE
                tv_no_consultation_items_found.visibility = View.GONE

                rv_consultation_list.layoutManager = GridLayoutManager(activity, 2)

                rv_consultation_list.setHasFixedSize(true)

                adapter = AlpasConsultationListAdapter(requireActivity(), consultationItemsList)
                rv_consultation_list.adapter = adapter

            } else {
                rv_consultation_list.visibility = View.GONE
                tv_no_consultation_items_found.visibility = View.VISIBLE

            }
        }
    }

}