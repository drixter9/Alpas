package com.example.alpas.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.alpas.*
import com.example.alpas.R
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.*
import com.example.alpas.ui.ForumFragment
import com.example.alpas.utilsAlpas.GlideLoader
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.item_comments.view.*
import kotlinx.android.synthetic.main.item_forum_list.view.*
import kotlinx.android.synthetic.main.item_product_layout.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class AlpasThreadsAllListAdapter(
    private val context: Context,
    private val list: ArrayList<Thread>,
    private val savedThreads: ArrayList<SavedThread>,
    private val fragment: ForumFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),Filterable {

    private var listFilterList = ArrayList<Thread>()
    init {
        listFilterList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ThreadViewholder(
            LayoutInflater.from(context).inflate(
                R.layout.item_forum_list,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = listFilterList[position]
        var likeID = ""
        var savedThread = ""
        var notificationID = ""
        if (holder is ThreadViewholder) {

            GlideLoader(context).loadUserImage(
                model.user_image,
                holder.itemView.iv_user_image_forum_item
            )
            holder.itemView.tv_forum_title.text = model.thread_title
            holder.itemView.tv_forum_type_engineering.text = model.thread_field_of_study
            holder.itemView.tv_forum_threads_topic.text = model.thread_topic

            if ((System.currentTimeMillis() - model.thread_date) >= TimeUnit.HOURS.toMillis(24)) {
                val dateFormat = "dd/MM/yyyy"
                val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
                val calendar: Calendar = Calendar.getInstance()
                calendar.timeInMillis = model.thread_date

                val dateMonthYear = formatter.format(calendar.time)
                holder.itemView.tv_forum_date_list.text = dateMonthYear
            } else {
                val dateFormat = "HH:mm"
                val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
                val calendar: Calendar = Calendar.getInstance()
                calendar.timeInMillis = model.thread_date
                val dateHour = formatter.format(calendar.time)
                holder.itemView.tv_forum_date_list.text = dateHour
            }
            holder.itemView.tv_forum_threads_description.text = model.thread_description

            if (model.thread_image == "") {
                holder.itemView.iv_forum_image.visibility = View.GONE
            } else {
                GlideLoader(context).loadProductImage(
                    model.thread_image,
                    holder.itemView.iv_forum_image
                )
                holder.itemView.iv_forum_image.visibility = View.VISIBLE
            }
            for (i in savedThreads) {
                if (i.thread_id == model.thread_id && i.user_id == FirestoreClass().getCurrentUserID()) {
                    holder.itemView.btn_go_to_save_forum.visibility = View.VISIBLE
                    holder.itemView.btn_save_forum_list.visibility = View.GONE
                    savedThread = i.user_id
                }
            }
            holder.itemView.btn_comments_forum_list.setOnClickListener {
                val intent = Intent(context, ThreadDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_THREAD_DETAILS, model.thread_id)
                intent.putExtra(Constants.EXTRA_SAVED_THREAD_DETAILS, savedThread)
                context.startActivity(intent)
            }
            holder.itemView.ll_forum_details.setOnClickListener {
                val intent = Intent(context, ThreadDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_THREAD_DETAILS, model.thread_id)
                intent.putExtra(Constants.EXTRA_SAVED_THREAD_DETAILS, savedThread)
                context.startActivity(intent)
            }
            holder.itemView.tv_forum_threads_description.setOnClickListener {
                val intent = Intent(context, ThreadDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_THREAD_DETAILS, model.thread_id)
                intent.putExtra(Constants.EXTRA_SAVED_THREAD_DETAILS, savedThread)
                context.startActivity(intent)
            }

            holder.itemView.btn_save_forum_list.setOnClickListener {
                val yourSavedItems = SavedThread(
                    FirestoreClass().getCurrentUserID(),
                    model.user_id,
                    model.user_name,
                    model.user_image,
                    model.thread_title,
                    model.thread_field_of_study,
                    model.thread_topic,
                    model.thread_description,
                    model.thread_image,
                    model.thread_like,
                    model.thread_comments,
                    model.thread_date,
                    model.thread_id,
                )
                fragment.saveThread(yourSavedItems)
            }

            holder.itemView.btn_go_to_save_forum.setOnClickListener {
                val intent = Intent(context, SaveThreadsActivity::class.java)
                context.startActivity(intent)
            }

            holder.itemView.tb_like_forum_list.setOnClickListener {
                if (holder.itemView.tb_like_forum_list.isChecked) {
                    val like = Like(model.thread_id, FirestoreClass().getCurrentUserID())
                    fragment.saveLike(like, model.thread_id,model.thread_title,model.user_id,true)
                } else {
                    fragment.deleteLike(likeID, model.thread_id,false, notificationID)
                }
            }
            holder.itemView.iv_user_image_forum_item.setOnClickListener {
                fragment.userInfo(model.user_id)
            }

            val databaseReferenceNotification: DatabaseReference =
                FirebaseDatabase.getInstance().getReference(Constants.NOTIFICATION)
            databaseReferenceNotification.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapShot: DataSnapshot in snapshot.children )  {
                        val notif = dataSnapShot.getValue(NotificationData::class.java)
                        if (notif != null) {
                            if (notif.user_id == FirestoreClass().getCurrentUserID() && notif.topic_id == model.thread_id && notif.type == 1) {
                                notificationID = dataSnapShot.key.toString()
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

            val databaseReference: DatabaseReference =
                FirebaseDatabase.getInstance().getReference(Constants.LIKE)
                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapShot: DataSnapshot in snapshot.children) {
                        val like = dataSnapShot.getValue(Like::class.java)
                        if (like!!.user_id == FirestoreClass().getCurrentUserID() && like.thread_id == model.thread_id) {
                            likeID = dataSnapShot.key.toString()
                            holder.itemView.tb_like_forum_list.isChecked = true
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })



            val numberString = when {
                (abs(model.thread_like / 1000000)) > 1 -> (model.thread_like / 1000000).toString()+ "m"
                (abs(model.thread_like / 1000) > 1) -> (model.thread_like / 1000).toString() + "k"
                else -> model.thread_like.toString()
            }
            holder.itemView.tb_like_forum_list.textOn = numberString
            holder.itemView.tb_like_forum_list.text = numberString
        }
    }

    override fun getItemCount(): Int {
        return listFilterList.size
    }

    private class ThreadViewholder(view: View) : RecyclerView.ViewHolder(view)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                listFilterList = if (charSearch == "") {
                    list
                } else {
                    val resultList = ArrayList<Thread>()
                    for (row in list) {
                        if (row.thread_title.toLowerCase(Locale.ROOT).contains(
                                constraint.toString().toLowerCase(Locale.ROOT)
                            )||
                            row.thread_description.toLowerCase(Locale.ROOT).contains(
                                constraint.toString().toLowerCase(Locale.ROOT)
                            )||
                            row.thread_topic.toLowerCase(Locale.ROOT).contains(
                                constraint.toString().toLowerCase(Locale.ROOT)
                            )||
                            row.thread_field_of_study.toLowerCase(Locale.ROOT).contains(
                                constraint.toString().toLowerCase(Locale.ROOT)
                            )
                        ) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }

                val filterResults = FilterResults()
                filterResults.values = listFilterList
                return filterResults
            }
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listFilterList = results!!.values as ArrayList<Thread>
                notifyDataSetChanged()
            }
        }
    }
}