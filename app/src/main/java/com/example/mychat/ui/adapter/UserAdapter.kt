package com.example.mychat.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mychat.R
import com.example.mychat.data.Users
import com.example.mychat.databinding.UserSearchItemLayoutBinding
import com.squareup.picasso.Picasso

class UserAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mList = ArrayList<Users>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return UserViewHolder(
            DataBindingUtil.inflate(
                inflater,
                R.layout.user_search_item_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as UserViewHolder).bind(mList[position])
    }

    override fun getItemCount(): Int = mList.size

    fun updateUI(list: ArrayList<Users>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    inner class UserViewHolder(val binding: UserSearchItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(props: Users) {
            binding.username.text = props.username
            Picasso.get().load(props.profile).placeholder(R.drawable.ic_profile).into(binding.profileImage)
        }
    }
}