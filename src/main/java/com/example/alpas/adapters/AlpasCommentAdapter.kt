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
import com.example.alpas.models.Comment
import com.example.alpas.models.Consultation
import com.example.alpas.models.Like
import com.example.alpas.utilsAlpas.GlideLoader
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_thread_details.*
import kotlinx.android.synthetic.main.item_comments.view.*
import kotlinx.android.synthetic.main.item_forum_list.view.*
import kotlinx.android.synthetic.main.item_list_layout.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.abs

open class AlpasCommentAdapter (
    private val context: Context,
    private val list: ArrayList<Comment>,
    private val activity: ThreadDetailsActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CommentViewholder(
            LayoutInflater.from(context).inflate(
                R.layout.item_comments,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        var likeID = ""
        if (holder is CommentViewholder) {
            GlideLoader(context).loadUserImage(model.sender_image,holder.itemView.userImage)
            holder.itemView.tv_comment_user_name.text = model.sender_name
            holder.itemView.tv_comment_message.text = model.comment

            if ((System.currentTimeMillis() - model.date )>= TimeUnit.HOURS.toMillis(24)){
                val dateFormat = "dd/MM/yyyy"
                val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
                val calendar: Calendar = Calendar.getInstance()
                calendar.timeInMillis = model.date

                val dateMonthYear = formatter.format(calendar.time)
                holder.itemView.tv_comment_date.text = dateMonthYear
            }
            else {
                val dateFormat = "HH:mm"
                val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
                val calendar: Calendar = Calendar.getInstance()
                calendar.timeInMillis = model.date
                val dateHour = formatter.format(calendar.time)
                holder.itemView.tv_comment_date.text = dateHour
            }
            holder.itemView.tb_like_comment_list.setOnClickListener {
                if (holder.itemView.tb_like_comment_list.isChecked) {
                    val like = Like(model.comment_id, FirestoreClass().getCurrentUserID())
                    activity.saveLikeComment(like,model.comment_id)
                } else {
                    activity.deleteLikeComment(likeID,model.comment_id)
                }
            }

            val databaseReference: DatabaseReference =
                FirebaseDatabase.getInstance().getReference(Constants.LIKE)
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapShot: DataSnapshot in snapshot.children) {
                        val like = dataSnapShot.getValue(Like::class.java)
                        if (like!!.user_id == FirestoreClass().getCurrentUserID() && like.thread_id == model.comment_id) {
                            likeID = dataSnapShot.key.toString()
                            holder.itemView.tb_like_comment_list.isChecked = true
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
            val numberString = when {
                (abs(model.comment_like / 1000000)) > 1 -> (model.comment_like / 1000000).toString()+ "m"
                (abs(model.comment_like / 1000) > 1) -> (model.comment_like / 1000).toString() + "k"
                else -> model.comment_like.toString()
            }
            holder.itemView.tb_like_comment_list.textOn = numberString
            holder.itemView.tb_like_comment_list.text = numberString

            holder.itemView.userImage.setOnClickListener {
                activity.userInfo(model.sender_id)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class CommentViewholder(view: View) : RecyclerView.ViewHolder(view)


}