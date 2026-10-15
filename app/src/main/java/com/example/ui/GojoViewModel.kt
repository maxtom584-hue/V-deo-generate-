package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AdminEventEntity
import com.example.data.local.AppDatabase
import com.example.data.local.CoinTransactionEntity
import com.example.data.local.CommentEntity
import com.example.data.local.TaskEntity
import com.example.data.local.UserWalletEntity
import com.example.data.local.VideoEntity
import com.example.data.repository.GojoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class GojoScreenTab {
    FEED,
    CREATE,
    TASKS,
    PROFILE
}

class GojoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GojoRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = GojoRepository(db.gojoDao())
        viewModelScope.launch {
            repository.initializeSeedData()
        }
    }

    // --- State Flows from Repository ---
    val wallet: StateFlow<UserWalletEntity> = repository.walletFlow
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserWalletEntity()
        )

    val tasks: StateFlow<List<TaskEntity>> = repository.tasksFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val communityFeed: StateFlow<List<VideoEntity>> = repository.communityVideosFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allVideos: StateFlow<List<VideoEntity>> = repository.allVideosFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activeEvents: StateFlow<List<AdminEventEntity>> = repository.activeEventsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allEvents: StateFlow<List<AdminEventEntity>> = repository.allEventsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val transactions: StateFlow<List<CoinTransactionEntity>> = repository.transactionsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Navigation & UI State ---
    var currentTab = MutableStateFlow(GojoScreenTab.FEED)
        private set

    fun setTab(tab: GojoScreenTab) {
        currentTab.value = tab
    }

    // --- Video Generator State ---
    var promptInput = MutableStateFlow("")
    var titleInput = MutableStateFlow("")
    var selectedStyle = MutableStateFlow("Anime")
    var selectedCameraMotion = MutableStateFlow("Dynamic Zoom")
    var selectedAspectRatio = MutableStateFlow("9:16")
    var selectedDuration = MutableStateFlow(5)
    var selectedImagePreset = MutableStateFlow("video_thumb_infinity_1788966908207")

    var isGenerating = MutableStateFlow(false)
        private set
    var generationStep = MutableStateFlow("")
        private set
    var lastGeneratedVideo = MutableStateFlow<VideoEntity?>(null)
        private set
    var showGeneratedPreview = MutableStateFlow(false)
        private set

    // --- Admin State ---
    var isAdminUnlocked = MutableStateFlow(false)
        private set
    var showAdminLoginDialog = MutableStateFlow(false)
    var showAdminPanel = MutableStateFlow(false)
    var adminPasswordInput = MutableStateFlow("")
    var adminPasswordError = MutableStateFlow<String?>(null)

    // --- Modals & Sheets ---
    var showWalletSheet = MutableStateFlow(false)
    var showCommentsVideoId = MutableStateFlow<String?>(null)
    var commentTextInput = MutableStateFlow("")

    val currentVideoComments: StateFlow<List<CommentEntity>> = showCommentsVideoId
        .flatMapLatest { videoId ->
            if (videoId == null) flowOf(emptyList())
            else repository.getCommentsForVideo(videoId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var snackbarMessage = MutableStateFlow<String?>(null)

    fun clearSnackbar() {
        snackbarMessage.value = null
    }

    // --- Actions: Video Generation ---
    fun startVideoGeneration() {
        val prompt = promptInput.value.trim()
        if (prompt.isBlank()) {
            snackbarMessage.value = "Please enter a prompt for your video!"
            return
        }

        val currentCoins = wallet.value.coinsBalance
        if (currentCoins < 10) {
            snackbarMessage.value = "Insufficient Gojo Coins! (Need 10, you have $currentCoins). Claim tasks or check Admin!"
            return
        }

        viewModelScope.launch {
            isGenerating.value = true
            generationStep.value = "Analyzing base image and prompt..."
            delay(900)

            generationStep.value = "Consulting AI director for scene composition..."
            delay(1000)

            generationStep.value = "Synthesizing ${selectedCameraMotion.value} optical flow & VFX..."
            delay(1100)

            generationStep.value = "Rendering video frames & keyframe interpolation..."
            delay(900)

            val result = repository.generateVideo(
                title = titleInput.value.trim(),
                prompt = prompt,
                imageUrl = "",
                style = selectedStyle.value,
                cameraMotion = selectedCameraMotion.value,
                aspectRatio = selectedAspectRatio.value,
                durationSec = selectedDuration.value,
                thumbResName = selectedImagePreset.value
            )

            isGenerating.value = false

            result.fold(
                onSuccess = { video ->
                    lastGeneratedVideo.value = video
                    showGeneratedPreview.value = true
                    snackbarMessage.value = "Video generated successfully! (-10 Gojo Coins)"
                },
                onFailure = { error ->
                    snackbarMessage.value = error.message ?: "Generation failed"
                }
            )
        }
    }

    fun dismissGeneratedPreview() {
        showGeneratedPreview.value = false
    }

    fun postGeneratedVideoToFeed() {
        val video = lastGeneratedVideo.value ?: return
        viewModelScope.launch {
            repository.postVideoToSocial(video)
            showGeneratedPreview.value = false
            snackbarMessage.value = "Video posted to Community Feed! Task progress updated!"
            currentTab.value = GojoScreenTab.FEED
        }
    }

    fun postExistingVideoToFeed(video: VideoEntity) {
        viewModelScope.launch {
            repository.postVideoToSocial(video)
            snackbarMessage.value = "Video posted to Community Feed!"
        }
    }

    // --- Actions: Social Interactions ---
    fun toggleLikeVideo(video: VideoEntity) {
        viewModelScope.launch {
            val liked = repository.toggleLikeVideo(video)
            if (liked) {
                snackbarMessage.value = "Liked '${video.title}'"
            }
        }
    }

    fun openComments(videoId: String) {
        showCommentsVideoId.value = videoId
        commentTextInput.value = ""
    }

    fun closeComments() {
        showCommentsVideoId.value = null
    }

    fun sendComment() {
        val videoId = showCommentsVideoId.value ?: return
        val text = commentTextInput.value.trim()
        if (text.isBlank()) return

        viewModelScope.launch {
            repository.addComment(videoId, text)
            commentTextInput.value = ""
        }
    }

    fun remixVideo(video: VideoEntity) {
        promptInput.value = video.prompt
        titleInput.value = "Remix: ${video.title}"
        selectedStyle.value = video.style
        selectedCameraMotion.value = video.cameraMotion
        selectedAspectRatio.value = video.aspectRatio
        if (video.videoThumbResName.isNotBlank()) {
            selectedImagePreset.value = video.videoThumbResName
        }
        currentTab.value = GojoScreenTab.CREATE
        snackbarMessage.value = "Loaded settings to Creator Studio!"
    }

    // --- Actions: Tasks & Rewards ---
    fun claimTaskReward(task: TaskEntity) {
        viewModelScope.launch {
            val result = repository.claimTaskReward(task.id)
            result.fold(
                onSuccess = { coins ->
                    snackbarMessage.value = "Claimed +$coins Gojo Coins for completing '${task.title}'!"
                },
                onFailure = { error ->
                    snackbarMessage.value = error.message ?: "Failed to claim task"
                }
            )
        }
    }

    // --- Actions: Admin Mode ---
    fun openAdminLogin() {
        if (isAdminUnlocked.value) {
            showAdminPanel.value = true
        } else {
            adminPasswordInput.value = ""
            adminPasswordError.value = null
            showAdminLoginDialog.value = true
        }
    }

    fun submitAdminPassword() {
        val input = adminPasswordInput.value
        if (repository.verifyAdminPassword(input)) {
            isAdminUnlocked.value = true
            showAdminLoginDialog.value = false
            showAdminPanel.value = true
            adminPasswordError.value = null
            snackbarMessage.value = "Welcome back, Admin Gojo!"
        } else {
            adminPasswordError.value = "Incorrect password! Password is 'Gojo'."
        }
    }

    fun adminGrantCoins(amount: Int, reason: String) {
        viewModelScope.launch {
            val result = repository.adminGrantCoins(amount, reason)
            result.fold(
                onSuccess = { granted ->
                    snackbarMessage.value = "Successfully granted +$granted Gojo Coins!"
                },
                onFailure = { error ->
                    snackbarMessage.value = error.message ?: "Grant failed"
                }
            )
        }
    }

    fun adminCreateEvent(title: String, desc: String, bonusCoins: Int, colorHex: String, tag: String) {
        viewModelScope.launch {
            val result = repository.adminCreateEvent(title, desc, bonusCoins, colorHex, tag)
            result.fold(
                onSuccess = { event ->
                    snackbarMessage.value = "Live Event '${event.title}' launched!"
                },
                onFailure = { error ->
                    snackbarMessage.value = error.message ?: "Failed to create event"
                }
            )
        }
    }

    fun adminToggleEvent(event: AdminEventEntity) {
        viewModelScope.launch {
            repository.adminToggleEventStatus(event)
            snackbarMessage.value = "Event status updated!"
        }
    }

    fun adminResetBalance(amount: Int) {
        viewModelScope.launch {
            repository.adminResetUserWallet(amount)
            snackbarMessage.value = "Balance reset to $amount Gojo Coins"
        }
    }

    fun selectPromptSuggestion(prompt: String, style: String, presetImg: String) {
        promptInput.value = prompt
        selectedStyle.value = style
        selectedImagePreset.value = presetImg
    }
}
