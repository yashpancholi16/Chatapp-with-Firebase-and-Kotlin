package com.example.mychat.data

import com.google.gson.annotations.SerializedName

data class Users(
    @SerializedName("uid") val uid: String = "",
    @SerializedName("username") val username: String = "",
    @SerializedName("profile") val profile: String = "",
    @SerializedName("cover") val cover: String = "",
    @SerializedName("status") val status: String = "",
    @SerializedName("search") val search: String = "",
    @SerializedName("facebook") val facebook: String = "",
    @SerializedName("instagram") val instagram: String = "",
    @SerializedName("website") val website: String = ""
)

