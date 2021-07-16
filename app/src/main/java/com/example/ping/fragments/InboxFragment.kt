package com.example.ping.fragments

import android.content.Intent
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedList

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ping.*
import com.example.ping.models.Inbox
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.paging.DatabasePagingOptions
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter
import com.firebase.ui.database.paging.LoadingState

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_chat.*


class inboxFragment : Fragment(R.layout.fragment_chat) {

    private lateinit var mAdapter: FirebaseRecyclerAdapter<Inbox, ChatViewHolder>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val mDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }



    private fun setupAdapter() {
     //   shimmershow()
        val baseQuery: Query = mDatabase.reference.child("chats").child(auth.uid!!).limitToLast(10)
      /*  val config = PagedList.Config.Builder()
            .setPrefetchDistance(2) // number of pages you want initally
            .setPageSize(1)
            .setEnablePlaceholders(false)
            .build()
        val options = DatabasePagingOptions.Builder<Inbox>()
            .setLifecycleOwner(this)
            .setQuery(baseQuery,config, Inbox::class.java)
            .build()*/
       // adapter = inboxAdapter(options)


       val options = FirebaseRecyclerOptions.Builder<Inbox>()
                .setLifecycleOwner(viewLifecycleOwner)
                .setQuery(baseQuery, Inbox::class.java)
                .build()
        // Instantiate Paging Adapter
        mAdapter = object : FirebaseRecyclerAdapter<Inbox, ChatViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
                return ChatViewHolder(layoutInflater.inflate(R.layout.list_item, parent, false))
            }

            override fun onBindViewHolder(
                    viewHolder: ChatViewHolder,
                    position: Int,
                    inbox: Inbox
            ) {

                viewHolder.bind(inbox = inbox) { name: String, photo: String, id: String ->
                    val intent = Intent(requireContext(), ChatActivity::class.java)
                    intent.putExtra(UID,id)
                    intent.putExtra(NAME,name)
                    intent.putExtra(IMAGE,photo)
                    startActivity(intent)
                }
            }

            override fun onDataChanged() {
                super.onDataChanged()
                if(mAdapter.itemCount == 0)
                {
                    nochatfound.visibility = View.VISIBLE
                    nochattxt.visibility = View.VISIBLE
                    explorechat.visibility = View.VISIBLE
                }
                else{
                    nochatfound.visibility = View.GONE
                    nochattxt.visibility = View.GONE
                    explorechat.visibility = View.GONE
                }
                shimmerhide()

            }

            override fun getItemCount(): Int {
                return super.getItemCount()
            }


            override fun onError(error: DatabaseError) {
                super.onError(error)
                shimmerhide()

            }

        }


    }

    override fun onStart() {
        super.onStart()
        mAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewManager = LinearLayoutManager(requireContext())
        shimmershow()
        setupAdapter()
        recyclerView.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = mAdapter

        }
        explorechat.setOnClickListener {
            (activity as MainActivity).viewpagerspecific()
        }


    }
    fun shimmerhide(){
        shimmer.stopShimmer()
        shimmer.visibility = View.GONE
    }
    fun shimmershow(){
        shimmer.startShimmer()
        shimmer.visibility = View.VISIBLE
    }

}