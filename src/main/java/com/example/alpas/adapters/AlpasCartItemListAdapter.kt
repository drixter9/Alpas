package com.example.alpas.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.alpas.CartListActivity
import com.example.alpas.Constants
import com.example.alpas.ProductDetailsActivity
import com.example.alpas.R
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.CartItem
import com.example.alpas.utilsAlpas.GlideLoader
import kotlinx.android.synthetic.main.item_cart_layout.view.*
import kotlinx.android.synthetic.main.item_list_layout.view.*

class AlpasCartItemListAdapter (
        private val context: Context,
        private val list:ArrayList<CartItem>,
        private val updatingCartItem : Boolean
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CartViewholder(
                LayoutInflater.from(context).inflate(
                        R.layout.item_cart_layout,
                        parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is CartViewholder){

            GlideLoader(context).loadProductImage(model.image,holder.itemView.iv_cart_item_image)
            holder.itemView.tv_cart_item_title.text = model.title
            holder.itemView.tv_cart_item_price.text = " Php ${model.price}"
            holder.itemView.tv_cart_quantity.text = model.cart_quantity

            if (model.cart_quantity == "0") {
                holder.itemView.ib_remove_cart_item.visibility = View.GONE
                holder.itemView.ib_add_cart_item.visibility=View.GONE

                if (updatingCartItem){
                    holder.itemView.ib_delete_cart_item.visibility = View.VISIBLE
                }
                else{
                    holder.itemView.ib_delete_cart_item.visibility = View.GONE
                }

                holder.itemView.tv_cart_quantity.text = context.resources.getString(R.string.lbl_out_of_stock)

                holder.itemView.tv_cart_quantity.setTextColor(
                        ContextCompat.getColor(
                                context,
                                R.color.colorDarkGrey
                        )
                )
            }
            else{
                if (updatingCartItem) {
                    holder.itemView.ib_remove_cart_item.visibility = View.VISIBLE
                    holder.itemView.ib_add_cart_item.visibility = View.VISIBLE
                    holder.itemView.ib_delete_cart_item.visibility = View.VISIBLE
                }
                else{
                    holder.itemView.ib_remove_cart_item.visibility = View.GONE
                    holder.itemView.ib_add_cart_item.visibility = View.GONE
                    holder.itemView.ib_delete_cart_item.visibility = View.GONE
                }
                holder.itemView.tv_cart_quantity.setTextColor(
                        ContextCompat.getColor(
                                context,
                                R.color.black
                        )
                )

            }
            holder.itemView.ib_delete_cart_item.setOnClickListener {
                when(context){
                    is CartListActivity -> {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                        FirestoreClass().deleteItemFromCart(context, model.id)
                    }
                }
            }
            holder.itemView.ib_remove_cart_item.setOnClickListener {
             if (model.cart_quantity == "1"){
                 FirestoreClass().deleteItemFromCart(context,model.id)
             }
             else{
                 val  cartQuantity: Int = model.cart_quantity.toInt()

                 val itemHashMap = HashMap<String, Any>()

                 itemHashMap[Constants.CART_QUANTITY]= (cartQuantity -1).toString()
                 if(context is CartListActivity){
                     context.showProgressDialog(context.resources.getString(R.string.please_wait))
                 }
                 FirestoreClass().updateCart(context, model.id, itemHashMap)
                }

            }
            holder.itemView.ib_add_cart_item.setOnClickListener {
                val cartQuantity: Int = model.cart_quantity.toInt()

                if (cartQuantity < model.stock_quantity.toInt()){
                    val itemHashMap = HashMap<String, Any>()
                    itemHashMap[Constants.CART_QUANTITY]= (cartQuantity +1).toString()

                    if(context is CartListActivity){
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }
                    FirestoreClass().updateCart(context, model.id, itemHashMap)
                }
                else{
                    if(context is CartListActivity){
                        context.showErrorSnackBar(
                                context.resources.getString(
                                        R.string.msg_for_available_stock,
                                        model.stock_quantity
                                ),
                                true
                        )
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    private class CartViewholder (view: View) : RecyclerView.ViewHolder(view)

}