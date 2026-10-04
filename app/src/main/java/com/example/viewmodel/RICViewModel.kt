package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MockDataProvider
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NotificationItem(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val type: String, // "Announcement", "Like", "Comment", "Request", "Event"
    val isRead: Boolean = false
)

class RICViewModel : ViewModel() {
    private val _currentUser = MutableStateFlow(MockDataProvider.currentUser)
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // 0: Home, 1: Friends, 2: Upload, 3: AI, 4: Profile
    private val _selectedBottomTab = MutableStateFlow(0)
    val selectedBottomTab: StateFlow<Int> = _selectedBottomTab.asStateFlow()

    // Feed Tabs: "For You", "All", "Recent", "Events"
    private val _feedFilter = MutableStateFlow("For You")
    val feedFilter: StateFlow<String> = _feedFilter.asStateFlow()

    private val _posts = MutableStateFlow(MockDataProvider.getInitialPosts())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _stories = MutableStateFlow(MockDataProvider.getInitialStories())
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    private val _activeStory = MutableStateFlow<Story?>(null)
    val activeStory: StateFlow<Story?> = _activeStory.asStateFlow()

    private val _chats = MutableStateFlow(MockDataProvider.getInitialChats())
    val chats: StateFlow<List<ChatConversation>> = _chats.asStateFlow()

    private val _activeChat = MutableStateFlow<ChatConversation?>(null)
    val activeChat: StateFlow<ChatConversation?> = _activeChat.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _events = MutableStateFlow(MockDataProvider.getInitialEvents())
    val events: StateFlow<List<EventItem>> = _events.asStateFlow()

    private val _officialGroups = MutableStateFlow(MockDataProvider.getOfficialGroups())
    val officialGroups: StateFlow<List<OfficialGroup>> = _officialGroups.asStateFlow()

    private val _reports = MutableStateFlow(MockDataProvider.getInitialReports())
    val reports: StateFlow<List<ReportItem>> = _reports.asStateFlow()

    // AI Chat state
    private val _aiMessages = MutableStateFlow<List<AIMessage>>(
        listOf(
            AIMessage(
                id = "ai_welcome",
                text = "Hello! I am your RIC AI Assistant. Ask me anything: from your studies and assignment help to summarizing documents, study schedules, coding, or college information!",
                isUser = false
            )
        )
    )
    val aiMessages: StateFlow<List<AIMessage>> = _aiMessages.asStateFlow()

    private val _isAILoading = MutableStateFlow(false)
    val isAILoading: StateFlow<Boolean> = _isAILoading.asStateFlow()

    // Admin state
    private val _adminSection = MutableStateFlow("Dashboard")
    val adminSection: StateFlow<String> = _adminSection.asStateFlow()

    private val _isInAdminView = MutableStateFlow(false)
    val isInAdminView: StateFlow<Boolean> = _isInAdminView.asStateFlow()

    // UI overlays
    private val _showNotifications = MutableStateFlow(false)
    val showNotifications: StateFlow<Boolean> = _showNotifications.asStateFlow()

    private val _showSearch = MutableStateFlow(false)
    val showSearch: StateFlow<Boolean> = _showSearch.asStateFlow()

    private val _activeCommentPost = MutableStateFlow<Post?>(null)
    val activeCommentPost: StateFlow<Post?> = _activeCommentPost.asStateFlow()

