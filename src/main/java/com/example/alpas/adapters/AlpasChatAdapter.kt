package com.example.alpas.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alpas.R
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.Chat
import com.example.alpas.utilsAlpas.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.item_chat_left.view.*
import kotlinx.android.synthetic.main.item_chat_left.view.tvMessage
import kotlinx.android.synthetic.main.item_chat_left.view.tv_chat_date
import kotlinx.android.synthetic.main.item_chat_right.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class AlpasChatAdapter (
    private val context: Context,
    private val chatList: ArrayList<Chat>) :
    RecyclerView.Adapter<AlpasChatAdapter.ViewHolder>() {
    private val firebaseUser = FirestoreClass().getCurrentUserID()
    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == MESSAGE_TYPE_RIGHT) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_chat_right, parent, false)
            ViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_chat_left, parent, false)
            ViewHolder(view)
        }

    }


    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatList[position]
        holder.itemView.tvMessage.text = chat.message
        if (getItemViewType(position) == MESSAGE_TYPE_LEFT) {
            GlideLoader(context).loadUserImage(
                chat.senderImage,
                holder.itemView.userImage_left
            )
        }
        if (getItemViewType(position) == MESSAGE_TYPE_RIGHT) {
            GlideLoader(context).loadUserImage(
                chat.senderImage,
                holder.itemView.userImage_right
            )
        }


        if ((System.currentTimeMillis()  - chat.date.toLong() )>= TimeUnit.HOURS.toMillis(24)){
            val dateFormat = "dd/MM/yyyy"
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = chat.date.toLong()

            val dateMonthYear = formatter.format(calendar.time)
            holder.itemView.tv_chat_date.text = dateMonthYear
        }
        else {
            val dateFormat = "HH:mm"
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = chat.date.toLong()
            val dateHour = formatter.format(calendar.time)
            holder.itemView.tv_chat_date.text = dateHour
        }
        holder.itemView.tv_chat_date.visibility = View.VISIBLE
        }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].senderId == firebaseUser) {
            MESSAGE_TYPE_RIGHT
        } else {
            MESSAGE_TYPE_LEFT
        }

    }
}