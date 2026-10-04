package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatConversation
import com.example.model.ChatMessage
import com.example.model.MessageType
import com.example.ui.theme.*
import com.example.viewmodel.RICViewModel

@Composable
fun ChatDetailScreen(
    chat: ChatConversation,
    viewModel: RICViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val context = LocalContext.current

    BackHandler {
        viewModel.closeChat()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavyDark)
    ) {
        // Chat Top Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CardNavy,
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.closeChat() },
                    modifier = Modifier.testTag("chat_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }

                Box(modifier = Modifier.size(42.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CardNavyVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = chat.participantName,
                            tint = NeonBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    if (chat.isOnline) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(OnlineGreen)
                                .border(1.5.dp, DeepNavyDark, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = chat.participantName,
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (chat.isOnline) "Online • ${chat.participantProgram}" else "Last seen recently",
                        color = if (chat.isOnline) OnlineGreen else TextMuted,
                        fontSize = 11.sp
                    )
                }

                IconButton(onClick = { Toast.makeText(context, "Voice call to ${chat.participantName}", Toast.LENGTH_SHORT).show() }) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Voice Call",
                        tint = NeonBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = { Toast.makeText(context, "Video call to ${chat.participantName}", Toast.LENGTH_SHORT).show() }) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Video Call",
                        tint = NeonCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatBubble(msg = msg, onDownload = {
                    Toast.makeText(context, "Downloading ${msg.attachmentName}...", Toast.LENGTH_SHORT).show()
                })
            }
        }

        // Input Bottom Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CardNavy,
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        // Quick demo attach document
                        viewModel.sendMessage("", MessageType.DOCUMENT, "RIC_Lab_Report_Final.pdf", "1.8 MB")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attach Document",
                        tint = NeonBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(
                    onClick = {
                        viewModel.sendMessage("", MessageType.VOICE, "Voice message (0:15)", null)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Message",
                        tint = NeonCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(CardNavyVariant)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        textStyle = androidx.compose.ui.text.TextStyle(color = TextWhite, fontSize = 14.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("chat_input_field"),
                        decorationBox = { innerTextField ->
                            if (inputText.isEmpty()) {
                                Text(
                                    text = "Type a message...",
                                    color = TextMuted,
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ElectricBlue)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    msg: ChatMessage,
    onDownload: () -> Unit
) {
    val isMe = msg.isFromMe

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            color = if (isMe) ElectricBlue else CardNavyVariant,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isMe) NeonBlue.copy(alpha = 0.4f) else GlassBorder
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                when (msg.type) {
                    MessageType.TEXT -> {
                        Text(
                            text = msg.text,
                            color = TextWhite,
                            fontSize = 13.5.sp,
                            lineHeight = 18.sp
                        )
                    }
                    MessageType.DOCUMENT -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DeepNavyDark.copy(alpha = 0.5f))
                                .clickable { onDownload() }
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = UnreadRed,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = msg.attachmentName ?: "Document.pdf",
                                    color = TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${msg.attachmentSize ?: "2.4 MB"} • Tap to save",
                                    color = NeonBlue,
                                    fontSize = 10.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download",
                                tint = NeonBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    MessageType.VOICE -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(NeonBlue.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = NeonBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            // Simulated audio waveform lines
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val heights = listOf(8, 14, 22, 10, 18, 24, 12, 16, 20, 8, 14, 10)
                                heights.forEach { h ->
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(h.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(NeonCyan)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "0:18",
                                color = TextLightGrey,
                                fontSize = 11.sp
                            )
                        }
                    }
                    MessageType.PHOTO -> {
                        Text(
                            text = "📷 Photo attachment",
                            color = NeonCyan,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = msg.timestamp,
                    color = if (isMe) TextLightGrey.copy(alpha = 0.8f) else TextMuted,
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
