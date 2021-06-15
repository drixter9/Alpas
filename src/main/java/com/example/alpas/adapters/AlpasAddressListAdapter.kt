package com.example.alpas.adapters;

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.alpas.*
import com.example.alpas.models.Address;

import kotlinx.android.synthetic.main.item_address_list_layout.view.*

import java.util.ArrayList;

class AlpasAddressListAdapter (
        private val context: Context,
        private val list:ArrayList<Address>,
        private val selectAddress: Boolean
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AddressViewholder(
                LayoutInflater.from(context).inflate(
                        R.layout.item_address_list_layout,
                        parent,
                        false
                )
        )
        }

        fun notifyEditItem (activity: Activity, position: Int){
                val intent = Intent (context, AddAddressActivity::class.java)

                intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS,list[position])
                activity.startActivityForResult(intent,Constants.ADD_ADDRESS_REQUEST_CODE)
                notifyItemChanged(position)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

                if (holder is AddressViewholder){
                        holder.itemView.tv_address_full_name_list.text = model.name
                        holder.itemView.tv_address_type_list.text = model.address_type
                        holder.itemView.tv_address_details_list.text = "${model.address}, ${model.zipCode}"
                        holder.itemView.tv_address_phone_number_list.text = model.mobile

                        if(selectAddress){
                                holder.itemView.setOnClickListener {
                                        val intent = Intent (context, CheckoutActivity::class.java)
                                        intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, model)
                                        context.startActivity(intent)
                                }
                        }

                }

        }

        override fun getItemCount(): Int {
                return list.size
        }


    private class AddressViewholder (view: View) : RecyclerView.ViewHolder(view)


}