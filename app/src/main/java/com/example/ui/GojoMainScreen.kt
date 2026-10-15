package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.screens.*

@Composable
fun GojoMainScreen(
    viewModel: GojoViewModel = viewModel()
) {
    val wallet by viewModel.wallet.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val communityVideos by viewModel.communityFeed.collectAsStateWithLifecycle()
    val allVideos by viewModel.allVideos.collectAsStateWithLifecycle()
    val activeEvents by viewModel.activeEvents.collectAsStateWithLifecycle()
    val allEvents by viewModel.allEvents.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val promptInput by viewModel.promptInput.collectAsStateWithLifecycle()
    val titleInput by viewModel.titleInput.collectAsStateWithLifecycle()
    val selectedStyle by viewModel.selectedStyle.collectAsStateWithLifecycle()
    val selectedCameraMotion by viewModel.selectedCameraMotion.collectAsStateWithLifecycle()
    val selectedAspectRatio by viewModel.selectedAspectRatio.collectAsStateWithLifecycle()
    val selectedDuration by viewModel.selectedDuration.collectAsStateWithLifecycle()
    val selectedPresetRes by viewModel.selectedImagePreset.collectAsStateWithLifecycle()

    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val generationStep by viewModel.generationStep.collectAsStateWithLifecycle()
    val showPreview by viewModel.showGeneratedPreview.collectAsStateWithLifecycle()
    val lastGeneratedVideo by viewModel.lastGeneratedVideo.collectAsStateWithLifecycle()

    val isAdminUnlocked by viewModel.isAdminUnlocked.collectAsStateWithLifecycle()
    val showAdminLogin by viewModel.showAdminLoginDialog.collectAsStateWithLifecycle()
    val showAdminPanel by viewModel.showAdminPanel.collectAsStateWithLifecycle()
    val adminPasswordInput by viewModel.adminPasswordInput.collectAsStateWithLifecycle()
    val adminPasswordError by viewModel.adminPasswordError.collectAsStateWithLifecycle()

    val showWalletSheet by viewModel.showWalletSheet.collectAsStateWithLifecycle()
    val showCommentsVideoId by viewModel.showCommentsVideoId.collectAsStateWithLifecycle()
    val commentInput by viewModel.commentTextInput.collectAsStateWithLifecycle()
    val commentsList by viewModel.currentVideoComments.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    val unclaimedTasksCount = remember(tasks) {
        tasks.count { it.isCompleted && !it.isClaimed }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("gojo_main_scaffold"),
        containerColor = Color(0xFF0B0E17),
        topBar = {
            GojoTopBar(
                coinsBalance = wallet.coinsBalance,
                isAdminUnlocked = isAdminUnlocked,
                onWalletClick = { viewModel.showWalletSheet.value = true },
                onAdminClick = { viewModel.openAdminLogin() }
            )
        },
        bottomBar = {
            GojoBottomNav(
                currentTab = currentTab,
                unclaimedTasksCount = unclaimedTasksCount,
                onTabSelected = { viewModel.setTab(it) }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = Color(0xFF1B233D),
                        contentColor = Color.White,
                        actionColor = Color(0xFF00E5FF)
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF0B0E17))
        ) {
            Crossfade(targetState = currentTab, label = "tab_transition") { tab ->
                when (tab) {
                    GojoScreenTab.FEED -> {
                        FeedScreen(
                            videos = communityVideos,
                            activeEvents = activeEvents,
                            onLikeClick = { viewModel.toggleLikeVideo(it) },
                            onCommentClick = { viewModel.openComments(it.id) },
                            onRemixClick = { viewModel.remixVideo(it) },
                            onCreateClick = { viewModel.setTab(GojoScreenTab.CREATE) }
                        )
                    }
                    GojoScreenTab.CREATE -> {
                        CreateStudioScreen(
                            promptInput = promptInput,
                            titleInput = titleInput,
                            selectedStyle = selectedStyle,
                            selectedCameraMotion = selectedCameraMotion,
                            selectedAspectRatio = selectedAspectRatio,
                            selectedDuration = selectedDuration,
                            selectedPresetRes = selectedPresetRes,
                            coinsBalance = wallet.coinsBalance,
                            isGenerating = isGenerating,
                            generationStep = generationStep,
                            showPreview = showPreview,
                            lastGeneratedVideo = lastGeneratedVideo,
                            onPromptChange = { viewModel.promptInput.value = it },
                            onTitleChange = { viewModel.titleInput.value = it },
                            onStyleChange = { viewModel.selectedStyle.value = it },
                            onMotionChange = { viewModel.selectedCameraMotion.value = it },
                            onAspectRatioChange = { viewModel.selectedAspectRatio.value = it },
                            onDurationChange = { viewModel.selectedDuration.value = it },
                            onPresetSelect = { preset ->
                                viewModel.selectPromptSuggestion(preset.suggestedPrompt, preset.style, preset.resName)
                            },
                            onGenerateClick = { viewModel.startVideoGeneration() },
                            onPostToFeed = { viewModel.postGeneratedVideoToFeed() },
                            onDismissPreview = { viewModel.dismissGeneratedPreview() }
                        )
                    }
                    GojoScreenTab.TASKS -> {
                        TasksScreen(
                            wallet = wallet,
                            tasks = tasks,
                            activeEvents = activeEvents,
                            onClaimTask = { viewModel.claimTaskReward(it) },
                            onOpenWalletHistory = { viewModel.showWalletSheet.value = true }
                        )
                    }
                    GojoScreenTab.PROFILE -> {
                        ProfileScreen(
                            wallet = wallet,
                            myVideos = allVideos,
                            isAdminUnlocked = isAdminUnlocked,
                            onOpenAdmin = { viewModel.openAdminLogin() },
                            onOpenWallet = { viewModel.showWalletSheet.value = true },
                            onPostDraftToFeed = { viewModel.postExistingVideoToFeed(it) }
                        )
                    }
                }
            }
        }
    }

    // Admin Login Dialog
    AdminLoginDialog(
        isOpen = showAdminLogin,
        passwordInput = adminPasswordInput,
        errorMessage = adminPasswordError,
        onPasswordChange = {
            viewModel.adminPasswordInput.value = it
            viewModel.adminPasswordError.value = null
        },
        onSubmit = { viewModel.submitAdminPassword() },
        onDismiss = { viewModel.showAdminLoginDialog.value = false }
    )

    // Admin Management Panel Dialog
    AdminPanelDialog(
        isOpen = showAdminPanel,
        activeEvents = activeEvents,
        allEvents = allEvents,
        onGrantCoins = { amount, reason ->
            viewModel.adminGrantCoins(amount, reason)
        },
        onCreateEvent = { title, desc, coins, colorHex, tag ->
            viewModel.adminCreateEvent(title, desc, coins, colorHex, tag)
        },
        onToggleEvent = { event ->
            viewModel.adminToggleEvent(event)
        },
        onResetBalance = { amount ->
            viewModel.adminResetBalance(amount)
        },
        onDismiss = { viewModel.showAdminPanel.value = false }
    )

    // Wallet Details Dialog
    WalletDialog(
        isOpen = showWalletSheet,
        wallet = wallet,
        transactions = transactions,
        onEarnCoinsClick = { viewModel.setTab(GojoScreenTab.TASKS) },
        onDismiss = { viewModel.showWalletSheet.value = false }
    )

    // Comments Dialog
    CommentsDialog(
        isOpen = showCommentsVideoId != null,
        comments = commentsList,
        commentInput = commentInput,
        onCommentChange = { viewModel.commentTextInput.value = it },
        onSendComment = { viewModel.sendComment() },
        onDismiss = { viewModel.closeComments() }
    )
}
