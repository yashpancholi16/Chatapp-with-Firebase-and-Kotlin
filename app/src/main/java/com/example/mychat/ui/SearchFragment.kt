package com.example.mychat.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mychat.R
import com.example.mychat.data.Users
import com.example.mychat.databinding.FragmentSearchBinding
import com.example.mychat.ui.adapter.UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    lateinit var userAdapter: UserAdapter
    val userList = ArrayList<Users>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        userAdapter = UserAdapter(requireContext())
        binding.searchList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        }
        fetchAllUsers()

        binding.searchUsersET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Unit
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchUser(s.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable?) {
                Unit
            }
        })
        return binding.root
    }


    private fun fetchAllUsers() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser?.uid
        val users = FirebaseDatabase.getInstance().reference.child("Users")

        users.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                if (binding.searchUsersET.text.isNullOrEmpty()) {
                    snapshot.children.forEach {
                        val user = it.getValue(Users::class.java)
                        if (!user?.uid.equals(firebaseUser)) {
                            user?.let {
                                userList.add(it)
                            }
                        }
                    }
                }
                userAdapter.updateUI(userList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun searchUser(text: String) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser?.uid
        val queryUsers =
            FirebaseDatabase.getInstance().reference.child("Users").orderByChild("search")
                .startAt(text).endAt(text + "\uf8ff")

        queryUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                snapshot.children.forEach {
                    val user = it.getValue(Users::class.java)
                    if (!user?.uid.equals(firebaseUser)) {
                        user?.let {
                            userList.add(it)
                        }
                    }
                }
                userAdapter.updateUI(userList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}