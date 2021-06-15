package com.example.alpas.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alpas.*
import com.example.alpas.models.Thread
import com.example.alpas.utilsAlpas.GlideLoader
import kotlinx.android.synthetic.main.item_list_layout.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class AlpasMyThreadsListAdapter(
    private val context: Context,
    private val list: ArrayList<Thread>,
    private val activity: MyThreadsActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyThreadsViewholder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,
                parent,
                false
            )
        )
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyThreadsViewholder) {
            GlideLoader(context).loadProductImage(model.thread_image, holder.itemView.iv_item_image)
            holder.itemView.tv_item_name.text = model.thread_title
            holder.itemView.tv_item_price.text = model.thread_topic

            if ((System.currentTimeMillis() - model.thread_date ) >= TimeUnit.HOURS.toMillis(24)){
                val dateFormat = "dd/MM/yyyy"
                val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
                val calendar: Calendar = Calendar.getInstance()
                calendar.timeInMillis = model.thread_date

                val dateMonthYear = formatter.format(calendar.time)
                holder.itemView.tv_item_stock.text = dateMonthYear
            }
            else {
                val dateFormat = "HH:mm"
                val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
                val calendar: Calendar = Calendar.getInstance()
                calendar.timeInMillis = model.thread_date
                val dateHour = formatter.format(calendar.time)
                holder.itemView.tv_item_stock.text = dateHour
            }

            holder.itemView.ib_delete_product.setOnClickListener {
               activity.deleteThread(model.thread_id)
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(context, ThreadDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_THREAD_DETAILS, model.thread_id)
                context.startActivity(intent)
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    private class MyThreadsViewholder(view: View) : RecyclerView.ViewHolder(view)

}