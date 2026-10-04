package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.PostType
import com.example.ui.components.RICHeader
import com.example.ui.theme.*
import com.example.viewmodel.RICViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun UploadScreen(
    viewModel: RICViewModel,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Upload state:
    // Step 1: Select Type & Pick File
    // Step 2: Add Caption, Hashtags & Publish (Only accessible after file is picked!)
    var uploadStep by remember { mutableStateOf(1) } // 1: Pick Media, 2: Add Caption & Publish

    var selectedUploadType by remember { mutableStateOf<PostType?>(null) }
    var selectedMediaUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFallbackRes by remember { mutableStateOf<Int?>(null) }
    var selectedMediaName by remember { mutableStateOf<String?>(null) }
    var selectedMediaSize by remember { mutableStateOf<String?>(null) }

    var caption by remember { mutableStateOf("") }
    var hashtagsText by remember { mutableStateOf("#RIC #ThokarCampus #StudentLife") }
    var selectedPrivacy by remember { mutableStateOf("College Only") }
    var selectedProgram by remember { mutableStateOf("BS Computer Science") }
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf(0f) }

    // Native Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedMediaUri = uri
            selectedFallbackRes = null
            selectedMediaName = "ric_photo_${System.currentTimeMillis().toString().takeLast(4)}.jpg"
            selectedMediaSize = "2.8 MB"
        }
    }

    // Native Video Picker
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedMediaUri = uri
            selectedFallbackRes = null
            selectedMediaName = "ric_video_${System.currentTimeMillis().toString().takeLast(4)}.mp4"
            selectedMediaSize = "14.2 MB"
        }
    }

    // Native Document Picker
    val docPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedMediaUri = uri
            selectedFallbackRes = null
            selectedMediaName = "ric_document_${System.currentTimeMillis().toString().takeLast(4)}.pdf"
            selectedMediaSize = "1.8 MB"
        }
    }

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

        if (uploadStep == 1) {
            // ==========================================
            // STEP 1: SELECT & UPLOAD MEDIA FILE FIRST
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Header with step indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Upload Media",
                            color = TextWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Step 1 of 2: Select your file first",
                            color = NeonBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CardNavy,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                    ) {
                        Text(
                            text = "1 / 2",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Three Media Type Selection Cards
                Text(
                    text = "Select what you want to upload:",
                    color = TextLightGrey,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 1. Video Card
                UploadTypeCard(
                    title = "Upload Video",
                    subtitle = "Select video from phone gallery or files",
                    icon = Icons.Default.PlayArrow,
                    gradient = listOf(Color(0xFF5B21B6), Color(0xFF2563EB)),
                    isSelected = selectedUploadType == PostType.VIDEO,
                    onClick = {
                        selectedUploadType = PostType.VIDEO
                        videoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    },
                    tag = "select_video_type"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Photo Card
                UploadTypeCard(
                    title = "Upload Photo",
                    subtitle = "Select picture from phone gallery or camera",
                    icon = Icons.Default.Image,
                    gradient = listOf(Color(0xFF1E40AF), Color(0xFF00B0FF)),
                    isSelected = selectedUploadType == PostType.PHOTO,
                    onClick = {
                        selectedUploadType = PostType.PHOTO
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    tag = "select_photo_type"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Document Card
                UploadTypeCard(
                    title = "Upload Document",
                    subtitle = "PDF, DOCX, study guides, assignment notes",
                    icon = Icons.Default.Description,
                    gradient = listOf(Color(0xFF991B1B), Color(0xFFEA580C)),
                    isSelected = selectedUploadType == PostType.DOCUMENT,
                    onClick = {
                        selectedUploadType = PostType.DOCUMENT
                        docPickerLauncher.launch(arrayOf("application/pdf", "text/plain", "*/*"))
                    },
                    tag = "select_document_type"
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Media Selection & Preview Area
                val hasSelectedFile = selectedMediaUri != null || selectedFallbackRes != null

                if (hasSelectedFile) {
                    // Preview Card for the selected file
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.5.dp, NeonBlue, RoundedCornerShape(18.dp)),
                        colors = CardDefaults.cardColors(containerColor = CardNavy)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(190.dp)
                                    .background(DeepNavyDark),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedMediaUri != null) {
                                    AsyncImage(
                                        model = selectedMediaUri,
                                        contentDescription = "Selected media",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else if (selectedFallbackRes != null) {
                                    Image(
                                        painter = painterResource(id = selectedFallbackRes!!),
                                        contentDescription = "Selected media",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                if (selectedUploadType == PostType.VIDEO) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(Color.Black.copy(alpha = 0.6f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Play Video",
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                } else if (selectedUploadType == PostType.DOCUMENT) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(UnreadRed.copy(alpha = 0.25f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PictureAsPdf,
                                            contentDescription = "PDF Document",
                                            tint = UnreadRed,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = OnlineGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = selectedMediaName ?: "media_file",
                                            color = TextWhite,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Text(
                                        text = "${selectedMediaSize ?: "3.2 MB"} • File Ready to Post",
                                        color = TextLightGrey,
                                        fontSize = 11.sp
                                    )
                                }

                                Button(
                                    onClick = {
                                        when (selectedUploadType) {
                                            PostType.VIDEO -> videoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                                            PostType.PHOTO -> photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                            PostType.DOCUMENT -> docPickerLauncher.launch(arrayOf("application/pdf", "text/plain", "*/*"))
                                            else -> photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CardNavyVariant),
                                    shape = RoundedCornerShape(16.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Change", color = NeonBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Prominent Button: PROCEED TO STEP 2
                    Button(
                        onClick = {
                            uploadStep = 2 // Move to caption & details ONLY when file is ready
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricBlue
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("proceed_to_caption_btn")
                    ) {
                        Text(
                            text = "Next: Add Caption & Post Details →",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // Quick Action: Choose Campus Sample Media if running in emulator / no files
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = CardNavy,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Or Choose from Thokar Campus Gallery:",
                                color = TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        selectedUploadType = PostType.PHOTO
                                        selectedFallbackRes = R.drawable.img_campus_night
                                        selectedMediaUri = null
                                        selectedMediaName = "RIC_Thokar_Night.jpg"
                                        selectedMediaSize = "3.8 MB"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CardNavyVariant),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = NeonBlue, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Campus Night", color = TextWhite, fontSize = 11.sp)
                                }

                                Button(
                                    onClick = {
                                        selectedUploadType = PostType.PHOTO
                                        selectedFallbackRes = R.drawable.img_campus_life
                                        selectedMediaUri = null
                                        selectedMediaName = "RIC_Campus_Life.jpg"
                                        selectedMediaSize = "4.2 MB"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CardNavyVariant),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.School, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Students Lawn", color = TextWhite, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(70.dp))
            }
        } else {
            // ========================================================
            // STEP 2: ADD CAPTION, HASHTAGS & AUDIENCE (PUBLISH FORM)
            // Only visible AFTER media has been chosen!
            // ========================================================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Header with Back to Step 1
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { uploadStep = 1 },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Media",
                            tint = TextWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Add Caption & Details",
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Step 2 of 2: Finalize your post",
                            color = NeonBlue,
                            fontSize = 11.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CardNavy,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                    ) {
                        Text(
                            text = "2 / 2",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Attached Media Mini-Banner
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = CardNavy,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonBlue.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DeepNavyDark),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedMediaUri != null) {
                                AsyncImage(
                                    model = selectedMediaUri,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else if (selectedFallbackRes != null) {
                                Image(
                                    painter = painterResource(id = selectedFallbackRes!!),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(Icons.Default.Attachment, contentDescription = null, tint = NeonBlue)
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedMediaName ?: "Selected File",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${selectedUploadType?.name ?: "POST"} • ${selectedMediaSize ?: "Ready"}",
                                color = NeonCyan,
                                fontSize = 11.sp
                            )
                        }

                        TextButton(onClick = { uploadStep = 1 }) {
                            Text("Change", color = NeonBlue, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Caption Input
                Text(
                    text = "Caption & Thoughts",
                    color = TextLightGrey,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    placeholder = {
                        Text(
                            text = "Write a caption, thoughts, or context for this post...",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("caption_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = CardNavy,
                        unfocusedContainerColor = CardNavy,
                        focusedBorderColor = NeonBlue,
                        unfocusedBorderColor = GlassBorder
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Hashtags
                Text(
                    text = "Hashtags",
                    color = TextLightGrey,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = hashtagsText,
                    onValueChange = { hashtagsText = it },
                    placeholder = { Text("#RIC #ThokarCampus #StudentLife", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hashtags_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = CardNavy,
                        unfocusedContainerColor = CardNavy,
                        focusedBorderColor = NeonBlue,
                        unfocusedBorderColor = GlassBorder
                    ),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                // Quick hashtag chips
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val tagSuggestions = listOf("#RIC", "#ThokarCampus", "#BSCS", "#StudentLife")
                    tagSuggestions.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CardNavyVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                            modifier = Modifier.clickable {
                                if (!hashtagsText.contains(tag)) {
                                    hashtagsText = if (hashtagsText.isBlank()) tag else "$hashtagsText $tag"
                                }
                            }
                        ) {
                            Text(
                                text = tag,
                                color = NeonCyan,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Program / Department
                Text(
                    text = "Program / Department",
                    color = TextLightGrey,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val programs = listOf("BSCS", "BBA", "BS English", "FSc")
                    programs.forEach { prog ->
                        val isSel = selectedProgram.contains(prog)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSel) ElectricBlue else CardNavy,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) NeonBlue else GlassBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedProgram = prog }
                        ) {
                            Text(
                                text = prog,
                                color = if (isSel) Color.White else TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Privacy level
                Text(
                    text = "Who can see this post?",
                    color = TextLightGrey,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val privacyOptions = listOf("College Only", "Public", "Friends Only")
                    privacyOptions.forEach { opt ->
                        val isSel = selectedPrivacy == opt
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSel) ElectricBlue else CardNavy,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) NeonBlue else GlassBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedPrivacy = opt }
                        ) {
                            Text(
                                text = opt,
                                color = if (isSel) Color.White else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Upload Progress Indicator
                if (isUploading) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            progress = { uploadProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = NeonBlue,
                            trackColor = CardNavyVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Uploading to RIC Thokar Campus Feed... ${(uploadProgress * 100).toInt()}%",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Publish Button
                Button(
                    onClick = {
                        isUploading = true
                        coroutineScope.launch {
                            for (p in 1..10) {
                                delay(90)
                                uploadProgress = p / 10f
                            }
                            val tagList = hashtagsText.split(" ").filter { it.isNotBlank() }
                            viewModel.createPost(
                                caption = caption.ifBlank { "Sharing moments at Riphah International College, Thokar Campus." },
                                type = selectedUploadType ?: PostType.PHOTO,
                                hashtags = if (tagList.isNotEmpty()) tagList else listOf("#RIC", "#ThokarCampus"),
                                mediaUri = selectedMediaUri?.toString(),
                                mediaRes = selectedFallbackRes,
                                documentTitle = if (selectedUploadType == PostType.DOCUMENT) (selectedMediaName ?: "RIC_Document.pdf") else null,
                                documentSize = if (selectedUploadType == PostType.DOCUMENT) (selectedMediaSize ?: "1.8 MB") else null
                            )
                            isUploading = false
                            uploadProgress = 0f
                            Toast.makeText(context, "Post published successfully!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !isUploading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricBlue,
                        disabledContainerColor = CardNavyVariant
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("publish_post_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isUploading) "Publishing..." else "Publish Post to Feed",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }
}

@Composable
private fun UploadTypeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) NeonBlue else GlassBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .testTag(tag),
        colors = CardDefaults.cardColors(containerColor = CardNavy)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(gradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.FileUpload,
                contentDescription = null,
                tint = if (isSelected) NeonBlue else TextMuted,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
