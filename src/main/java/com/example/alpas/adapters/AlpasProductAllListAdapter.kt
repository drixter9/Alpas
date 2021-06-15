package com.example.alpas.adapters

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.alpas.Constants
import com.example.alpas.ProductDetailsActivity
import com.example.alpas.R
import com.example.alpas.models.Product
import com.example.alpas.utilsAlpas.GlideLoader
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.item_product_layout.view.*
import java.util.*
import kotlin.collections.ArrayList


class AlpasProductAllListAdapter(
    private val context: Context,
    private val list: ArrayList<Product>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var listFilterList = ArrayList<Product>()
    init {
        listFilterList = list
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProductViewholder(
            LayoutInflater.from(context).inflate(
                R.layout.item_product_layout,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = listFilterList[position]

        if (holder is ProductViewholder) {
            GlideLoader(context).loadProductImage(
                model.image,
                holder.itemView.iv_item_product_image
            )
            holder.itemView.tv_item_product_name.text = model.title
            holder.itemView.tv_item_product_price.text = "Php ${model.price}"
            holder.itemView.tv_item_product_stock.text = model.stock_quantity

            holder.itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.product_id)
                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.user_id)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return listFilterList.size
    }

    private class ProductViewholder(view: View) : RecyclerView.ViewHolder(view)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                listFilterList = if (charSearch == "") {
                    list
                } else {
                    val resultList = ArrayList<Product>()
                    for (row in list) {
                        if (row.title.toLowerCase(Locale.ROOT).contains(
                                constraint.toString().toLowerCase(Locale.ROOT)
                            )||
                            row.description.toLowerCase(Locale.ROOT).contains(
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
                listFilterList = results!!.values as ArrayList<Product>
                notifyDataSetChanged()
            }
        }
    }
}