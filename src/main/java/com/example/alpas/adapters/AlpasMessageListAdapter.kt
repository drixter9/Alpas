package com.example.alpas.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.alpas.ChatActivity
import com.example.alpas.Constants
import com.example.alpas.R
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.Chat
import com.example.alpas.models.Product
import com.example.alpas.models.UserAlpas
import com.example.alpas.utilsAlpas.GlideLoader
import kotlinx.android.synthetic.main.item_user.view.*
import java.util.*
import kotlin.collections.ArrayList

class AlpasMessageListAdapter(
private val context: Context,
private val userList: ArrayList<Chat>) :
RecyclerView.Adapter<AlpasMessageListAdapter.ViewHolder>(),Filterable {

    private var listFilterList = ArrayList<Chat>()
    init {
        listFilterList = userList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return listFilterList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = listFilterList[position]
        if (user.senderId == FirestoreClass().getCurrentUserID()){
            holder.itemView.tv_user_name.text = user.recieverName
                holder.itemView.tv_new_message.text = user.message
            GlideLoader(context).loadUserImage(user.receiverImage,holder.itemView.iv_user_image)

            holder.itemView.setOnClickListener {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra(Constants.EXTRA_USER_DETAILS_CHAT,user.receiverId)
                context.startActivity(intent)
            }
        }
        else{
            holder.itemView.tv_user_name.text = user.senderNAme
            holder.itemView.tv_new_message.text = user.message
            GlideLoader(context).loadUserImage(user.senderImage,holder.itemView.iv_user_image)
            holder.itemView.setOnClickListener {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra(Constants.EXTRA_USER_DETAILS_CHAT,user.senderId)
                context.startActivity(intent)
            }
        }

    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                   listFilterList = userList
                } else {
                    val resultList = ArrayList<Chat>()
                    for (row in userList) {
                        if (row.senderId == FirestoreClass().getCurrentUserID()) {
                            if (row.recieverName.toLowerCase(Locale.ROOT).contains(
                                    constraint.toString().toLowerCase(Locale.ROOT)
                                )
                            ) {
                                resultList.add(row)
                            }
                        }

                        else{
                            if (row.senderNAme.contains(
                                    constraint.toString().toLowerCase(Locale.ROOT)
                                )
                            ) {
                                resultList.add(row)
                            }
                        }

                    }
                    listFilterList = resultList
                }

                val filterResults = FilterResults()
                filterResults.values = listFilterList
                return filterResults
            }
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listFilterList = results!!.values as ArrayList<Chat>
                notifyDataSetChanged()
            }
        }
    }
}