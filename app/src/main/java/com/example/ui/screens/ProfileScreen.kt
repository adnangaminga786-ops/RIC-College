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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Post
import com.example.model.PostType
import com.example.ui.components.RICHeader
import com.example.ui.theme.*
import com.example.viewmodel.RICViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: RICViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsState()
    val allPosts by viewModel.posts.collectAsState()
    val context = LocalContext.current

    // Filter user's posts, liked posts, and saved posts
    val userPosts = remember(allPosts, user.id) {
        allPosts.filter { it.authorId == user.id }
    }
    val likedPosts = remember(allPosts) {
        allPosts.filter { it.isLiked }
    }
    val savedPosts = remember(allPosts) {
        allPosts.filter { it.isSaved }
    }
    val userVideos = remember(allPosts, user.id) {
        allPosts.filter { it.type == PostType.VIDEO && (it.authorId == user.id || it.isLiked) }
    }
    val userDocuments = remember(allPosts, user.id) {
        allPosts.filter { it.type == PostType.DOCUMENT && (it.authorId == user.id || it.isSaved) }
    }

    // Total likes received across user's uploaded posts, or total liked posts
    val totalLikesCount = remember(userPosts, likedPosts) {
        val earned = userPosts.sumOf { it.likesCount }
        if (earned > 0) "$earned" else "${likedPosts.size}"
    }

    var selectedTab by remember { mutableStateOf("My Posts") }
    var showSettingsSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavyDark)
    ) {
        // Top App Bar with official RIC Header and Settings Gear Icon at top right
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = DeepNavyDark.copy(alpha = 0.95f),
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.5.dp, NeonBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_ric_logo),
                            contentDescription = "RIC Logo",
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Student Profile",
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Riphah International College",
                            color = NeonBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Settings Icon at Top Right (as explicitly requested by user)
                IconButton(
                    onClick = { showSettingsSheet = true },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(CardNavy)
                        .border(1.dp, GlassBorder, CircleShape)
                        .testTag("profile_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings & Menu",
                        tint = NeonBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("profile_scroll_view"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Profile Card Header (Avatar, Names, Badges, Bio)
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Avatar with Glowing Border
                    Box(modifier = Modifier.size(92.dp)) {
                        Box(
                            modifier = Modifier
                                .size(92.dp)
                                .clip(CircleShape)
                                .border(
                                    2.5.dp,
                                    Brush.sweepGradient(listOf(NeonBlue, ElectricBlue, NeonCyan, NeonBlue)),
                                    CircleShape
                                )
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_campus_life),
                                contentDescription = "Student Avatar",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Verified Student Badge
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(ElectricBlue)
                                .border(2.dp, DeepNavyDark, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = user.name,
                        color = TextWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "@${user.username} • ${user.studentId}",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Badges: Student & Thokar Campus
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ElectricBlue,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonBlue)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Student",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CardNavy,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = NeonBlue,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = user.campus,
                                    color = TextLightGrey,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = user.bio,
                        color = TextLightGrey,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats Row: Posts | Followers | Following | Likes
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardNavy)
                            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(count = "${userPosts.size}", label = "Posts")
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(GlassBorder)
                        )
                        StatItem(count = user.followersCount, label = "Followers")
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(GlassBorder)
                        )
                        StatItem(count = user.followingCount, label = "Following")
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(GlassBorder)
                        )
                        StatItem(count = totalLikesCount, label = "Likes", tint = LikePink)
                    }
                }
            }

            // Profile Tabs: My Posts, Videos, Liked, Saved, Documents
            item {
                val profileTabs = listOf("My Posts", "Videos", "Liked", "Saved", "Documents")
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(profileTabs) { tab ->
                        val isSelected = selectedTab == tab
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) ElectricBlue else CardNavy,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NeonBlue else GlassBorder
                            ),
                            modifier = Modifier.clickable { selectedTab = tab }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (tab) {
                                        "My Posts" -> Icons.Default.GridOn
                                        "Videos" -> Icons.Default.PlayCircle
                                        "Liked" -> Icons.Default.Favorite
                                        "Saved" -> Icons.Default.Bookmark
                                        else -> Icons.Default.Description
                                    },
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else TextMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = tab,
                                    color = if (isSelected) Color.White else TextLightGrey,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Tab Content: Showing User's Posts, Videos, Liked posts, etc.
            when (selectedTab) {
                "My Posts" -> {
                    if (userPosts.isEmpty()) {
                        item {
                            EmptyStateCard(
                                icon = Icons.Default.AddPhotoAlternate,
                                title = "No Posts Yet",
                                message = "You haven't posted any videos, photos or study documents yet.",
                                buttonLabel = "+ Create Post",
                                onAction = { viewModel.selectBottomTab(2) }
                            )
                        }
                    } else {
                        items(userPosts, key = { it.id }) { post ->
                            UserPostItem(
                                post = post,
                                onLike = { viewModel.toggleLike(post.id) },
                                onComment = { viewModel.openComments(post) }
                            )
                        }
                    }
                }

                "Videos" -> {
                    if (userVideos.isEmpty()) {
                        item {
                            EmptyStateCard(
                                icon = Icons.Default.VideoLibrary,
                                title = "No Videos Yet",
                                message = "You haven't posted or liked any campus videos yet.",
                                buttonLabel = "Upload Video",
                                onAction = { viewModel.selectBottomTab(2) }
                            )
                        }
                    } else {
                        items(userVideos, key = { it.id }) { post ->
                            UserPostItem(
                                post = post,
                                onLike = { viewModel.toggleLike(post.id) },
                                onComment = { viewModel.openComments(post) }
                            )
                        }
                    }
                }

                "Liked" -> {
                    if (likedPosts.isEmpty()) {
                        item {
                            EmptyStateCard(
                                icon = Icons.Default.FavoriteBorder,
                                title = "No Liked Posts Yet",
                                message = "Tap the heart icon on any post in the Home feed to see it here!",
                                buttonLabel = "Go to Feed",
                                onAction = { viewModel.selectBottomTab(0) }
                            )
                        }
                    } else {
                        items(likedPosts, key = { it.id }) { post ->
                            UserPostItem(
                                post = post,
                                onLike = { viewModel.toggleLike(post.id) },
                                onComment = { viewModel.openComments(post) }
                            )
                        }
                    }
                }

                "Saved" -> {
                    if (savedPosts.isEmpty()) {
                        item {
                            EmptyStateCard(
                                icon = Icons.Default.BookmarkBorder,
                                title = "No Saved Posts",
                                message = "Bookmark important notes, events, or videos to find them easily here.",
                                buttonLabel = "Browse Feed",
                                onAction = { viewModel.selectBottomTab(0) }
                            )
                        }
                    } else {
                        items(savedPosts, key = { it.id }) { post ->
                            UserPostItem(
                                post = post,
                                onLike = { viewModel.toggleLike(post.id) },
                                onComment = { viewModel.openComments(post) }
                            )
                        }
                    }
                }

                "Documents" -> {
                    if (userDocuments.isEmpty()) {
                        item {
                            EmptyStateCard(
                                icon = Icons.Default.PictureAsPdf,
                                title = "No Documents Saved",
                                message = "Upload PDF notes, assignments, or study guides to access them here.",
                                buttonLabel = "Upload Document",
                                onAction = { viewModel.selectBottomTab(2) }
                            )
                        }
                    } else {
                        items(userDocuments, key = { it.id }) { post ->
                            UserPostItem(
                                post = post,
                                onLike = { viewModel.toggleLike(post.id) },
                                onComment = { viewModel.openComments(post) }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }

    // Dedicated Settings & Options Modal Bottom Sheet (Triggered by Top-Right Settings Icon)
    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            containerColor = CardNavy,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(TextMuted)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Settings & Preferences",
                        color = TextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { showSettingsSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextLightGrey)
                    }
                }

                Divider(color = GlassBorder, thickness = 0.5.dp)

                SettingsRowItem(
                    icon = Icons.Default.AdminPanelSettings,
                    title = "Open Admin Panel",
                    subtitle = "Dashboard, user moderation & event control",
                    tint = GoldenYellow,
                    onClick = {
                        showSettingsSheet = false
                        viewModel.toggleAdminMode()
                    }
                )

                SettingsRowItem(
                    icon = Icons.Default.Edit,
                    title = "Edit Profile",
                    subtitle = "Change avatar, bio, and student info",
                    onClick = {
                        showSettingsSheet = false
                        Toast.makeText(context, "Edit Profile opened", Toast.LENGTH_SHORT).show()
                    }
                )

                SettingsRowItem(
                    icon = Icons.Default.Security,
                    title = "Privacy & Security",
                    subtitle = "Profile visibility and messaging permissions",
                    onClick = {
                        showSettingsSheet = false
                        Toast.makeText(context, "Privacy settings: Only RIC Students can view profile", Toast.LENGTH_SHORT).show()
                    }
                )

                SettingsRowItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Announcements, likes, and group alerts",
                    onClick = {
                        showSettingsSheet = false
                        viewModel.setShowNotifications(true)
                    }
                )

                SettingsRowItem(
                    icon = Icons.Default.Info,
                    title = "About RIC-Friends",
                    subtitle = "Riphah International College, Thokar Campus",
                    onClick = {
                        showSettingsSheet = false
                        Toast.makeText(context, "RIC-Friends v1.0 • Thokar Campus", Toast.LENGTH_SHORT).show()
                    }
                )

                SettingsRowItem(
                    icon = Icons.Default.ExitToApp,
                    title = "Log Out",
                    subtitle = "Sign out of your account",
                    tint = UnreadRed,
                    onClick = {
                        showSettingsSheet = false
                        viewModel.logout()
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun StatItem(
    count: String,
    label: String,
    tint: Color = TextWhite
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            color = tint,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = TextMuted,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun EmptyStateCard(
    icon: ImageVector,
    title: String,
    message: String,
    buttonLabel: String,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
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
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(CardNavyVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NeonBlue,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                color = TextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message,
                color = TextMuted,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(text = buttonLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun UserPostItem(
    post: Post,
    onLike: () -> Unit,
    onComment: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CardNavy)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CardNavyVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (post.mediaUri != null) {
                        AsyncImage(
                            model = post.mediaUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = post.mediaRes),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    if (post.type == PostType.VIDEO) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else if (post.type == PostType.DOCUMENT) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = UnreadRed,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (post.type) {
                                PostType.VIDEO -> ElectricBlue.copy(alpha = 0.3f)
                                PostType.PHOTO -> NeonCyan.copy(alpha = 0.2f)
                                PostType.DOCUMENT -> UnreadRed.copy(alpha = 0.2f)
                            }
                        ) {
                            Text(
                                text = post.type.name,
                                color = when (post.type) {
                                    PostType.VIDEO -> NeonBlue
                                    PostType.PHOTO -> NeonCyan
                                    PostType.DOCUMENT -> UnreadRed
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = post.timestamp,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = post.caption,
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onLike() }
                        ) {
                            Icon(
                                imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (post.isLiked) LikePink else TextMuted,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${post.likesCount}",
                                color = if (post.isLiked) LikePink else TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onComment() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = "Comments",
                                tint = TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${post.commentsCount}",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tint: Color = NeonBlue
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CardNavyVariant.copy(alpha = 0.6f),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (tint == UnreadRed) UnreadRed else TextWhite,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
