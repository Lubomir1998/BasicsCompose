package com.example.basicscompose.data.models

import com.example.basicscompose.util.Constants.DEFAULT_PROFILE_IMG_URL
import com.google.gson.annotations.Expose
import java.util.*

data class User(
    val email: String = "",
    val username: String,
    val description: String = "",
    val profileImgUrl: String = DEFAULT_PROFILE_IMG_URL,
    var following: List<String> = listOf(),
    var followers: List<String> = listOf(),
    var posts: Int = 0,
    @Expose(serialize = false, deserialize = false)
    var isFollowing: Boolean = false,
    val uid: String = UUID.randomUUID().toString()
)