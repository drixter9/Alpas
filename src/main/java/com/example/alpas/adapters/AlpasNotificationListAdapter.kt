package com.example.alpas.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alpas.*
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.NotificationData
import com.example.alpas.models.SavedThread
import com.example.alpas.ui.NotificationsFragment
import com.example.alpas.utilsAlpas.GlideLoader
import kotlinx.android.synthetic.main.item_comments.view.*
import kotlinx.android.synthetic.main.item_forum_list.view.*
import kotlinx.android.synthetic.main.item_notifications.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

open class AlpasNotificationListAdapter  (
private val context: Context,
private val list: ArrayList<NotificationData>,
private val saveThreadList: ArrayList<SavedThread>,
private val Fragment:NotificationsFragment,

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationViewholder(
            LayoutInflater.from(context).inflate(
                R.layout.item_notifications,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        var savedThread = ""
        if (holder is NotificationViewholder) {
            GlideLoader(context).loadUserImage(model.user_image,holder.itemView.userImage_notification)
            holder.itemView.tv_notification_message.text = "\"${model.title}\" ${model.message}"

            if ((System.currentTimeMillis() - model.date )>= TimeUnit.HOURS.toMillis(24)){
                val dateFormat = "dd/MM/yyyy"
                val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
                val calendar: Calendar = Calendar.getInstance()
                calendar.timeInMillis = model.date

                val dateMonthYear = formatter.format(calendar.time)
                holder.itemView.tv_notification_date.text = dateMonthYear
            }
            else {
                val dateFormat = "HH:mm"
                val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
                val calendar: Calendar = Calendar.getInstance()
                calendar.timeInMillis = model.date
                val dateHour = formatter.format(calendar.time)
                holder.itemView.tv_notification_date.text = dateHour
            }

            for (i in saveThreadList) {
                if (i.thread_id == model.topic_id && i.user_id == FirestoreClass().getCurrentUserID()) {
                    savedThread = i.user_id
                }
            }

            holder.itemView.setOnClickListener {
                when(model.type){
                    1,4,5-> { val intent = Intent(context, ThreadDetailsActivity::class.java)
                        intent.putExtra(Constants.EXTRA_THREAD_DETAILS, model.topic_id)
                        intent.putExtra(Constants.EXTRA_SAVED_THREAD_DETAILS, savedThread)
                        context.startActivity(intent)}

                    2 ->{val intent = Intent(context, ProductDetailsActivity::class.java)
                        intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.topic_id)
                        intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.to)
                        context.startActivity(intent)}
                    3 ->{val intent = Intent(context, MyOrderActivity::class.java)
                        context.startActivity(intent)}
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
    private class NotificationViewholder(view: View) : RecyclerView.ViewHolder(view)
}