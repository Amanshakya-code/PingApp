package com.example.ping.fragments

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.ping.models.Inbox
import com.example.ping.R
import com.example.ping.formatAsListItem

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.list_item.view.*
import java.util.*

class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(inbox: Inbox, onClick: (name: String, photo: String, id: String) -> Unit) =
            with(itemView) {
                countTv.isVisible = inbox.count > 0
                countTv.text = inbox.count.toString()
                timeTv.text = inbox.time.formatAsListItem(context)

                titleTv.text = inbox.name
                subTitleTv.text = inbox.msg
                if(inbox.image.isEmpty())
                {
                    userImgView.setImageResource(R.color.action_bar_color)
                    textname.visibility = View.VISIBLE
                    textname.text = inbox.name[0].toString().toUpperCase(Locale.ROOT)
                }
                else
                {
                    textname.visibility = View.GONE
                    Picasso.get()
                        .load(inbox.image)
                        .placeholder(R.drawable.defaultavatar)
                        .error(R.drawable.defaultavatar)
                        .into(userImgView)
                }

                setOnClickListener {
                    onClick.invoke(inbox.name, inbox.image, inbox.from)
                }
            }
}