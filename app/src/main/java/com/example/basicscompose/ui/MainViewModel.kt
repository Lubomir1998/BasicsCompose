package com.example.basicscompose.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basicscompose.data.models.Post
import com.example.basicscompose.data.models.User
import com.example.basicscompose.repositories.MainRepository
import com.example.basicscompose.util.Constants.NO_UID
import com.example.basicscompose.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel() {

    private val _uid = MutableStateFlow(NO_UID)
    val uid: StateFlow<String> = _uid

    private val _posts = MutableStateFlow<Resource<List<Post>>>(Resource.Empty())
    val posts: StateFlow<Resource<List<Post>>> = _posts

    private val _user = MutableStateFlow<Resource<User>>(Resource.Empty())
    val user: StateFlow<Resource<User>> = _user


    fun getUser(uid: String) {
        _user.value = Resource.Loading()
        viewModelScope.launch {
            val response = repository.getUser(uid)
            val user = response.data
            _user.value = if(user != null) {
                Resource.Success(user)
            } else {
                Resource.Error(response.message ?: "No user found")
            }
        }
    }

    fun getPosts(uid: String) {
        _posts.value = Resource.Loading()
        viewModelScope.launch {
            val response = repository.getPostsForProfile(uid)
            val posts = response.data
            _posts.value = if(posts != null) {
                Resource.Success(posts)
            } else {
                Resource.Error(response.message ?: "No user found")
            }
        }
    }

}