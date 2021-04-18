package com.example.mychat.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.mychat.MainActivity
import com.example.mychat.R
import com.example.mychat.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var users: DatabaseReference
    private var firebaseUserId = ""
    lateinit var binding: FragmentSignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false)
        binding.navigationBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.btnRegister.setOnClickListener {
            registerUser()
        }
        return binding.root
    }

    private fun registerUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (binding.etUsername.text.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {

                        firebaseUserId = firebaseAuth.currentUser!!.uid
                        users = FirebaseDatabase.getInstance().reference.child("Users")
                            .child(firebaseUserId)

                        users.updateChildren(prepareData()).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val intent = Intent(requireActivity(), MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), it.exception?.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    private fun prepareData(): HashMap<String, Any> {
        val userDatabase = HashMap<String, Any>()
        userDatabase["uid"] = firebaseUserId
        userDatabase["username"] = binding.etUsername.text.toString().trim()
        userDatabase["profile"] =
            "https://firebasestorage.googleapis.com/v0/b/my-chat-5453d.appspot.com/o/IMG_20201213_155140_Bokeh.jpg?alt=media&token=6fbbece5-d0a7-40fd-900f-4c74a0320887"
        userDatabase["cover"] =
            "https://firebasestorage.googleapis.com/v0/b/my-chat-5453d.appspot.com/o/Yash.jpg?alt=media&token=36f98a69-54ac-456d-9266-ed22b65ea323"
        userDatabase["status"] = "Offline"
        userDatabase["search"] =
            binding.etUsername.text.toString().trim().toLowerCase()
        userDatabase["facebook"] = "https://facebook.com"
        userDatabase["instagram"] = "https://instagram.com"
        userDatabase["website"] = "https://google.com"
        return userDatabase
    }
}