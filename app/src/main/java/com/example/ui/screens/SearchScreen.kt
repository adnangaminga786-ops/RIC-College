package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PostType
import com.example.ui.theme.*
import com.example.viewmodel.RICViewModel
import coil.compose.AsyncImage

@Composable
fun SearchScreen(
    viewModel: RICViewModel,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Videos", "Photos", "Documents", "Users", "Events")

    val posts by viewModel.posts.collectAsState()
    val events by viewModel.events.collectAsState()
    val chats by viewModel.chats.collectAsState()

    BackHandler {
        onClose()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavyDark)
            .statusBarsPadding()
    ) {
        // Search Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextWhite
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(CardNavy)
                    .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = NeonBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    androidx.compose.foundation.text.BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        textStyle = androidx.compose.ui.text.TextStyle(color = TextWhite, fontSize = 14.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_text_input"),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (query.isEmpty()) {
                                Text(
                                    text = "Search RIC videos, photos, docs, students...",
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }

            if (query.isNotEmpty()) {
                IconButton(onClick = { query = "" }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
                }
            }
        }

        // Category Filter Tabs
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSel = selectedCategory == cat
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSel) ElectricBlue else CardNavy,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) NeonBlue else GlassBorder),
                    modifier = Modifier.clickable { selectedCategory = cat }
                ) {
                    Text(
                        text = cat,
                        color = if (isSel) Color.White else TextLightGrey,
                        fontSize = 12.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Search Results List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Filter posts
            val matchedPosts = posts.filter {
                (selectedCategory == "All" ||
                 (selectedCategory == "Videos" && it.type == PostType.VIDEO) ||
                 (selectedCategory == "Photos" && it.type == PostType.PHOTO) ||
                 (selectedCategory == "Documents" && it.type == PostType.DOCUMENT)) &&
                (query.isBlank() || it.caption.contains(query, ignoreCase = true) || it.hashtags.any { h -> h.contains(query, ignoreCase = true) })
            }

            if (matchedPosts.isNotEmpty() && selectedCategory != "Users" && selectedCategory != "Events") {
                item {
                    Text("Posts & Media (${matchedPosts.size})", color = NeonBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                items(matchedPosts) { post ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, GlassBorder, RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = CardNavy)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (post.mediaUri != null) {
                                AsyncImage(
                                    model = post.mediaUri,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = post.mediaRes),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = post.authorName, color = NeonBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = post.caption, color = TextWhite, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(text = "${post.type.name} • ${post.timestamp}", color = TextMuted, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            // Filter Users
            val matchedUsers = chats.filter {
                (selectedCategory == "All" || selectedCategory == "Users") &&
                (query.isBlank() || it.participantName.contains(query, ignoreCase = true))
            }
            if (matchedUsers.isNotEmpty() && (selectedCategory == "All" || selectedCategory == "Users")) {
                item {
                    Text("Students & Users (${matchedUsers.size})", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                }
                items(matchedUsers) { user ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, GlassBorder, RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = CardNavy)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(ElectricBlue.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = NeonBlue)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = user.participantName, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(text = "${user.participantProgram} • RIC Thokar Campus", color = TextMuted, fontSize = 11.sp)
                            }
                            Button(
                                onClick = {
                                    viewModel.openChat(user)
                                    onClose()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Message", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Filter Events
            val matchedEvents = events.filter {
                (selectedCategory == "All" || selectedCategory == "Events") &&
                (query.isBlank() || it.title.contains(query, ignoreCase = true))
            }
            if (matchedEvents.isNotEmpty() && (selectedCategory == "All" || selectedCategory == "Events")) {
                item {
                    Text("College Events (${matchedEvents.size})", color = GoldenYellow, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                }
                items(matchedEvents) { ev ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, GlassBorder, RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = CardNavy)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(8.dp), color = DarkBlueAccent) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text(text = ev.month, color = GoldenYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(text = ev.day, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = ev.title, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = "${ev.time} • ${ev.location}", color = NeonBlue, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
