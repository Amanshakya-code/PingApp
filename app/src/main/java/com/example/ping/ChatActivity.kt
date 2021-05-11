package com.example.ping

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ping.adapter.ChatAdapter
import com.example.ping.models.Inbox
import com.example.ping.models.User
import com.example.ping.network.NotificationData
import com.example.ping.network.PushNotification
import com.example.ping.network.RetrofitInstance
import com.example.ping.utils.KeyBoardVisibilityUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.google.GoogleEmojiProvider
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val UID = "uid"
const val NAME = "name"
const val IMAGE = "photo"
const val TOPIC = "/topics/mytopic"
class ChatActivity : AppCompatActivity() {
    private var friendid:String? =""
    private var name:String?= ""
    private var image:String?=""
    private var mCurrentId:String?=""
    private val db: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
    private lateinit var keyboardVisibilityHelper: KeyBoardVisibilityUtil
    lateinit var currentUser: User
    lateinit var friendUser: User
    private val messages = mutableListOf<ChatEvent>()
    lateinit var chatAdapter: ChatAdapter
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmojiManager.install(GoogleEmojiProvider())
        setContentView(R.layout.activity_chat)

        friendid = intent.getStringExtra(UID)
        name = intent.getStringExtra(NAME)
        image = intent.getStringExtra(IMAGE)
        mCurrentId = FirebaseAuth.getInstance().uid!!

        keyboardVisibilityHelper = KeyBoardVisibilityUtil(rootView) {
            msgRv.scrollToPosition(messages.size - 1)
        }

        FirebaseFirestore.getInstance().collection("users").document(mCurrentId!!).get().addOnSuccessListener {
            currentUser = it.toObject(User::class.java)!!
            FirebaseMessaging.getInstance().subscribeToTopic(currentUser.deviceToken)
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        }
        FirebaseFirestore.getInstance().collection("users").document(friendid!!).get().addOnSuccessListener {
            friendUser = it.toObject(User::class.java)!!


        }
        FirebaseFirestore.getInstance().collection("users").document(friendid!!).addSnapshotListener { value, error ->
            error?.let {
                Log.i("eoo","$it")
                return@addSnapshotListener
            }
            value?.let {
                if(it.exists()){
                    val onlineState = it.getString("onlineStatus")
                    onlinestatus.text = onlineState
                }
            }

        }

        chatAdapter = ChatAdapter(messages,mCurrentId!!)
        msgRv.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatAdapter
        }
        nameTv.text = name
        Picasso.get().load(image).into(userImgView)
        nameTv.setOnClickListener {
            val intent = Intent(this,FriendProfileView::class.java)
            intent.putExtra(NAME,name)
            intent.putExtra(IMAGE,image)
            startActivity(intent)
        }

        val emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(msgEdtv)
        smileBtn.setOnClickListener {
            emojiPopup.toggle()
        }

        listenToMessages()
        sendBtn.setOnClickListener {
            msgEdtv.text?.let {
                if(it.isNotEmpty()){
                    sendMessage(it.toString())
                    it.clear()
                }
            }
        }
        updateReadCount()

    }

    private fun sendNotification(notification:PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try{
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful){
               // Log.i("response","response: ${Gson().toJson(response)}")
            }
            else
            {
                Log.i("responsefaild",response.errorBody().toString())
            }
        }
        catch (e:Exception){
            Log.i("reerror",e.toString())
        }
    }

    private fun updateReadCount() {
        getInbox(mCurrentId!!,friendid!!).child("count").setValue(0)
    }

    private fun sendMessage(msg: String) {
        val id = getMessages(friendid!!).push().key //uniquekey
        checkNotNull(id){
            "cannot be null"
        }
        val msgMap = Message(msg,mCurrentId!!,id,getId(friendid!!))
        getMessages(friendid!!).child(id).setValue(msgMap).addOnSuccessListener {

        }
        val currentusername = currentUser.name
        val messagetobesend = msg
        val friendToken = friendUser.deviceToken
        Log.i("details","$currentusername------$messagetobesend-------$friendToken")
        if(currentusername.isNotEmpty()&&messagetobesend.isNotEmpty()&&friendToken.isNotEmpty()){
            PushNotification(
                NotificationData(currentusername,messagetobesend),
                friendToken
            ).also {
                sendNotification(it)
            }
        }
        updateLastMessage(msgMap)
    }

    private fun updateLastMessage(message: Message) {
        val inboxMap = Inbox(
                message.msg,
                friendid!!,
                name!!,
                image!!,
                count = 0
        )
        getInbox(mCurrentId!!,friendid!!).setValue(inboxMap).addOnSuccessListener {
            getInbox(friendid!!,mCurrentId!!).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value  = snapshot.getValue(Inbox::class.java)
                    inboxMap.apply {
                        from = message.senderId
                        name = currentUser.name
                        image = currentUser.thumbImage
                        count = 1
                    }
                    value?.let {
                        if(it.from == message.senderId){
                            inboxMap.count = value.count+1
                        }
                    }
                    getInbox(friendid!!,mCurrentId!!).setValue(inboxMap)
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
    private fun listenToMessages(){
        getMessages(friendid!!)
                .orderByKey()
                .addChildEventListener(object:ChildEventListener{
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        val message = snapshot.getValue(Message::class.java)!!
                        addMessages(message)
                    }

                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                        Log.i("removed","change")
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {

                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                        TODO("Not yet implemented")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
    }

    private fun addMessages(msg: Message) {
        val eventBefore = messages.lastOrNull()
        if((eventBefore != null && !eventBefore.sendAt.isSameDayAs(msg.sendAt))|| eventBefore == null){
            messages.add(
                    DateHeader(
                            msg.sendAt,context = this,getId(friendid!!),friendid!!
                    )
            )
        }
        messages.add(msg)
        chatAdapter.notifyItemInserted(messages.size -1)
        msgRv.scrollToPosition(messages.size-1)
    }
    private fun getInbox(toUser:String,fromUser:String) = db.reference.child("chats/$toUser/$fromUser")
    private fun getMessages(friendId: String) = db.reference.child("messages/${getId(friendId)}")
    private fun getId(friendId:String):String
    {
        return if(friendId > mCurrentId.toString()){
            mCurrentId + friendId
        }
        else{
            friendId+mCurrentId
        }
    }
    private fun onlinestatus(status:String){
        val reference  =  FirebaseFirestore.getInstance().collection("users").document(mCurrentId!!)
        var hashMap: HashMap<String, Any> = HashMap<String, Any>()
        hashMap.put("onlineStatus",status)
        reference.update(hashMap)

    }
    override fun onStart() {
        super.onStart()
        onlinestatus("online")
        rootView.viewTreeObserver
                .addOnGlobalLayoutListener(keyboardVisibilityHelper.visibilityListener)
    }

    override fun onPause() {
        super.onPause()
        onlinestatus("offline")
        rootView.viewTreeObserver
                .addOnGlobalLayoutListener(keyboardVisibilityHelper.visibilityListener)
    }
}
//simplychange