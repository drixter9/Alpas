package com.example.alpas.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.alpas.*
import com.example.alpas.adapters.AlpasProductAllListAdapter
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.Product
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.fragment_consultation.*
import kotlinx.android.synthetic.main.fragment_product_list.*

class ProductListFragment : BaseFragment() {

    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            activity,
            R.anim.rotate_animation_open
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            activity,
            R.anim.rotate_animation_close
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            activity,
            R.anim.from_animation_bottom
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            activity,
            R.anim.to_animation_bottom
        )
    }
    lateinit var adapter: AlpasProductAllListAdapter
    private lateinit var mProductList: ArrayList<Product>
    private var click = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_product_list, container, false)
        return root
    }

    override fun onResume() {
        super.onResume()
        getHomeItemList()
        ib_chat.setOnClickListener {
            val intent = Intent(activity, MessageActivity::class.java)
            startActivity(intent)
        }
        ib_cart.setOnClickListener {
            val intent = Intent(activity, CartListActivity::class.java)
            startActivity(intent)
        }
        fb_menu_shopping.setOnClickListener {
            onMenuButtonClick()
        }
        fb_products_shopping.setOnClickListener {
            val intent = Intent(activity, MyProductsActivity::class.java)
            startActivity(intent)
        }
        fb_orders_shopping.setOnClickListener {
            val intent = Intent(activity, MyOrderActivity::class.java)
            startActivity(intent)
        }
        fb_sold_shopping.setOnClickListener {
            val intent = Intent(activity, MySoldProductsActivity::class.java)
            startActivity(intent)
        }
        sv_shopping_search.setQuery("",false)
        shopping_fragment.requestFocus()

        if (sv_shopping_search != null) {
            sv_shopping_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    sv_shopping_search.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter.filter(newText)
                    return true
                }
            })
        }




    }

    private fun onMenuButtonClick() {
        setVisibility(click)
        setAnimation(click)
        click = !click
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            ll_fb_products.visibility = View.VISIBLE
            ll_fb_order.visibility = View.VISIBLE
            ll_fb_sold.visibility = View.VISIBLE
        } else {
            ll_fb_products.visibility = View.GONE
            ll_fb_order.visibility = View.GONE
            ll_fb_sold.visibility = View.GONE
        }
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            ll_fb_products.startAnimation(fromBottom)
            ll_fb_order.startAnimation(fromBottom)
            ll_fb_sold.startAnimation(fromBottom)
            fb_menu_shopping.startAnimation(rotateOpen)
        } else {
            ll_fb_products.startAnimation(toBottom)
            ll_fb_order.startAnimation(toBottom)
            ll_fb_sold.startAnimation(toBottom)
            fb_menu_shopping.startAnimation(rotateClose)
        }
    }

    private fun getHomeItemList() {
        FirestoreClass().getHomeItemsList(this@ProductListFragment)
    }

    fun successHomeDashboardItemsList(productALLItemsList: ArrayList<Product>) {
        mProductList = productALLItemsList

        if (rv_home_items_products != null) {
            if (mProductList.size > 0) {
                rv_home_items_products.visibility = View.VISIBLE
                tv_no_home_items_found.visibility = View.GONE

                rv_home_items_products.layoutManager = GridLayoutManager(activity, 2)
                rv_home_items_products.setHasFixedSize(true)

                adapter = AlpasProductAllListAdapter(requireActivity(), mProductList)
                rv_home_items_products.adapter = adapter

            } else {
                rv_home_items_products.visibility = View.GONE
                tv_no_home_items_found.visibility = View.VISIBLE

            }
        }
    }


}