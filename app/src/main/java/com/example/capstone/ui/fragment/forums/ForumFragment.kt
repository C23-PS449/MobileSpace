package com.example.capstone.ui.fragment.forums

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.R
import com.example.capstone.adapter.MessageAdapter
import com.example.capstone.databinding.FragmentForumBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Date
import com.firebase.ui.database.FirebaseRecyclerOptions


class ForumFragment : Fragment() {
    private var _binding:FragmentForumBinding?=null
    private val binding get()= _binding!!
    private lateinit var auth:FirebaseAuth
    private lateinit var db:FirebaseDatabase
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentForumBinding.inflate(inflater,container,false)
        auth= Firebase.auth
        val firebaseUser=auth.currentUser

        db=Firebase.database
        val messagesReference=db.reference.child(MESSAGES_CHILD)

        binding.sendButton.setOnClickListener {
            val usermessage= Message(
                binding.chatMessage.text.toString(),
                firebaseUser?.displayName.toString(),
                firebaseUser?.photoUrl.toString(),
                Date().time
            )
            messagesReference.push().setValue(usermessage){error, _ ->
                if(error != null){
                    Toast.makeText(context,getString(R.string.gagal_mengirim_pesan)+error.message,Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context,getString(R.string.pesan_berhasil_terkirim),Toast.LENGTH_SHORT).show()
                }
            }
            binding.chatMessage.setText("")
        }
        val manager= LinearLayoutManager(requireContext())
        manager.stackFromEnd = true
        binding.idRecycler.layoutManager=manager

        val options=FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(messagesReference,Message::class.java)
            .build()
        adapter= MessageAdapter(options,firebaseUser?.displayName)
        binding.idRecycler.adapter=adapter

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }
    override fun onPause() {
        adapter.stopListening()
        super.onPause()

    }

    companion object{
        const val MESSAGES_CHILD="messages"
    }

}