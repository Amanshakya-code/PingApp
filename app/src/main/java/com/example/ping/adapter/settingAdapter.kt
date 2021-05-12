package com.example.ping.adapter

import android.app.Activity
import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.ping.R
import com.example.ping.models.settingitem
import kotlinx.android.synthetic.main.setting_single_item.view.*

class settingAdapter (var context:Activity,var items:List<settingitem>):ArrayAdapter<settingitem>(context,R.layout.setting_single_item,items){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = LayoutInflater.from(context).inflate(R.layout.setting_single_item,null)
        val image = view.findViewById<ImageView>(R.id.settingicon)
        val text = view.findViewById<TextView>(R.id.settingTxt)
        var mItem:settingitem = items[position]
        image.setImageDrawable(context.resources.getDrawable(mItem.image))
        text.text = mItem.text
        return view
    }
}