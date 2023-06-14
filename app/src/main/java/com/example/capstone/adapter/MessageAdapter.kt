package com.example.capstone.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.capstone.R
import com.example.capstone.databinding.ItemChatBinding
import com.example.capstone.ui.fragment.forums.Message
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class MessageAdapter(
    options:FirebaseRecyclerOptions<Message>,
    private val currentUserName:String?
): FirebaseRecyclerAdapter<Message, MessageAdapter.MessageViewHolder>(options) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageViewHolder {
       val inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.item_chat,parent,false)
        val binding=ItemChatBinding.bind(view)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MessageViewHolder,
        position: Int,
        model: Message
    ) {
        holder.bind(model)
    }

    inner class MessageViewHolder(private val binding:ItemChatBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item:Message){
            binding.tvMessage.text=item.text
            setTextColor(item.name,binding.tvMessage)
            binding.tvMessenger.text=item.name
            Glide.with(itemView.context)
                .load(item.photoUrl)
                .circleCrop()
                .into(binding.ivMessenger)
            if(item.timestamp != null){
                binding.tvTimestamp.text= DateUtils.getRelativeTimeSpanString(item.timestamp)
            }
        }
    }

    private fun setTextColor(userName:String?, textView: TextView){
        if (currentUserName==userName && userName != null){
            textView.setBackgroundResource(R.drawable.rounded_message_darkgreen)
        }else{
            textView.setBackgroundResource(R.drawable.rounded_message_lightgreen)
        }
    }

}