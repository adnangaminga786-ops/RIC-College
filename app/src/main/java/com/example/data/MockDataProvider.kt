package com.example.data

import com.example.R
import com.example.model.*

object MockDataProvider {
    val currentUser = User(
        id = "user_001",
        name = "Abdul Sattar",
        username = "abdulsattar",
        studentId = "RIC-2025-015",
        role = UserRole.STUDENT,
        program = "BS Computer Science",
        campus = "RIC Thokar Campus",
        avatarRes = R.drawable.img_campus_life,
        bio = "Student at Riphah International College, Thokar Campus ✍️ #RIC #StudentLife",
        postsCount = 0,
        followersCount = "0",
        followingCount = "0",
        isOnline = true
    )

    val adminUser = User(
        id = "admin_001",
        name = "Campus Administrator",
        username = "ric_admin",
        studentId = "STAFF-ADM-01",
        role = UserRole.ADMIN,
        program = "Administration",
        campus = "RIC Thokar Campus",
        avatarRes = R.drawable.img_ric_logo,
        bio = "Official Administration of Riphah International College, Thokar Campus.",
        postsCount = 0,
        followersCount = "0",
        followingCount = "0",
        isOnline = true
    )

    // No pre-loaded posts - all start empty until uploaded
    fun getInitialPosts(): List<Post> {
        return emptyList()
    }

    // No pre-loaded stories
    fun getInitialStories(): List<Story> {
        return emptyList()
    }

    // No pre-loaded chats or fake contacts
    fun getInitialChats(): List<ChatConversation> {
        return emptyList()
    }

    // Only events created by admin; default starts empty or official college events
    fun getInitialEvents(): List<EventItem> {
        return emptyList()
    }

    // No pre-loaded groups - groups only created by Admin
    fun getOfficialGroups(): List<OfficialGroup> {
        return emptyList()
    }

    fun getSampleComments(postId: String): List<Comment> {
        return emptyList()
    }

    fun getChatMessages(chatId: String): List<ChatMessage> {
        return emptyList()
    }

    fun getInitialReports(): List<ReportItem> {
        return emptyList()
    }
}
