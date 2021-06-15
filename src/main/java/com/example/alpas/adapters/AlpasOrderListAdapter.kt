package com.example.alpas.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.alpas.Constants
import com.example.alpas.FirebaseService
import com.example.alpas.OrderDetailsActivity
import com.example.alpas.R
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.NotificationData
import com.example.alpas.models.Order
import com.example.alpas.models.PushNotification
import com.example.alpas.utilsAlpas.GlideLoader
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_order_details.*
import kotlinx.android.synthetic.main.item_list_layout.view.*

class AlpasOrderListAdapter(
    private val context: Context,
    private val list: ArrayList<Order>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OrderViewholder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is OrderViewholder) {
            GlideLoader(context).loadProductImage(model.image, holder.itemView.iv_item_image)
            holder.itemView.tv_item_name.text = model.title
            holder.itemView.tv_item_price.text = "Php ${model.total_amount}"
            val diffInMilliSeconds: Long = System.currentTimeMillis() - model.order_dateTime
            val diffInHours: Long =
                java.util.concurrent.TimeUnit.MILLISECONDS.toHours(diffInMilliSeconds)
            Log.e("Difference in Hours", "$diffInHours")

            when {
                diffInHours < 1 -> {
                    holder.itemView.tv_item_stock.text = Constants.STATUS_PENDING
                }
                diffInHours < 2 -> {
                    if (holder.itemView.tv_item_stock.text != Constants.STATUS_PROCESSING) {
                        holder.itemView.tv_item_stock.text = Constants.STATUS_PROCESSING
                        val topic = "/topics/${model.user_id}"
                        val message = "${model.title} is on process"
                        val notification = NotificationData(
                            FirestoreClass().getCurrentUserID(),
                            model.image,
                            model.id,
                            "Your Order",
                            message,
                            Constants.NOTIFICATION_ORDER,
                            model.user_id,
                            System.currentTimeMillis()
                        )
                        PushNotification(
                            notification,
                            topic
                        ).also {
                            FirebaseService().sendNotification(it)
                        }
                        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
                        reference.child(Constants.NOTIFICATION).push().setValue(notification)
                    }

                }
                else -> {
                    holder.itemView.tv_item_stock.text = Constants.STATUS_DELIVER
                }
            }

            holder.itemView.ib_delete_product.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, OrderDetailsActivity::class.java)
            intent.putExtra(Constants.EXTRA_ORDER_DETAILS, model)
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class OrderViewholder(view: View) : RecyclerView.ViewHolder(view)

}