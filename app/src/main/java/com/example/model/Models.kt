package com.example.model

import com.example.R

enum class UserRole {
    STUDENT,
    ADMIN
}

enum class PostType {
    VIDEO,
    PHOTO,
    DOCUMENT
}

enum class PrivacyLevel {
    PUBLIC,
    COLLEGE_ONLY,
    FRIENDS_ONLY
}

data class User(
    val id: String,
    val name: String,
    val username: String,
    val studentId: String,
    val role: UserRole = UserRole.STUDENT,
    val program: String = "BS Computer Science",
    val campus: String = "RIC Thokar Campus",
    val avatarRes: Int = R.drawable.ic_launcher_foreground,
    val bio: String = "Dream big, work hard, make it happen. 💪 #RIC",
    val postsCount: Int = 124,
    val followersCount: String = "2.4K",
    val followingCount: String = "458",
    val isOnline: Boolean = true,
    val status: String = "Active"
)

data class Post(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorUsername: String,
    val authorAvatarRes: Int = R.drawable.ic_launcher_foreground,
    val program: String = "RIC Thokar Campus",
    val type: PostType = PostType.VIDEO,
    val caption: String,
    val hashtags: List<String> = listOf("#RIC", "#CampusLife", "#ThokarCampus"),
    val mediaRes: Int = R.drawable.img_campus_night,
    val mediaUri: String? = null,
    val documentTitle: String? = null,
    val documentSize: String? = null,
    val audioTrack: String = "Original Sound - RIC Official",
    val durationText: String = "00:58",
    val likesCount: Int = 2412,
    val commentsCount: Int = 156,
    val sharesCount: Int = 342,
    val downloadsCount: Int = 89,
    val timestamp: String = "2 days ago",
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val isPublished: Boolean = true
)

data class Comment(
    val id: String,
    val authorName: String,
    val authorAvatarRes: Int = R.drawable.ic_launcher_foreground,
    val text: String,
    val timestamp: String,
    val likesCount: Int = 12
)

data class Story(
    val id: String,
    val userId: String,
    val userName: String,
    val userAvatarRes: Int = R.drawable.ic_launcher_foreground,
    val mediaRes: Int = R.drawable.img_campus_night,
    val text: String = "",
    val timestamp: String = "2h ago",
    val isSeen: Boolean = false
)

enum class MessageType {
    TEXT,
    PHOTO,
    VOICE,
    DOCUMENT
}

data class ChatMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val type: MessageType = MessageType.TEXT,
    val attachmentName: String? = null,
    val attachmentSize: String? = null,
    val timestamp: String = "10:24 AM",
    val isFromMe: Boolean = false
)

data class ChatConversation(
    val id: String,
    val participantId: String,
    val participantName: String,
    val participantProgram: String = "BSCS",
    val participantAvatarRes: Int = R.drawable.ic_launcher_foreground,
    val lastMessage: String,
    val lastTimestamp: String,
    val unreadCount: Int = 0,
    val isOnline: Boolean = true,
    val isTyping: Boolean = false,
    val lastMessageType: MessageType = MessageType.TEXT
)

data class EventItem(
    val id: String,
    val title: String,
    val month: String,
    val day: String,
    val time: String,
    val location: String,
    val description: String,
    val imageRes: Int = R.drawable.img_campus_life,
    val isUpcoming: Boolean = true,
    val isOfficial: Boolean = true
)

data class OfficialGroup(
    val id: String,
    val name: String,
    val description: String,
    val membersCount: String,
    val isOfficial: Boolean = true, // Students cannot create groups
    val isJoined: Boolean = false,
    val category: String = "Official"
)

data class CollegeDocument(
    val id: String,
    val title: String,
    val category: String,
    val size: String,
    val date: String,
    val downloadCount: Int = 142
)

data class ReportItem(
    val id: String,
    val targetType: String, // "Post", "User", "Comment"
    val reportedItem: String,
    val reportedBy: String,
    val reason: String,
    val timestamp: String,
    val status: String = "Pending"
)

data class AIMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
