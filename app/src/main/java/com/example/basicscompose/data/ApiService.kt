package com.example.basicscompose.data

import com.example.basicscompose.data.models.Post
import com.example.basicscompose.data.models.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("/getUid")
    suspend fun getUid(): String?

    @GET("getPostsForUser/{uid}")
    suspend fun getPostsForProfile(@Path("uid") id: String): Response<List<Post>>

    @GET("getUser/{id}")
    suspend fun getUserById(@Path("id") id: String): User?

}