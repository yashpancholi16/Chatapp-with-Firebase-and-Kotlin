package com.example.mychat.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.mychat.R
import com.example.mychat.data.Users
import com.example.mychat.databinding.FragmentSettingsBinding
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso

class SettingsFragment : Fragment() {

    companion object {
        const val GALLERY_REQUEST_CODE = 786
    }

    lateinit var binding: FragmentSettingsBinding
    private var userReference: DatabaseReference? = null
    private var firebaseUser: FirebaseUser? = null
    private var storageRef: StorageReference? = null
    private var imageUri: Uri? = null
    private var isProfileImage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference =
            FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        handleProfile()
        return binding.root
    }

    private fun handleProfile() {
        userReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(Users::class.java)
                    binding.usernameSettings.text = user?.username
                    Picasso.get().load(user?.cover).into(binding.coverImageSettings)
                    Picasso.get().load(user?.profile).into(binding.profileImageSettings)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }
        })

        binding.profileImageSettings.setOnClickListener {
            isProfileImage = true
            uploadPhoto()
        }

        binding.coverImageSettings.setOnClickListener {
            isProfileImage = false
            uploadPhoto()
        }
    }

    private fun uploadPhoto() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode ==
            Activity.RESULT_OK && data!!.data != null
        ) {
            imageUri = data.data
            Toast.makeText(requireContext(), "Uploading Image....", Toast.LENGTH_SHORT).show()
            uploadImageToDataBase()
        }
    }

    private fun uploadImageToDataBase() {
        val progressBar = ProgressDialog(requireContext())
        progressBar.setTitle("Image is uploading, Please wait....")
        progressBar.show()
        imageUri?.let { uri ->
            val fileRef = storageRef?.child(System.currentTimeMillis().toString() + ".jpg")
            val uploadTask = fileRef?.putFile(uri)
            uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                if (!it.isSuccessful) {
                    it.exception?.let {exception ->
                        throw exception
                    }
                }
                return@Continuation fileRef.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()
                    val map = HashMap<String, Any>()
                    if (isProfileImage) {
                        map["profile"] = url
                    } else {
                        map["cover"] = url
                    }
                    userReference?.updateChildren(map)
                    progressBar.dismiss()
                }
            }
        }
    }
}