package com.example.ping.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ping.*
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.list_item_chat_recv_message.view.content
import kotlinx.android.synthetic.main.list_item_chat_recv_message.view.time
import kotlinx.android.synthetic.main.list_item_chat_sent_message.view.*
import kotlinx.android.synthetic.main.list_item_date_header.view.*

class ChatAdapter(private val list:MutableList<ChatEvent>, private val mCurrentUid:String):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var messagesendlayout:MaterialCardView
    lateinit var messagerecvlayout:MaterialCardView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = {layout:Int ->
            LayoutInflater.from(parent.context).inflate(layout,parent,false)
        }
        return when(viewType){
            TEXT_MESSAGE_RECEIVED ->{
                MessageViewHolder(inflate(R.layout.list_item_chat_recv_message))
            }
            TEXT_MESSAGE_SENT ->{
                MessageViewHolder(inflate(R.layout.list_item_chat_sent_message))
            }
            DATE_HEADER ->{
                MessageViewHolder(inflate(R.layout.list_item_date_header))
            }
            else-> MessageViewHolder(inflate(R.layout.list_item_chat_recv_message))
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = list[position]){
            is DateHeader ->{
                holder.itemView.textView.text = item.date
            }
            is Message ->{
                holder.itemView.apply{
                    content.text = item.msg
                    time.text = item.sendAt.formatAsTime()
                    try{
                        messagesendlayout = findViewById(R.id.materialCardView3)
                        messagesendlayout?.setOnClickListener {
                            var dialogBuilder = AlertDialog.Builder(context)
                            dialogBuilder.setTitle("Delete")
                            dialogBuilder.setMessage("Are you sure to delete this message ?")
                                    .setCancelable(true)
                                    .setPositiveButton("Delete",DialogInterface.OnClickListener(){
                                        dialog, which -> deleteMessages(position)
                                    })
                                    .setNegativeButton("No",DialogInterface.OnClickListener(){
                                        dialog, which -> dialog.dismiss()
                                    })
                            val alert = dialogBuilder.create()
                            alert.show()
                        }
                    }catch (e:Exception)
                    {
                        Log.i("erroe","$e")
                    }
                    try {
                        messagerecvlayout = findViewById(R.id.messageCardView)
                        messagerecvlayout?.setOnClickListener {
                            var dialogBuilder = AlertDialog.Builder(context)
                            dialogBuilder.setTitle("Sorry")
                            dialogBuilder.setMessage("You can only delete your own messages !")
                                    .setCancelable(true)
                                    .setPositiveButton("Ok",DialogInterface.OnClickListener(){
                                        dialog, which -> dialog.dismiss()
                                    })
                            val alert = dialogBuilder.create()
                            alert.show()
                        }
                    }catch (e:Exception)
                    {
                        Log.i("erroe","$e")
                    }

                   try {
                       if(position == list.size-1){
                           if(item.status == 2){
                               messageStatus.text = "Seen"
                           }
                           else{
                               messageStatus.text = "Delivered"
                           }
                       }else{
                           messageStatus.visibility = View.GONE
                       }
                   }
                   catch (e:Exception){
                       Log.i("SMerror","error = $e")
                   }
                }
            }
        }
    }
    private fun deleteMessages(position: Int) {
        var msgtime = list.get(position).combineId
        var msgid = list.get(position).msgId
        FirebaseDatabase.getInstance().getReference("messages/$msgtime").child("$msgid").removeValue().addOnCompleteListener {
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(val event = list[position]){
            is Message -> {
                if(event.senderId == mCurrentUid){
                    TEXT_MESSAGE_SENT
                }
                else
                {
                    TEXT_MESSAGE_RECEIVED
                }
            }
            is DateHeader -> DATE_HEADER
            else -> UNSUPPORTED
        }
    }
    class DateViewHolder(view: View):RecyclerView.ViewHolder(view)
    class MessageViewHolder(view: View):RecyclerView.ViewHolder(view)
    companion object{
        //static variables of this class
        private const val UNSUPPORTED = -1
        private const val  TEXT_MESSAGE_RECEIVED = 0
        private const val TEXT_MESSAGE_SENT = 1
        private const val DATE_HEADER = 2
    }
}