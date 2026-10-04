package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.EventItem
import com.example.model.ReportItem
import com.example.ui.theme.*
import com.example.viewmodel.RICViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: RICViewModel,
    modifier: Modifier = Modifier
) {
    val adminSection by viewModel.adminSection.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val events by viewModel.events.collectAsState()
    val reports by viewModel.reports.collectAsState()
    val groups by viewModel.officialGroups.collectAsState()
    val context = LocalContext.current

    var showCreateEventDialog by remember { mutableStateOf(false) }
    var showCreateGroupDialog by remember { mutableStateOf(false) }
    var showSendAnnouncementDialog by remember { mutableStateOf(false) }

    BackHandler {
        viewModel.toggleAdminMode()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavyDark)
    ) {
        // Top Admin Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CardNavy,
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.toggleAdminMode() },
                    modifier = Modifier.testTag("admin_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Student View",
                        tint = TextWhite
                    )
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, GoldenYellow, CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_ric_logo),
                        contentDescription = null,
                        modifier = Modifier.size(34.dp).clip(CircleShape),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Riphah International College",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Admin Panel • Thokar Campus",
                        color = GoldenYellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ElectricBlue,
                    modifier = Modifier.clickable { viewModel.toggleAdminMode() }
                ) {
                    Text(
                        text = "Student App",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Horizontal navigation tabs for Admin sections
        val sections = listOf("Dashboard", "Manage Users", "Manage Content", "Events & Notices", "Groups", "Reports", "Settings")
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardNavy.copy(alpha = 0.5f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sections) { sec ->
                val isSelected = adminSection == sec
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) ElectricBlue else CardNavyVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NeonBlue else GlassBorder),
                    modifier = Modifier.clickable { viewModel.setAdminSection(sec) }
                ) {
                    Text(
                        text = sec,
                        color = if (isSelected) Color.White else TextLightGrey,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Section Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (adminSection) {
                "Dashboard" -> {
                    // Metrics Cards
                    item {
                        Text(
                            text = "Admin Dashboard",
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Welcome back, Admin! Real-time statistics:",
                            color = TextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetricCard(
                                title = "Total Users",
                                value = "2,548",
                                badge = "↑ 12%",
                                icon = Icons.Default.People,
                                color = NeonBlue,
                                modifier = Modifier.weight(1f)
                            )
                            MetricCard(
                                title = "Total Posts",
                                value = "1,248",
                                badge = "↑ 8%",
                                icon = Icons.Default.DynamicFeed,
                                color = ElectricBlue,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetricCard(
                                title = "Total Events",
                                value = "18",
                                badge = "↑ 5%",
                                icon = Icons.Default.Event,
                                color = GoldenYellow,
                                modifier = Modifier.weight(1f)
                            )
                            MetricCard(
                                title = "Reports",
                                value = "32",
                                badge = "↑ 15%",
                                icon = Icons.Default.Report,
                                color = UnreadRed,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Quick Actions
                    item {
                        Text(
                            text = "Quick Actions",
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                QuickActionButton(
                                    label = "Add Notice",
                                    icon = Icons.Default.Campaign,
                                    onClick = { viewModel.setAdminSection("Events & Notices") },
                                    modifier = Modifier.weight(1f)
                                )
                                QuickActionButton(
                                    label = "Manage Content",
                                    icon = Icons.Default.PermMedia,
                                    onClick = { viewModel.setAdminSection("Manage Content") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                QuickActionButton(
                                    label = "Announcement",
                                    icon = Icons.Default.Campaign,
                                    onClick = { showSendAnnouncementDialog = true },
                                    modifier = Modifier.weight(1f)
                                )
                                QuickActionButton(
                                    label = "Create Group",
                                    icon = Icons.Default.Groups,
                                    onClick = { showCreateGroupDialog = true },
                                    modifier = Modifier.weight(1f)
                                )
                                QuickActionButton(
                                    label = "Reports",
                                    icon = Icons.Default.ReportProblem,
                                    onClick = { viewModel.setAdminSection("Reports") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Storage Monitoring Widget
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, GlassBorder, RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = CardNavy)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Cloud Storage Monitoring",
                                        color = TextWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "6.5 GB / 10 GB (65%)",
                                        color = NeonBlue,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { 0.65f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = NeonBlue,
                                    trackColor = CardNavyVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "• Videos: 4.2 GB  • Photos: 1.6 GB  • Documents: 0.7 GB",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Recent Activity Log
                    item {
                        Text(
                            text = "Recent Activity",
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(CardNavy)
                                .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ActivityRow("New user registered: Ayesha Khan (BSCS)", "2 mins ago")
                            Divider(color = GlassBorder, thickness = 0.5.dp)
                            ActivityRow("New post reported: Post by Ahmed Raza", "10 mins ago")
                            Divider(color = GlassBorder, thickness = 0.5.dp)
                            ActivityRow("Official event published: Career Guidance Session", "1 hour ago")
                        }
                    }
                }

                "Manage Users" -> {
                    item {
                        Text(
                            text = "Manage Users (2,548)",
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    val sampleUsers = listOf(
                        Triple("Ayesha Khan", "Student • BSCS", "Active"),
                        Triple("Ahmed Raza", "Student • BBA", "Active"),
                        Triple("Sara Ali", "Student • BS English", "Active"),
                        Triple("Usman Tariq", "Student • BSCS", "Suspended"),
                        Triple("Zainab Fatima", "Student • B.Com", "Active")
                    )
                    items(sampleUsers) { (name, role, status) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, GlassBorder, RoundedCornerShape(14.dp)),
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
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(ElectricBlue.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = NeonBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = name, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(text = role, color = TextMuted, fontSize = 11.sp)
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (status == "Active") OnlineGreen.copy(alpha = 0.2f) else UnreadRed.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = status,
                                        color = if (status == "Active") OnlineGreen else UnreadRed,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(onClick = { Toast.makeText(context, "Actions for $name", Toast.LENGTH_SHORT).show() }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = null, tint = TextMuted)
                                }
                            }
                        }
                    }
                }

                "Manage Content" -> {
                    item {
                        Text(
                            text = "Manage College Feed Posts",
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(posts) { post ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, GlassBorder, RoundedCornerShape(14.dp)),
                            colors = CardDefaults.cardColors(containerColor = CardNavy)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = post.authorName, color = NeonBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(text = post.caption, color = TextWhite, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    Text(text = "${post.likesCount} likes • ${post.commentsCount} comments", color = TextMuted, fontSize = 10.sp)
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.deletePost(post.id)
                                        Toast.makeText(context, "Post removed by admin", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = UnreadRed)
                                }
                            }
                        }
                    }
                }

                "Events & Notices" -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Events & Notices",
                                color = TextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { showSendAnnouncementDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = CardNavyVariant),
                                    shape = RoundedCornerShape(16.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                                ) {
                                    Icon(Icons.Default.Campaign, contentDescription = null, tint = GoldenYellow, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Send Notice", color = GoldenYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { showCreateEventDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                    shape = RoundedCornerShape(16.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("New Event", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    items(events) { ev ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, GlassBorder, RoundedCornerShape(14.dp)),
                            colors = CardDefaults.cardColors(containerColor = CardNavy)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DarkBlueAccent,
                                    modifier = Modifier.padding(end = 10.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = ev.month, color = GoldenYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Text(text = ev.day, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = ev.title, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "${ev.time} • ${ev.location}", color = NeonBlue, fontSize = 11.sp)
                                    Text(text = ev.description, color = TextMuted, fontSize = 11.sp, maxLines = 1)
                                }
                            }
                        }
                    }
                }

                "Groups" -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Official Groups (Admin Only)",
                                color = TextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = { showCreateGroupDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Create Group", fontSize = 12.sp)
                            }
                        }
                    }
                    items(groups) { grp ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, GlassBorder, RoundedCornerShape(14.dp)),
                            colors = CardDefaults.cardColors(containerColor = CardNavy)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = grp.name, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "${grp.membersCount} • ${grp.description}", color = TextMuted, fontSize = 11.sp)
                                }
                                Surface(shape = RoundedCornerShape(12.dp), color = DarkBlueAccent) {
                                    Text(text = "Official", color = NeonBlue, fontSize = 10.sp, modifier = Modifier.padding(6.dp))
                                }
                            }
                        }
                    }
                }

                "Reports" -> {
                    item {
                        Text(
                            text = "Moderation Reports (${reports.size})",
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(reports) { rep ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, GlassBorder, RoundedCornerShape(14.dp)),
                            colors = CardDefaults.cardColors(containerColor = CardNavy)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "${rep.targetType}: ${rep.reportedItem}", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Reason: ${rep.reason} (by ${rep.reportedBy})", color = TextMuted, fontSize = 11.sp)
                                    Text(text = "Status: ${rep.status}", color = if (rep.status == "Resolved") OnlineGreen else GoldenYellow, fontSize = 10.sp)
                                }
                                if (rep.status != "Resolved") {
                                    Button(
                                        onClick = {
                                            viewModel.resolveReport(rep.id)
                                            Toast.makeText(context, "Report resolved", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Resolve", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                "Settings" -> {
                    item {
                        Text(
                            text = "Admin & System Settings",
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(CardNavy)
                                .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = "AI Model Engine: Gemini 2.5 Flash (Configured)", color = NeonBlue, fontSize = 13.sp)
                            Text(text = "Image Generation: Configured with Quota Safe Guard", color = TextLightGrey, fontSize = 12.sp)
                            Text(text = "Cloud Backup: Last synced today at 04:00 AM", color = TextLightGrey, fontSize = 12.sp)
                            Button(
                                onClick = { Toast.makeText(context, "Cache cleared successfully", Toast.LENGTH_SHORT).show() },
                                colors = ButtonDefaults.buttonColors(containerColor = CardNavyVariant),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Clear System Cache", color = TextWhite, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }

    // Dialog for creating event
    if (showCreateEventDialog) {
        var evTitle by remember { mutableStateOf("") }
        var evMonth by remember { mutableStateOf("MAY") }
        var evDay by remember { mutableStateOf("15") }
        var evTime by remember { mutableStateOf("11:00 AM") }
        var evLocation by remember { mutableStateOf("Auditorium, Thokar Campus") }
        var evDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateEventDialog = false },
            title = { Text("Create Official College Event", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = evTitle, onValueChange = { evTitle = it }, label = { Text("Event Title") })
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = evMonth, onValueChange = { evMonth = it }, label = { Text("Month") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = evDay, onValueChange = { evDay = it }, label = { Text("Day") }, modifier = Modifier.weight(1f))
                    }
                    OutlinedTextField(value = evTime, onValueChange = { evTime = it }, label = { Text("Time") })
                    OutlinedTextField(value = evLocation, onValueChange = { evLocation = it }, label = { Text("Location") })
                    OutlinedTextField(value = evDesc, onValueChange = { evDesc = it }, label = { Text("Description") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (evTitle.isNotBlank()) {
                            viewModel.createEvent(evTitle, evMonth, evDay, evTime, evLocation, evDesc)
                            showCreateEventDialog = false
                            Toast.makeText(context, "Event created and announced to all students!", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                ) {
                    Text("Publish Event")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateEventDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = CardNavy
        )
    }

    // Dialog for creating group
    if (showCreateGroupDialog) {
        var grpName by remember { mutableStateOf("") }
        var grpDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateGroupDialog = false },
            title = { Text("Create Official Group", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Only College Administration can create official student groups.", color = TextMuted, fontSize = 11.sp)
                    OutlinedTextField(value = grpName, onValueChange = { grpName = it }, label = { Text("Group Name") })
                    OutlinedTextField(value = grpDesc, onValueChange = { grpDesc = it }, label = { Text("Description & Guidelines") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (grpName.isNotBlank()) {
                            viewModel.createOfficialGroup(grpName, grpDesc)
                            showCreateGroupDialog = false
                            Toast.makeText(context, "Official group created!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateGroupDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = CardNavy
        )
    }

    // Dialog for sending official announcement
    if (showSendAnnouncementDialog) {
        var notifTitle by remember { mutableStateOf("") }
        var notifBody by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showSendAnnouncementDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Campaign, contentDescription = null, tint = GoldenYellow)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send Official Notice", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "This notification will be dispatched immediately to all students at Riphah International College, Thokar Campus.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = notifTitle,
                        onValueChange = { notifTitle = it },
                        placeholder = { Text("Notice Title (e.g. Exam Timetable Released)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = notifBody,
                        onValueChange = { notifBody = it },
                        placeholder = { Text("Notice Details & Instructions") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.height(100.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (notifTitle.isNotBlank()) {
                            viewModel.sendAdminAnnouncement(
                                notifTitle.trim(),
                                notifBody.ifBlank { "Official campus notice from Administration." }.trim()
                            )
                            showSendAnnouncementDialog = false
                            Toast.makeText(context, "Announcement sent to all students!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Please enter notice title", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldenYellow, contentColor = DeepNavyDark)
                ) {
                    Text("Broadcast", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSendAnnouncementDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = CardNavy
        )
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    badge: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CardNavy)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = OnlineGreen.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badge,
                        color = OnlineGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = value, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = title, color = TextMuted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CardNavy,
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = NeonBlue, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ActivityRow(title: String, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = TextLightGrey, fontSize = 12.sp, modifier = Modifier.weight(1f))
        Text(text = time, color = TextMuted, fontSize = 10.sp)
    }
}
