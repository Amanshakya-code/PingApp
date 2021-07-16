package com.example.ping.fragments

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.ping.R
import com.example.ping.models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item.view.*
import java.util.*

class UserViewHolder(itemView:View) :RecyclerView.ViewHolder(itemView){
    fun bind(user: User, onClick:(name:String, photo:String, id:String)->Unit) = with(itemView){

        countTv.isVisible = false
        timeTv.isVisible = false
        titleTv.text = user.name
        subTitleTv.text = user.status
        if(user.thumbImage.isEmpty())
        {
            userImgView.setImageResource(R.color.action_bar_color)
            textname.visibility = View.VISIBLE
            textname.text = user.name[0].toString().toUpperCase(Locale.ROOT)
        }
        else
        {
            Picasso.get()
                .load(user.thumbImage)
                .placeholder(R.drawable.defaultavatar)
                .error(R.drawable.defaultavatar)
                .into(userImgView)
        }


        setOnClickListener {
            onClick.invoke(user.name,user.thumbImage,user.uid)
        }
    }
}