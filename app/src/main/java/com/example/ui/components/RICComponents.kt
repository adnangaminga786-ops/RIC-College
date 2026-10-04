package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun RICHeader(
    onNotificationClick: () -> Unit,
    unreadCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(DeepNavyDark.copy(alpha = 0.95f))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Official RIC Logo inside glowing shield
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    brush = Brush.radialGradient(
                        listOf(NeonBlue.copy(alpha = glowAlpha), ElectricBlue, DeepNavy)
                    ),
                    shape = CircleShape
                )
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_ric_logo),
                contentDescription = "Riphah International College Official Logo",
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title and Campus details
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Riphah International College",
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Thokar Campus",
                    color = NeonBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Verified Campus",
                    tint = NeonBlue,
                    modifier = Modifier.size(13.dp)
                )
            }
            Text(
                text = "Learn  •  Connect  •  Grow",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal
            )
        }

        // Notification Button with badge
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(CardNavy)
                .clickable { onNotificationClick() }
                .testTag("notification_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = TextLightGrey,
                modifier = Modifier.size(22.dp)
            )
            if (unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(17.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-2).dp)
                        .clip(CircleShape)
                        .background(UnreadRed),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$unreadCount",
                        color = TextWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun RICBottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Exactly 5 items as strictly required:
    // 0: Home, 1: Friends, 2: Upload, 3: AI, 4: Profile
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(16.dp),
        color = DeepNavyDark.copy(alpha = 0.98f),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 0: Home
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                tag = "nav_home"
            )

            // Tab 1: Friends (Contains Chat!)
            BottomNavItem(
                icon = Icons.Default.People,
                label = "Friends",
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                tag = "nav_friends"
            )

            // Tab 2: + Upload (Prominent Neon Center Floating Button)
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(ElectricBlue, NeonBlue, NeonCyan)
                        )
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple()
                    ) { onTabSelected(2) }
                    .testTag("nav_upload"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(DeepNavyDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Upload Content",
                        tint = NeonBlue,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Tab 3: AI
            BottomNavItem(
                icon = Icons.Default.AutoAwesome,
                label = "AI",
                isSelected = selectedTab == 3,
                onClick = { onTabSelected(3) },
                tag = "nav_ai"
            )

            // Tab 4: Profile
            BottomNavItem(
                icon = Icons.Default.Person,
                label = "Profile",
                isSelected = selectedTab == 4,
                onClick = { onTabSelected(4) },
                tag = "nav_profile"
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) NeonBlue else TextMuted,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) NeonBlue else TextMuted,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(3.dp))
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(NeonBlue)
            )
        } else {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
