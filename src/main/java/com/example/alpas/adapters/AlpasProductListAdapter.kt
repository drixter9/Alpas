package com.example.alpas.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alpas.*
import com.example.alpas.models.Product
import com.example.alpas.utilsAlpas.GlideLoader
import kotlinx.android.synthetic.main.item_list_layout.view.*

open class AlpasProductListAdapter (
    private val context: Context,
    private val list:ArrayList<Product>,
    private val activity: MyProductsActivity
    ): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProductViewholder(
            LayoutInflater.from(context).inflate(
                    R.layout.item_list_layout,
                    parent,
                    false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is ProductViewholder){
            GlideLoader(context).loadProductImage(model.image,holder.itemView.iv_item_image)
            holder.itemView.tv_item_name.text = model.title
            holder.itemView.tv_item_price.text = " Php ${model.price}"
            holder.itemView.tv_item_stock.text = model.stock_quantity

            holder.itemView.ib_delete_product.setOnClickListener {
            activity.deleteProduct(model.product_id)
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.product_id)
                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.user_id)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    private class ProductViewholder (view: View) : RecyclerView.ViewHolder(view)

}