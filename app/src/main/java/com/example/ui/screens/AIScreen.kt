package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AIMessage
import com.example.ui.components.RICHeader
import com.example.ui.theme.*
import com.example.viewmodel.RICViewModel

@Composable
fun AIScreen(
    viewModel: RICViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.aiMessages.collectAsState()
    val isLoading by viewModel.isAILoading.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    var promptInput by remember { mutableStateOf("") }
    val context = LocalContext.current

    val suggestedPrompts = listOf(
        "Help me with assignments",
        "Summarize this document",
        "Generate study plan",
        "College information",
        "Any other question..."
    )

    // Pulsing halo animation for the 3D robot
    val infiniteTransition = rememberInfiniteTransition(label = "portal")
    val pulseSize by infiniteTransition.animateFloat(
        initialValue = 130f,
        targetValue = 150f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

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

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Section: AI Mascot & Suggested Prompts (visible if conversation is young)
            if (messages.size <= 2) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "AI Assistant",
                            color = TextWhite,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Ask anything, get instant help!",
                            color = NeonBlue,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Glowing 3D Robot in portal
                        Box(
                            modifier = Modifier
                                .size(pulseSize.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            NeonCyan.copy(alpha = 0.4f),
                                            ElectricBlue.copy(alpha = 0.2f),
                                            Color.Transparent
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, NeonBlue, CircleShape)
                                    .background(CardNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_ai_robot),
                                    contentDescription = "RIC AI Mascot",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Suggested Prompts list
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            suggestedPrompts.forEach { suggestion ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = CardNavy,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.sendAIMessage(suggestion)
                                        }
                                        .testTag("ai_suggestion_${suggestion.take(10)}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = NeonBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = suggestion,
                                            color = TextWhite,
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Chat Messages History
            items(messages, key = { it.id }) { msg ->
                AIMessageBubble(
                    message = msg,
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("AI Response", msg.text))
                        Toast.makeText(context, "Copied response to clipboard", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // Loading state
            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(CardNavyVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeonBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = CardNavy,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = NeonBlue
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AI is thinking...",
                                    color = TextLightGrey,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Query Input Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CardNavy,
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(CardNavyVariant)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        textStyle = androidx.compose.ui.text.TextStyle(color = TextWhite, fontSize = 14.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_input_field"),
                        decorationBox = { innerTextField ->
                            if (promptInput.isEmpty()) {
                                Text(
                                    text = "Type your question...",
                                    color = TextMuted,
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(
                    onClick = {
                        if (promptInput.isNotBlank()) {
                            viewModel.sendAIMessage(promptInput)
                            promptInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(ElectricBlue)
                        .testTag("ai_send_button"),
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Ask AI",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AIMessageBubble(
    message: AIMessage,
    onCopy: () -> Unit
) {
    val isUser = message.isUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(DarkBlueAccent)
                    .border(1.dp, NeonBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI",
                    tint = NeonBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) ElectricBlue else CardNavy,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isUser) NeonBlue.copy(alpha = 0.5f) else GlassBorder
            ),
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    color = TextWhite,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp
                )

                if (!isUser) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = onCopy,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = NeonCyan,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
