package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.model.ChatConversation
import com.example.model.MessageType
import com.example.model.Story
import com.example.ui.components.RICHeader
import com.example.ui.theme.*
import com.example.viewmodel.RICViewModel

@Composable
fun FriendsScreen(
    viewModel: RICViewModel,
    modifier: Modifier = Modifier
) {
    val stories by viewModel.stories.collectAsState()
    val chats by viewModel.chats.collectAsState()
    val groups by viewModel.officialGroups.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val context = LocalContext.current

    var selectedFilter by remember { mutableStateOf("All Friends") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddFriendDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavyDark)
    ) {
        // RIC Header
        RICHeader(
            onNotificationClick = { viewModel.setShowNotifications(true) },
            unreadCount = notifications.count { !it.isRead }
        )

        // Search Bar for friends & chats
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(CardNavy)
                .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                androidx.compose.foundation.text.BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    textStyle = androidx.compose.ui.text.TextStyle(color = TextWhite, fontSize = 13.sp),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search friends or chats...",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }
                        innerTextField()
                    }
                )
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { searchQuery = "" },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        val displayedChats = remember(chats, selectedFilter, searchQuery) {
            val filtered = if (selectedFilter == "Online") chats.filter { it.isOnline } else chats
            if (searchQuery.isNotBlank()) {
                filtered.filter {
                    it.participantName.contains(searchQuery, ignoreCase = true) ||
                    it.lastMessage.contains(searchQuery, ignoreCase = true)
                }
            } else filtered
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("friends_page_list")
        ) {
            // Section 1: Stories / Status Carousel
            item {
                Text(
                    text = "Stories / Status",
                    color = TextLightGrey,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 6.dp)
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // + Add Status circle
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    viewModel.selectBottomTab(2) // Switch to upload
                                }
                                .testTag("add_status_button")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(CircleShape)
                                    .border(
                                        2.dp,
                                        Brush.linearGradient(listOf(ElectricBlue, NeonCyan)),
                                        CircleShape
                                    )
                                    .background(CardNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Status",
                                    tint = NeonBlue,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Add Status",
                                color = TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Friends' Statuses
                    if (stories.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = CardNavy,
                                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                                modifier = Modifier
                                    .height(64.dp)
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "No active stories • Tap + to share your day",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(stories) { story ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { viewModel.openStory(story) }
                                    .testTag("story_${story.id}")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(62.dp)
                                        .clip(CircleShape)
                                        .border(
                                            2.5.dp,
                                            Brush.sweepGradient(
                                                listOf(NeonBlue, ElectricBlue, NeonPurple, NeonCyan, NeonBlue)
                                            ),
                                            CircleShape
                                        )
                                        .padding(3.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = story.mediaRes),
                                        contentDescription = story.userName,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .align(Alignment.BottomEnd)
                                            .clip(CircleShape)
                                            .background(OnlineGreen)
                                            .border(1.5.dp, DeepNavyDark, CircleShape)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = story.userName.split(" ").firstOrNull() ?: story.userName,
                                    color = TextWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Normal,
                                    maxLines = 1
                                )
                                Text(
                                    text = story.timestamp,
                                    color = TextMuted,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }

            // Section 2: Filter Pills (All Friends, Online, Groups, Requests)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterPill(
                        label = "All Friends",
                        badge = "${chats.size}",
                        isSelected = selectedFilter == "All Friends",
                        onClick = { selectedFilter = "All Friends" }
                    )
                    FilterPill(
                        label = "Online",
                        badge = "${chats.count { it.isOnline }}",
                        isSelected = selectedFilter == "Online",
                        onClick = { selectedFilter = "Online" },
                        dotColor = OnlineGreen
                    )
                    FilterPill(
                        label = "Groups",
                        badge = "${groups.size}",
                        isSelected = selectedFilter == "Groups",
                        onClick = { selectedFilter = "Groups" }
                    )
                    FilterPill(
                        label = "Requests",
                        badge = "0",
                        isSelected = selectedFilter == "Requests",
                        onClick = { selectedFilter = "Requests" }
                    )
                }
            }

            // Section 3: Prominent Content based on filter
            if (selectedFilter == "Groups") {
                // Official Admin-Created Groups only
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Official College Groups",
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DarkBlueAccent.copy(alpha = 0.5f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                            ) {
                                Text(
                                    text = "Admin Managed",
                                    color = NeonBlue,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Created and moderated exclusively by College Administration",
                            color = TextMuted,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                        )
                    }
                }

                if (groups.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, GlassBorder, RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = CardNavy)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = null,
                                    tint = NeonBlue,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "No Official Groups Yet",
                                    color = TextWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Official college groups are created by College Administration. When announced, they will appear here.",
                                    color = TextMuted,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(groups) { group ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 5.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, GlassBorder, RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = CardNavy)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(ElectricBlue.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Groups,
                                        contentDescription = null,
                                        tint = NeonBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = group.name,
                                            color = TextWhite,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Official",
                                            tint = NeonBlue,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                    Text(
                                        text = "${group.membersCount} • ${group.description}",
                                        color = TextMuted,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Button(
                                    onClick = { viewModel.toggleGroupJoin(group.id) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (group.isJoined) CardNavyVariant else ElectricBlue,
                                        contentColor = if (group.isJoined) NeonBlue else Color.White
                                    ),
                                    shape = RoundedCornerShape(20.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text(
                                        text = if (group.isJoined) "Joined" else "Join",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (selectedFilter == "Requests") {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = CardNavy)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAddDisabled,
                                contentDescription = null,
                                tint = NeonBlue,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No Pending Friend Requests",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "When students send you friend requests, you can accept or decline them here.",
                                color = TextMuted,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // All Friends / Online filter
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                tint = NeonBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (selectedFilter == "Online") "Online Friends" else "Friends & Chats",
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // "+ Add Friend" Button
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = ElectricBlue,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonBlue),
                            modifier = Modifier
                                .clickable { showAddFriendDialog = true }
                                .testTag("add_friend_header_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "+ Add Friend",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                if (displayedChats.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .border(1.dp, GlassBorder, RoundedCornerShape(18.dp)),
                            colors = CardDefaults.cardColors(containerColor = CardNavy)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(CardNavyVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PersonAdd,
                                        contentDescription = null,
                                        tint = NeonBlue,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (selectedFilter == "Online") "No Friends Online" else "No Friends Added Yet",
                                    color = TextWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "All pre-loaded contacts have been removed. Tap '+ Add Friend' below to add fellow students from RIC Thokar Campus.",
                                    color = TextMuted,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { showAddFriendDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier.testTag("add_friend_empty_btn")
                                ) {
                                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add Friend", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    items(displayedChats) { chat ->
                        ChatListItem(
                            chat = chat,
                            onClick = { viewModel.openChat(chat) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }

    // Add Friend Dialog
    if (showAddFriendDialog) {
        var friendName by remember { mutableStateOf("") }
        var friendStudentId by remember { mutableStateOf("") }
        var friendProgram by remember { mutableStateOf("BS Computer Science") }

        Dialog(onDismissRequest = { showAddFriendDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = CardNavy,
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Add College Friend",
                            color = TextWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showAddFriendDialog = false }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextLightGrey)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Enter classmate's details from Riphah International College, Thokar Campus.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Student Name
                    OutlinedTextField(
                        value = friendName,
                        onValueChange = { friendName = it },
                        placeholder = { Text("Friend's Full Name (e.g. Usman Tariq)", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = CardNavyVariant,
                            unfocusedContainerColor = CardNavyVariant,
                            focusedBorderColor = NeonBlue,
                            unfocusedBorderColor = GlassBorder
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Student ID
                    OutlinedTextField(
                        value = friendStudentId,
                        onValueChange = { friendStudentId = it },
                        placeholder = { Text("Student ID (e.g. RIC-2025-045)", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = CardNavyVariant,
                            unfocusedContainerColor = CardNavyVariant,
                            focusedBorderColor = NeonBlue,
                            unfocusedBorderColor = GlassBorder
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Program Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val programs = listOf("BSCS", "BBA", "BS English", "FSc")
                        programs.forEach { prog ->
                            val isSel = friendProgram.contains(prog)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) ElectricBlue else CardNavyVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) NeonBlue else GlassBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { friendProgram = prog }
                            ) {
                                Text(
                                    text = prog,
                                    color = if (isSel) Color.White else TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 7.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (friendName.isNotBlank()) {
                                viewModel.addFriend(friendName, friendStudentId, friendProgram)
                                showAddFriendDialog = false
                                Toast.makeText(context, "$friendName added to your Friends!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Please enter student name", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Friend & Chat", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatListItem(
    chat: ChatConversation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("chat_item_${chat.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with online status
        Box(modifier = Modifier.size(52.dp)) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(CardNavyVariant)
                    .border(1.dp, GlassBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = chat.participantName,
                    tint = NeonBlue,
                    modifier = Modifier.size(28.dp)
                )
            }
            if (chat.isOnline) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(OnlineGreen)
                        .border(2.dp, DeepNavyDark, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name and Message Preview
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.participantName,
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = chat.lastTimestamp,
                    color = if (chat.unreadCount > 0) NeonBlue else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = if (chat.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.lastMessage,
                    color = if (chat.unreadCount > 0) TextWhite else TextMuted,
                    fontSize = 12.sp,
                    fontWeight = if (chat.unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (chat.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(ElectricBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${chat.unreadCount}",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterPill(
    label: String,
    badge: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    dotColor: Color? = null
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) ElectricBlue else CardNavy,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) NeonBlue else GlassBorder
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (dotColor != null) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
            Text(
                text = label,
                color = if (isSelected) Color.White else TextLightGrey,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isSelected) DeepNavyDark.copy(alpha = 0.5f) else CardNavyVariant)
                    .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
                Text(
                    text = badge,
                    color = if (isSelected) NeonBlue else TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
