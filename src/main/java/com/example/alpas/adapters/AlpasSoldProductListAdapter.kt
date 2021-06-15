package com.example.alpas.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alpas.Constants
import com.example.alpas.R
import com.example.alpas.SoldProductDetailsActivity
import com.example.alpas.models.SoldProduct
import com.example.alpas.utilsAlpas.GlideLoader
import kotlinx.android.synthetic.main.activity_sold_product_details.view.*
import kotlinx.android.synthetic.main.item_comments.view.*
import kotlinx.android.synthetic.main.item_list_layout.view.*
import kotlinx.android.synthetic.main.item_product_layout.view.*

class AlpasSoldProductListAdapter (
    private val context: Context,
    private var list: ArrayList<SoldProduct>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SoldProductViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is SoldProductViewHolder) {

            GlideLoader(context).loadProductImage(
                model.image,
                holder.itemView.iv_item_image
            )

            holder.itemView.tv_item_name.text = model.title
            holder.itemView.tv_item_price.text = "Php ${model.price}"

            holder.itemView.ib_delete_product.visibility = View.GONE

            holder.itemView.setOnClickListener {
               val intent = Intent(context, SoldProductDetailsActivity::class.java)
               intent.putExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS, model)
               context.startActivity(intent)
            }


        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    class SoldProductViewHolder(view: View) : RecyclerView.ViewHolder(view)
}