    private val _comments = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())
    val comments: StateFlow<Map<String, List<Comment>>> = _comments.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    fun selectBottomTab(tab: Int) {
        _selectedBottomTab.value = tab
    }

    fun setFeedFilter(filter: String) {
        _feedFilter.value = filter
    }

    fun toggleLike(postId: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                val newLiked = !post.isLiked
                post.copy(
                    isLiked = newLiked,
                    likesCount = if (newLiked) post.likesCount + 1 else post.likesCount - 1
                )
            } else post
        }
    }

    fun toggleSave(postId: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) post.copy(isSaved = !post.isSaved) else post
        }
    }

    fun openComments(post: Post) {
        _activeCommentPost.value = post
        if (!_comments.value.containsKey(post.id)) {
            val currentMap = _comments.value.toMutableMap()
            currentMap[post.id] = MockDataProvider.getSampleComments(post.id)
            _comments.value = currentMap
        }
    }

    fun closeComments() {
        _activeCommentPost.value = null
    }

    fun addComment(postId: String, text: String) {
        if (text.isBlank()) return
        val currentList = _comments.value[postId]?.toMutableList() ?: mutableListOf()
        val newComment = Comment(
            id = "c_${System.currentTimeMillis()}",
            authorName = _currentUser.value.name,
            authorAvatarRes = _currentUser.value.avatarRes,
            text = text,
            timestamp = "Just now"
        )
        currentList.add(0, newComment)
        val currentMap = _comments.value.toMutableMap()
        currentMap[postId] = currentList
        _comments.value = currentMap

        // Update post comment count
        _posts.value = _posts.value.map {
            if (it.id == postId) it.copy(commentsCount = it.commentsCount + 1) else it
        }
    }

    fun openStory(story: Story) {
        _activeStory.value = story
    }

    fun closeStory() {
        _activeStory.value = null
    }

    fun openChat(chat: ChatConversation) {
        _activeChat.value = chat
        _chatMessages.value = MockDataProvider.getChatMessages(chat.id)
        // Mark as read
        _chats.value = _chats.value.map {
            if (it.id == chat.id) it.copy(unreadCount = 0) else it
        }
    }

    fun closeChat() {
        _activeChat.value = null
    }

    fun sendMessage(text: String, type: MessageType = MessageType.TEXT, attachmentName: String? = null, attachmentSize: String? = null) {
        if (text.isBlank() && attachmentName == null) return
        val current = _chatMessages.value.toMutableList()
        val newMsg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderId = _currentUser.value.id,
            senderName = _currentUser.value.name,
            text = text,
            type = type,
            attachmentName = attachmentName,
            attachmentSize = attachmentSize,
            timestamp = "Just now",
            isFromMe = true
        )
        current.add(newMsg)
        _chatMessages.value = current

        // Update last message in chats list
        _activeChat.value?.let { chat ->
            _chats.value = _chats.value.map {
                if (it.id == chat.id) it.copy(
                    lastMessage = if (type == MessageType.TEXT) text else attachmentName ?: "Media",
                    lastTimestamp = "Just now"
                ) else it
            }
        }
    }

    fun createPost(
        caption: String,
        type: PostType,
        hashtags: List<String>,
        mediaUri: String? = null,
        mediaRes: Int? = null,
        documentTitle: String? = null,
        documentSize: String? = null
    ) {
        val newPost = Post(
            id = "post_${System.currentTimeMillis()}",
            authorId = _currentUser.value.id,
            authorName = _currentUser.value.name,
            authorUsername = _currentUser.value.username,
            authorAvatarRes = _currentUser.value.avatarRes,
            program = _currentUser.value.program,
            type = type,
            caption = caption,
            hashtags = hashtags,
            mediaRes = mediaRes ?: if (type == PostType.DOCUMENT) com.example.R.drawable.img_campus_night else com.example.R.drawable.img_campus_life,
            mediaUri = mediaUri,
            documentTitle = documentTitle,
            documentSize = documentSize,
            likesCount = 0,
            isLiked = false,
            commentsCount = 0,
            sharesCount = 0,
            downloadsCount = 0,
            timestamp = "Just now"
        )
        val updated = _posts.value.toMutableList()
        updated.add(0, newPost)
        _posts.value = updated
        _currentUser.value = _currentUser.value.copy(postsCount = updated.count { it.authorId == _currentUser.value.id })
        _selectedBottomTab.value = 0 // Return to Home feed
    }

    fun addFriend(name: String, studentId: String, program: String) {
        if (name.isBlank()) return
        val newChat = ChatConversation(
            id = "friend_${System.currentTimeMillis()}",
            participantId = "user_${System.currentTimeMillis()}",
            participantName = name.trim(),
            participantProgram = if (program.isNotBlank()) program.trim() else "BSCS",
            lastMessage = "Connected as friends! Say hi 👋",
            lastTimestamp = "Just now",
            unreadCount = 0,
            isOnline = true
        )
        _chats.value = listOf(newChat) + _chats.value.filter { it.participantName != name }
    }

    fun markNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun toggleGroupJoin(groupId: String) {
        _officialGroups.value = _officialGroups.value.map {
            if (it.id == groupId) it.copy(isJoined = !it.isJoined) else it
        }
    }

    fun sendAIMessage(prompt: String) {
        if (prompt.isBlank()) return
        val userMsg = AIMessage("user_${System.currentTimeMillis()}", prompt, isUser = true)
        _aiMessages.value = _aiMessages.value + userMsg
        _isAILoading.value = true

        viewModelScope.launch {
            delay(1200) // Realistic thoughtful processing
            val replyText = generateAIResponse(prompt)
            val aiMsg = AIMessage("ai_${System.currentTimeMillis()}", replyText, isUser = false)
            _aiMessages.value = _aiMessages.value + aiMsg
            _isAILoading.value = false
        }
    }

    private fun generateAIResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("assignment") || lower.contains("help") ->
                "I'd be glad to help you with your assignment! Please provide the assignment topic, key instructions, and any rubrics or specific concepts you'd like me to cover or explain step-by-step."
            lower.contains("summarize") || lower.contains("document") ->
                "I can summarize any document or text for you! Paste the key sections or describe the document, and I'll generate a concise summary highlighting the core takeaways, definitions, and conclusions."
            lower.contains("study plan") || lower.contains("schedule") ->
                "Here is a recommended 4-Week Study Plan for RIC students:\n• Week 1: Core concepts and lecture revisions (2 hrs daily)\n• Week 2: Practical exercises, lab tasks & programming assignments\n• Week 3: Past exam papers review and peer group discussions\n• Week 4: Comprehensive mock test & summary cheat-sheets."
            lower.contains("college") || lower.contains("campus") || lower.contains("thokar") ->
                "Riphah International College, Thokar Campus is renowned for its academic excellence, modern computer labs, state-of-the-art auditorium, and vibrant campus life. Campus hours are 8:00 AM – 4:00 PM (Mon-Fri). You can find official announcements in the Friends > Groups section or Events tab."
            lower.contains("code") || lower.contains("programming") || lower.contains("python") || lower.contains("kotlin") ->
                "Here is an efficient programming solution:\n```kotlin\n// Quick helper function\nfun calculateStudyHours(credits: Int): Int = credits * 3\n```\nMake sure your functions are modular and follow clean architecture principles."
            else ->
                "Great question! Based on the query '$prompt':\n• Key Point 1: Organize your ideas into distinct modular steps.\n• Key Point 2: Review official course materials and cross-reference with credible academic sources.\n• Key Point 3: Test and verify each solution. Let me know if you would like me to expand on any particular part!"
        }
    }

    fun toggleAdminMode() {
        _isInAdminView.value = !_isInAdminView.value
    }

    fun setAdminSection(section: String) {
        _adminSection.value = section
    }

    fun setShowNotifications(show: Boolean) {
        _showNotifications.value = show
    }

    fun setShowSearch(show: Boolean) {
        _showSearch.value = show
    }

    fun login(asAdmin: Boolean = false) {
        if (asAdmin) {
            _currentUser.value = MockDataProvider.adminUser
            _isInAdminView.value = true
        } else {
            _currentUser.value = MockDataProvider.currentUser
            _isInAdminView.value = false
        }
        _isLoggedIn.value = true
    }

    fun logout() {
        _isLoggedIn.value = false
        _isInAdminView.value = false
    }

    // Admin actions
    fun deletePost(postId: String) {
        _posts.value = _posts.value.filter { it.id != postId }
    }

    fun togglePostPublish(postId: String) {
        _posts.value = _posts.value.map {
            if (it.id == postId) it.copy(isPublished = !it.isPublished) else it
        }
    }

    fun resolveReport(reportId: String) {
        _reports.value = _reports.value.map {
            if (it.id == reportId) it.copy(status = "Resolved") else it
        }
    }

    fun createOfficialGroup(name: String, desc: String) {
        val newGrp = OfficialGroup(
            id = "grp_${System.currentTimeMillis()}",
            name = name,
            description = desc,
            membersCount = "1 member",
            isOfficial = true,
            isJoined = true
        )
        _officialGroups.value = _officialGroups.value + newGrp
    }

    fun createEvent(title: String, month: String, day: String, time: String, location: String, desc: String) {
        val newEv = EventItem(
            id = "ev_${System.currentTimeMillis()}",
            title = title,
            month = month,
            day = day,
            time = time,
            location = location,
            description = desc,
            imageRes = com.example.R.drawable.img_campus_life,
            isUpcoming = true,
            isOfficial = true
        )
        _events.value = listOf(newEv) + _events.value

        // Automatically push official announcement to notifications center
        val notif = NotificationItem(
            id = "notif_${System.currentTimeMillis()}",
            title = "Official Event: $title",
            description = "$month $day • $time at $location. $desc",
            time = "Just now",
            type = "Event",
            isRead = false
        )
        _notifications.value = listOf(notif) + _notifications.value
    }

    fun sendAdminAnnouncement(title: String, description: String) {
        val notif = NotificationItem(
            id = "notif_${System.currentTimeMillis()}",
            title = title,
            description = description,
            time = "Just now",
            type = "Announcement",
            isRead = false
        )
        _notifications.value = listOf(notif) + _notifications.value
    }

    fun addStory(text: String, mediaRes: Int = com.example.R.drawable.img_campus_life) {
        val newStory = Story(
            id = "story_${System.currentTimeMillis()}",
            userId = _currentUser.value.id,
            userName = _currentUser.value.name,
            userAvatarRes = _currentUser.value.avatarRes,
            mediaRes = mediaRes,
            text = text,
            timestamp = "Just now"
        )
        _stories.value = listOf(newStory) + _stories.value
    }

    fun startChatWith(userId: String, userName: String, program: String) {
        val existing = _chats.value.find { it.participantId == userId || it.participantName == userName }
        if (existing != null) {
            openChat(existing)
        } else {
            val newChat = ChatConversation(
                id = "chat_${System.currentTimeMillis()}",
                participantId = userId,
                participantName = userName,
                participantProgram = program,
                lastMessage = "Say hello to $userName!",
                lastTimestamp = "Just now",
                unreadCount = 0,
                isOnline = true
            )
            _chats.value = listOf(newChat) + _chats.value
            openChat(newChat)
        }
    }
}
