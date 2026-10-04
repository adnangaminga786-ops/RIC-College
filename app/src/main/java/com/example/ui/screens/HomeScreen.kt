package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

@Composable
fun HomeScreen(
    viewModel: RICViewModel,
    modifier: Modifier = Modifier
) {
    val posts by viewModel.posts.collectAsState()
    val feedFilter by viewModel.feedFilter.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavyDark)
    ) {
        // Official RIC Header
        RICHeader(
            onNotificationClick = { viewModel.setShowNotifications(true) },
            unreadCount = notifications.count { !it.isRead }
        )

        // Search Bar (Opens global search)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(CardNavy)
                .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                .clickable { viewModel.setShowSearch(true) }
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .testTag("home_search_bar")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Search videos, photos, documents...",
                    color = TextMuted,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Filters",
                    tint = NeonBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Content Filter Tabs: "For You", "All", "Recent", "Events"
        val filterTabs = listOf("For You", "All", "Recent", "Events")
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterTabs) { tab ->
                val isSelected = feedFilter == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) Brush.horizontalGradient(listOf(ElectricBlue, NeonBlue))
                            else Brush.horizontalGradient(listOf(CardNavy, CardNavy))
                        )
                        .clickable { viewModel.setFeedFilter(tab) }
                        .padding(horizontal = 16.dp, vertical = 7.dp)
                        .testTag("feed_tab_$tab")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (tab == "For You") {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = if (isSelected) DeepNavyDark else GoldenYellow,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = tab,
                            color = if (isSelected) DeepNavyDark else TextLightGrey,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Filtered Posts
        val filteredPosts = remember(posts, feedFilter) {
            when (feedFilter) {
                "Recent" -> posts.sortedByDescending { it.id }
                "Events" -> posts.filter { it.hashtags.any { tag -> tag.contains("Event", ignoreCase = true) || tag.contains("Party", ignoreCase = true) } }.ifEmpty { posts }
                else -> posts
            }
        }

        // TikTok-style vertical social feed
        if (filteredPosts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(CardNavy)
                            .border(2.dp, NeonBlue.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PostAdd,
                            contentDescription = null,
                            tint = NeonBlue,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Posts Yet",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "All previous data has been cleared. Be the first to share a video, photo, or document with RIC Thokar Campus!",
                        color = TextMuted,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.selectBottomTab(2) },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.testTag("empty_feed_upload_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Create Post", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("feed_list"),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredPosts, key = { it.id }) { post ->
                    FeedPostCard(
                        post = post,
                        onLike = { viewModel.toggleLike(post.id) },
                        onComment = { viewModel.openComments(post) },
                        onShare = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Check this out on RIC-Friends!")
                                putExtra(Intent.EXTRA_TEXT, "${post.authorName} on RIC-Friends:\n${post.caption}\n#RIC #ThokarCampus")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share with"))
                        },
                        onDownload = {
                            Toast.makeText(context, "Downloading media from RIC-Friends...", Toast.LENGTH_SHORT).show()
                        },
                        onSave = { viewModel.toggleSave(post.id) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }
        }
    }
}

@Composable
fun FeedPostCard(
    post: Post,
    onLike: () -> Unit,
    onComment: () -> Unit,
    onShare: () -> Unit,
    onDownload: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(true) }

    // Rotating music disc animation
    val infiniteTransition = rememberInfiniteTransition(label = "disc")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(520.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            .testTag("post_card_${post.id}"),
        colors = CardDefaults.cardColors(containerColor = CardNavy)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Full background media (Campus building / campus life / user uploaded media)
            if (post.mediaUri != null) {
                AsyncImage(
                    model = post.mediaUri,
                    contentDescription = "Post Media",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = post.mediaRes),
                    contentDescription = "Post Media",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Dark gradient overlay for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.35f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Top Header: Campus Life / Post Type badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DeepNavyDark.copy(alpha = 0.75f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (post.type) {
                                PostType.VIDEO -> Icons.Default.PlayCircle
                                PostType.PHOTO -> Icons.Default.Image
                                PostType.DOCUMENT -> Icons.Default.Description
                            },
                            contentDescription = null,
                            tint = NeonBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (post.type) {
                                PostType.VIDEO -> "Campus Video"
                                PostType.PHOTO -> "Campus Life"
                                PostType.DOCUMENT -> "College Doc"
                            },
                            color = TextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                IconButton(
                    onClick = onSave,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(DeepNavyDark.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = if (post.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save",
                        tint = if (post.isSaved) GoldenYellow else TextWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Center Play/Pause button for Video
            if (post.type == PostType.VIDEO) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                        .border(1.5.dp, Color.White.copy(alpha = 0.7f), CircleShape)
                        .clickable { isPlaying = !isPlaying },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Document preview card inside post if document type
            if (post.type == PostType.DOCUMENT && post.documentTitle != null) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(0.85f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onDownload() },
                    color = DeepNavyDark.copy(alpha = 0.9f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonBlue.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(UnreadRed.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = UnreadRed,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = post.documentTitle,
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${post.documentSize ?: "2.4 MB"} • Tap to Download",
                                color = NeonBlue,
                                fontSize = 11.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download",
                            tint = NeonBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Vertical Action Rail on the Right
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Author avatar with '+' follow button
                Box(
                    modifier = Modifier.size(46.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = post.authorAvatarRes),
                        contentDescription = post.authorName,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, NeonBlue, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.BottomCenter)
                            .offset(y = 4.dp)
                            .clip(CircleShape)
                            .background(LikePink),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Follow",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                // Like button
                ActionItem(
                    icon = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    label = "${post.likesCount}",
                    tint = if (post.isLiked) LikePink else Color.White,
                    onClick = onLike,
                    tag = "like_btn_${post.id}"
                )

                // Comment button
                ActionItem(
                    icon = Icons.Default.ChatBubbleOutline,
                    label = "${post.commentsCount}",
                    tint = Color.White,
                    onClick = onComment,
                    tag = "comment_btn_${post.id}"
                )

                // Share button
                ActionItem(
                    icon = Icons.Default.Reply,
                    label = "${post.sharesCount}",
                    tint = Color.White,
                    onClick = onShare,
                    tag = "share_btn_${post.id}",
                    rotate = 180f
                )

                // Download button
                ActionItem(
                    icon = Icons.Default.Download,
                    label = "Save",
                    tint = NeonBlue,
                    onClick = onDownload,
                    tag = "download_btn_${post.id}"
                )

                // Music Disc
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .rotate(if (isPlaying) rotation else 0f)
                        .clip(CircleShape)
                        .background(DeepNavyDark)
                        .border(1.dp, NeonBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Sound Track",
                        tint = NeonBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Bottom Post Metadata (Author info, Caption, Hashtags, Audio track)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(0.78f)
                    .padding(start = 14.dp, bottom = 14.dp)
            ) {
                // Author row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = post.authorName,
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = NeonBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "•  ${post.timestamp}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = post.program,
                    color = NeonBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = post.caption,
                    color = TextWhite,
                    fontSize = 12.5.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))
                // Hashtags
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    post.hashtags.take(3).forEach { tag ->
                        Text(
                            text = tag,
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                // Audio track info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = TextLightGrey,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.audioTrack,
                        color = TextLightGrey,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Progress line
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.45f)
                            .fillMaxHeight()
                            .background(NeonBlue)
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
    tag: String,
    rotate: Float = 0f
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier
                .size(28.dp)
                .rotate(rotate)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
