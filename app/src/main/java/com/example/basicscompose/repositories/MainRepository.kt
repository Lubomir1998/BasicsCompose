package com.example.basicscompose.repositories

import com.example.basicscompose.data.ApiService
import com.example.basicscompose.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val api: ApiService
) {

    suspend fun getUid() = api.getUid()

    suspend fun getPostsForProfile(uid: String) = withContext(Dispatchers.IO) {
        try {
            val response = api.getPostsForProfile(uid).body()
            if(response != null) {
                Resource.Success(response)
            } else {
                Resource.Error("Error occurred")
            }

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error occurred")
        }

    }

    suspend fun getUser(uid: String) = withContext(Dispatchers.IO) {
        try {
            val user = api.getUserById(uid)
            user?.let {
                Resource.Success(it)
            } ?: Resource.Error("No user")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error occurred")
        }
    }

}