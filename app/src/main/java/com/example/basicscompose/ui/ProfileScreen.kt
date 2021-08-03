package com.example.basicscompose.ui

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.request.ImageRequest
import com.example.basicscompose.R
import com.example.basicscompose.data.models.Post
import com.example.basicscompose.data.models.User
import com.example.basicscompose.util.Resource
import com.google.accompanist.coil.rememberCoilPainter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "ProfileScreen"

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun ProfileScreen(
    viewModel: MainViewModel
) {

    val scope = rememberCoroutineScope()

    var isFollowing by remember { mutableStateOf(false)}

    var showAnimation by remember { mutableStateOf(false)}
    var animationPadding by remember { mutableStateOf(0.dp) }

    val animDp = animateDpAsState(
        targetValue = animationPadding,
        tween(
            durationMillis = if(isFollowing) 2700 else 0,
            delayMillis = 500
        )
    )

    val user = produceState<User?>(initialValue = null) {
        viewModel.getUser("uniqueuid")
        viewModel.user.collect {
            value = it.data
        }
    }.value

    val postsState = produceState<Resource<List<Post>>>(initialValue = Resource.Loading()) {
        viewModel.getPosts("uniqueuid")
        viewModel.posts.collect {
            value = it
        }        
    }.value
    
    
    val numberOfPosts = animateIntAsState(
        targetValue = user?.posts ?: 0,
        tween(
            durationMillis = 3000
        )
    )

    val infiniteTransition = rememberInfiniteTransition()
    val buttonColor by infiniteTransition.animateColor(
        initialValue = if(!isFollowing) Color(0XFF033BC8) else Color(0XFF3FF5FF),
        targetValue = if(!isFollowing) Color(0XFFFB02B7) else Color(0xFFD7FF3F),
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 1200),
            repeatMode = RepeatMode.Reverse
        )
    )


    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        TopBar(
            username = user?.username ?: "user is null",
            modifier = Modifier.padding(10.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        ProfileSection(
            user = user,
            numberOfPosts = numberOfPosts.value,
            animDp = animDp.value,
            showAnimation = showAnimation
        ) { animationDP, isAnimationVisible ->
            animationPadding = animationDP
            showAnimation = isAnimationVisible
        }
        Spacer(modifier = Modifier.height(24.dp))
        FollowButton(
            color = buttonColor,
            isFollowing = isFollowing
        ) {
            isFollowing = it
            showAnimation = isFollowing
            animationPadding = if(isFollowing) 130.dp else 0.dp
        }
        Spacer(modifier = Modifier.height(20.dp))
        when(postsState) {
            is Resource.Success -> {
                PostSection(posts = postsState.data!!)
            }   
            is Resource.Error -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = postsState.message ?: "Error",
                        fontSize = 22.sp
                    )
                }
            }
            is Resource.Loading -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colors.primary)
                }

            }
            else -> Unit
        }
        

    }
    
}

@Composable
fun TopBar(
    username: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            modifier = modifier.size(24.dp)
        )
        Spacer(modifier = modifier.width(32.dp))
        Text(
            text = username,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            overflow = TextOverflow.Ellipsis
        )
    }
    
    
    
    

}


@ExperimentalAnimationApi
@Composable
fun ProfileSection(
    user: User?,
    numberOfPosts: Int,
    animDp: Dp,
    showAnimation: Boolean,
    modifier: Modifier = Modifier,
    onAnimEnd: (Dp, Boolean) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            RoundImage(
                request = ImageRequest.Builder(LocalContext.current)
                    .data(user?.profileImgUrl)
                    .build(),
                modifier = Modifier
                    .weight(30f)
                    .size(100.dp)
            )
            StatSection(
                user = user,
                numberOfPosts = numberOfPosts,
                modifier = Modifier.weight(70f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        ProfileDescription(
            user = user,
            animDp = animDp,
            showAnimation = showAnimation,
            onAnimEnd = onAnimEnd
        )

        
    }
}


@Composable
fun StatSection(
    user: User?,
    numberOfPosts: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        ProfileStat(number = numberOfPosts, name = "Posts")
        ProfileStat(number = user?.followers?.size ?: 0, name = "Followers")
        ProfileStat(number = user?.following?.size ?: 0, name = "Following")
    }
}

@Composable
fun FollowButton(
    color: Color,
    isFollowing: Boolean,
    modifier: Modifier = Modifier,
    onClick: (Boolean) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { onClick(!isFollowing) },
            colors = ButtonDefaults.buttonColors(backgroundColor = color),
            modifier = Modifier
                .width(250.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Text(
                text = if(!isFollowing) "Follow" else "Unfollow",
                fontSize = 18.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }

}


@Composable
fun ProfileStat(
    number: Int,
    name: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(
            text = number.toString(),
            fontSize = 20.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = name,
            fontSize = 20.sp,
            color = Color.Black
        )
    }
}


@ExperimentalAnimationApi
@Composable
fun ProfileDescription(
    user: User?,
    animDp: Dp,
    showAnimation: Boolean,
    modifier: Modifier = Modifier,
    onAnimEnd: (Dp, Boolean) -> Unit
) {

    when(animDp) {
        130.dp -> {
            onAnimEnd(0.dp, false)
        }
        else -> {}
    }

    Column {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .weight(8f)
            ) {
                Text(
                    text = user?.username ?: "",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = user?.description ?: "",
                    fontSize = 20.sp
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1.5f)
                    .padding(bottom = animDp)
            ) {
                AnimatedVisibility(
                    visible = showAnimation,
                    enter = fadeIn() + expandIn(),
                    exit = fadeOut() + shrinkOut()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.heart),
                        contentDescription = null
                    )
                }

            }
        }
    }
}



@Composable
fun RoundImage(
    request: ImageRequest,
    modifier: Modifier = Modifier
) {
    Image(
        painter = rememberCoilPainter(request = request),
        contentScale = ContentScale.Crop,
        contentDescription = null,
        modifier = modifier
            .aspectRatio(1f, matchHeightConstraintsFirst = true)
            .clip(CircleShape)
    )

}

@ExperimentalFoundationApi
@Composable
fun PostSection(
    posts: List<Post>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(3),
        modifier = modifier.scale(1.01f)
    ) {
        items(posts) {
            Image(
                painter = rememberCoilPainter(request = ImageRequest.Builder(LocalContext.current)
                    .data(it.imgUrl)
                    .build()),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(1f)
                    .border(1.dp, Color.White)
            )
        }
    }
}







