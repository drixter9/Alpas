package com.example.alpas.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alpas.*
import com.example.alpas.models.Consultation
import com.example.alpas.utilsAlpas.GlideLoader
import kotlinx.android.synthetic.main.item_list_layout.view.*

open class AlpasMyConsultationAdapter(
    private val context: Context,
    private val list: ArrayList<Consultation>,
    private val activity: MyConsultationActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyConsultationViewholder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,
                parent,
                false
            )
        )
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyConsultationViewholder) {
            GlideLoader(context).loadProductImage(model.image, holder.itemView.iv_item_image)
            holder.itemView.tv_item_name.text = model.user_name
            holder.itemView.tv_item_price.text = "${model.consultation_degree} of ${model.consultation_program_of_study}"
            holder.itemView.tv_item_stock.text = "Php ${model.consultation_price}"

            holder.itemView.ib_delete_product.setOnClickListener {
                activity.deleteConsultation(model.consultation_id)
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(context, ConsultationDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_CONSULTATION_ID, model.consultation_id)
                intent.putExtra(Constants.EXTRA_CONSULTATION_OWNER_ID, model.user_id)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyConsultationViewholder(view: View) : RecyclerView.ViewHolder(view)
}