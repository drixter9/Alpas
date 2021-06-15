package com.example.alpas.adapters

import android.annotation.SuppressLint
import android.app.Activity
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
import com.example.alpas.models.UserAlpas
import com.example.alpas.utilsAlpas.GlideLoader
import kotlinx.android.synthetic.main.item_user.view.*
import java.util.*
import kotlin.collections.ArrayList


class AlpasSearchUserListAdapter(
    private val context: Context,
    private val userList: ArrayList<UserAlpas>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private var listFilterList = ArrayList<UserAlpas>()
    init {
        listFilterList = userList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return listFilterList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user = listFilterList[position]

        holder.itemView.tv_user_name.text = "${user.firstName} ${user.lastName}"
        holder.itemView.tv_new_message.text = user.email
        GlideLoader(context).loadUserImage(user.image, holder.itemView.iv_user_image)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS_CHAT, user.uid)
            context.startActivity(intent)
            (context as Activity).finish()
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
                    val resultList = ArrayList<UserAlpas>()
                    for (row in userList) {
                            if (row.firstName.toLowerCase(Locale.ROOT).contains(
                                    constraint.toString().toLowerCase(Locale.ROOT)
                                ) || row.lastName.toLowerCase(Locale.ROOT).contains(
                                    constraint.toString().toLowerCase(Locale.ROOT)
                                )||
                                row.email.toLowerCase(Locale.ROOT).contains(
                                    constraint.toString().toLowerCase(Locale.ROOT)
                                )
                            ) {
                                resultList.add(row)
                            }

                    }
                    listFilterList = resultList
                }

                val filterResults = FilterResults()
                filterResults.values = listFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listFilterList = results!!.values as ArrayList<UserAlpas>
                notifyDataSetChanged()
            }
        }
    }


}