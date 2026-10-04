package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CommentBottomSheet
import com.example.ui.components.NotificationCenterDialog
import com.example.ui.components.RICBottomNavBar
import com.example.ui.screens.*
import com.example.ui.theme.DeepNavyDark
import com.example.ui.theme.RICFriendsTheme
import com.example.viewmodel.RICViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RICFriendsTheme {
                val viewModel: RICViewModel = viewModel()
                val isLoggedIn by viewModel.isLoggedIn.collectAsState()
                val selectedTab by viewModel.selectedBottomTab.collectAsState()
                val isInAdminView by viewModel.isInAdminView.collectAsState()
                val activeChat by viewModel.activeChat.collectAsState()
                val activeStory by viewModel.activeStory.collectAsState()
                val showSearch by viewModel.showSearch.collectAsState()
                val showNotifications by viewModel.showNotifications.collectAsState()
                val notifications by viewModel.notifications.collectAsState()
                val activeCommentPost by viewModel.activeCommentPost.collectAsState()
                val commentsMap by viewModel.comments.collectAsState()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DeepNavyDark)
                ) {
                    when {
                        !isLoggedIn -> {
                            LoginScreen(viewModel = viewModel)
                        }
                        isInAdminView -> {
                            AdminDashboardScreen(viewModel = viewModel)
                        }
                        activeStory != null -> {
                            StoryViewerScreen(
                                story = activeStory!!,
                                onClose = { viewModel.closeStory() }
                            )
                        }
                        activeChat != null -> {
                            ChatDetailScreen(
                                chat = activeChat!!,
                                viewModel = viewModel
                            )
                        }
                        showSearch -> {
                            SearchScreen(
                                viewModel = viewModel,
                                onClose = { viewModel.setShowSearch(false) }
                            )
                        }
                        else -> {
                            Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                bottomBar = {
                                    RICBottomNavBar(
                                        selectedTab = selectedTab,
                                        onTabSelected = { tab ->
                                            viewModel.selectBottomTab(tab)
                                        }
                                    )
                                },
                                containerColor = DeepNavyDark
                            ) { innerPadding ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(bottom = innerPadding.calculateBottomPadding())
                                ) {
                                    when (selectedTab) {
                                        0 -> HomeScreen(viewModel = viewModel)
                                        1 -> FriendsScreen(viewModel = viewModel)
                                        2 -> UploadScreen(viewModel = viewModel)
                                        3 -> AIScreen(viewModel = viewModel)
                                        4 -> ProfileScreen(viewModel = viewModel)
                                    }
                                }
                            }
                        }
                    }

                    // Comments Bottom Sheet Modal
                    activeCommentPost?.let { post ->
                        val postComments = commentsMap[post.id] ?: emptyList()
                        CommentBottomSheet(
                            post = post,
                            comments = postComments,
                            onDismiss = { viewModel.closeComments() },
                            onAddComment = { text -> viewModel.addComment(post.id, text) }
                        )
                    }

                    // Notification Center Dialog
                    if (showNotifications) {
                        NotificationCenterDialog(
                            notifications = notifications,
                            onDismiss = {
                                viewModel.markNotificationsAsRead()
                                viewModel.setShowNotifications(false)
                            }
                        )
                    }
                }
            }
        }
    }
}
