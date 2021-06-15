package com.example.alpas.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alpas.*
import com.example.alpas.R
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.Like
import com.example.alpas.models.SavedConsultation
import com.example.alpas.models.SavedThread
import com.example.alpas.utilsAlpas.GlideLoader
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.item_forum_list.view.*
import kotlinx.android.synthetic.main.item_list_layout.view.*

class AlpasSavedThreadListAdapter (
private val context: Context,
private val list: ArrayList<SavedThread>,
private val activity: SaveThreadsActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SavedThreadViewholder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,
                parent,
                false
            )
        )
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is SavedThreadViewholder) {
            GlideLoader(context).loadProductImage(model.thread_image, holder.itemView.iv_item_image)
            holder.itemView.tv_item_name.text = model.thread_title
            holder.itemView.tv_item_price.text = model.thread_topic
            holder.itemView.tv_item_stock.text = model.thread_field_of_study

            holder.itemView.ib_delete_product.setOnClickListener {
              activity.deleteSavedThread(model.saved_thread_id)
            }
            holder.itemView.setOnClickListener {
                val intent = Intent(context, ThreadDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_THREAD_DETAILS, model.thread_id)
                intent.putExtra(Constants.EXTRA_SAVED_THREAD_DETAILS, model.user_id)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class SavedThreadViewholder(view: View) : RecyclerView.ViewHolder(view)

}