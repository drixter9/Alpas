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
import com.example.alpas.models.Consultation
import com.example.alpas.models.Product
import com.example.alpas.utilsAlpas.GlideLoader
import kotlinx.android.synthetic.main.item_list_layout.view.*
import kotlinx.android.synthetic.main.item_product_layout.view.*
import java.util.*
import kotlin.collections.ArrayList

class AlpasConsultationListAdapter (
    private val context: Context,
    private val list: ArrayList<Consultation>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),Filterable {

    private var listFilterList = ArrayList<Consultation>()
    init {
        listFilterList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ConsultationViewholder(
            LayoutInflater.from(context).inflate(
                R.layout.item_product_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = listFilterList[position]

        if (holder is ConsultationViewholder) {

                GlideLoader(context).loadProductImage(model.image,holder.itemView.iv_item_product_image)
                holder.itemView.tv_item_product_name.text = model.consultation_title
                holder.itemView.tv_item_product_price.text = "${model.consultation_degree} of ${model.consultation_program_of_study}"
                holder.itemView.tv_item_product_stock.text = "Php ${model.consultation_price}"

                holder.itemView.setOnClickListener {
                    val intent = Intent(context, ConsultationDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_CONSULTATION_ID, model.consultation_id)
                    intent.putExtra(Constants.EXTRA_CONSULTATION_OWNER_ID, model.user_id)
                    context.startActivity(intent)
                }
        }
    }

    override fun getItemCount(): Int {
        return listFilterList.size
    }

    private class ConsultationViewholder(view: View) : RecyclerView.ViewHolder(view)


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                listFilterList = if (charSearch == "") {
                    list
                } else {
                    val resultList = ArrayList<Consultation>()
                    for (row in list) {
                        if (row.consultation_title.toLowerCase(Locale.ROOT).contains(
                                constraint.toString().toLowerCase(Locale.ROOT)
                            )||row.consultation_degree.toLowerCase(Locale.ROOT).contains(
                                constraint.toString().toLowerCase(Locale.ROOT))
                            ||row.consultation_description.toLowerCase(Locale.ROOT).contains(
                                constraint.toString().toLowerCase(Locale.ROOT))
                            ||row.consultation_program_of_study.toLowerCase(Locale.ROOT).contains(
                                constraint.toString().toLowerCase(Locale.ROOT))
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
                listFilterList = results!!.values as ArrayList<Consultation>
                notifyDataSetChanged()
            }
        }
    }
}