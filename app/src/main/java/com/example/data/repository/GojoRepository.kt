package com.example.data.repository

import com.example.data.ai.GeminiVideoService
import com.example.data.local.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.UUID

class GojoRepository(
    private val dao: GojoDao,
    private val geminiService: GeminiVideoService = GeminiVideoService()
) {

    val walletFlow: Flow<UserWalletEntity?> = dao.getUserWallet()
    val tasksFlow: Flow<List<TaskEntity>> = dao.getAllTasks()
    val communityVideosFlow: Flow<List<VideoEntity>> = dao.getCommunityVideos()
    val allVideosFlow: Flow<List<VideoEntity>> = dao.getAllVideos()
    val activeEventsFlow: Flow<List<AdminEventEntity>> = dao.getActiveEvents()
    val allEventsFlow: Flow<List<AdminEventEntity>> = dao.getAllEvents()
    val transactionsFlow: Flow<List<CoinTransactionEntity>> = dao.getRecentTransactions()

    fun getCommentsForVideo(videoId: String): Flow<List<CommentEntity>> = dao.getCommentsForVideo(videoId)

    suspend fun initializeSeedData() = withContext(Dispatchers.IO) {
        // 1. Initialize Wallet if not present
        val currentWallet = dao.getUserWalletSync()
        if (currentWallet == null) {
            val initialWallet = UserWalletEntity(
                id = 1,
                coinsBalance = 30,
                totalEarned = 30,
                totalSpent = 0,
                username = "GojoCreator"
            )
            dao.insertWallet(initialWallet)
            dao.insertTransaction(
                CoinTransactionEntity(
                    amount = 30,
                    type = "WELCOME_GIFT",
                    description = "Welcome to Gojo Video! Initial creator grant of 30 Gojo Coins"
                )
            )
        }

        // 2. Initialize Default Tasks
        val initialTasks = listOf(
            TaskEntity(
                id = "task_first_video",
                title = "First Sparks",
                description = "Generate your very first AI video based on an image and prompt",
                rewardCoins = 25,
                currentProgress = 0,
                targetProgress = 1,
                isCompleted = false,
                isClaimed = false,
                category = "GENERATION"
            ),
            TaskEntity(
                id = "task_post_video",
                title = "Social Sorcerer",
                description = "Post any of your generated videos to the Community Social Feed",
                rewardCoins = 20,
                currentProgress = 0,
                targetProgress = 1,
                isCompleted = false,
                isClaimed = false,
                category = "SOCIAL"
            ),
            TaskEntity(
                id = "task_daily_checkin",
                title = "Daily Limitless Focus",
                description = "Check in today to claim your daily creator stipend",
                rewardCoins = 10,
                currentProgress = 1,
                targetProgress = 1,
                isCompleted = true,
                isClaimed = false,
                category = "DAILY"
            ),
            TaskEntity(
                id = "task_like_videos",
                title = "Trend Hunter",
                description = "Like 3 videos created by other artists in the community",
                rewardCoins = 15,
                currentProgress = 0,
                targetProgress = 3,
                isCompleted = false,
                isClaimed = false,
                category = "SOCIAL"
            ),
            TaskEntity(
                id = "task_anime_style",
                title = "Jujutsu Animation Style",
                description = "Generate a video using the Anime art style preset",
                rewardCoins = 15,
                currentProgress = 0,
                targetProgress = 1,
                isCompleted = false,
                isClaimed = false,
                category = "GENERATION"
            ),
            TaskEntity(
                id = "task_event_participate",
                title = "Infinity Void Challenger",
                description = "Participate in an active Admin hosted community event",
                rewardCoins = 30,
                currentProgress = 0,
                targetProgress = 1,
                isCompleted = false,
                isClaimed = false,
                category = "EVENT"
            )
        )
        dao.insertTasks(initialTasks)

        // 3. Initialize Admin Events
        val initialEvents = listOf(
            AdminEventEntity(
                id = "event_infinity_void",
                title = "Infinity Void Festival",
                description = "Host Gojo activated the limitless domain! Bonus Gojo Coins for all creator generations.",
                bonusCoins = 50,
                isActive = true,
                tag = "LIVE ADMIN EVENT",
                gradientColorHex = "#00E5FF"
            ),
            AdminEventEntity(
                id = "event_anime_expo",
                title = "Neo-Tokyo Animation Gala",
                description = "Showcase your best cinematic and anime video clips to the community.",
                bonusCoins = 35,
                isActive = true,
                tag = "WEEKLY CHALLENGE",
                gradientColorHex = "#B388FF"
            )
        )
        dao.insertEvents(initialEvents)

        // 4. Seed Initial Community Feed Videos if empty
        val existingFeed = dao.getCommunityVideos().firstOrNull()
        if (existingFeed.isNullOrEmpty()) {
            val defaultVideos = listOf(
                VideoEntity(
                    id = "video_seed_1",
                    title = "Domain Expansion: Infinite Void",
                    prompt = "Gojo unleashing Infinite Void, starry cosmos unfolding, glowing cyan eyes, floating in boundless space with azure particle auras",
                    imageUrl = "",
                    videoThumbResName = "video_thumb_infinity_1788966908207",
                    style = "Anime",
                    cameraMotion = "Dynamic Zoom",
                    aspectRatio = "9:16",
                    durationSec = 5,
                    authorName = "Satoru G.",
                    likesCount = 142,
                    isLikedByUser = false,
                    isPostedToSocial = true,
                    createdAt = System.currentTimeMillis() - 3600000 * 4
                ),
                VideoEntity(
                    id = "video_seed_2",
                    title = "Cyber Tokyo Midnight",
                    prompt = "Cyberpunk anime protagonist speeding on futuristic hover-bike through rain-slicked neon street, holographic ads and reflection puddles",
                    imageUrl = "",
                    videoThumbResName = "video_thumb_cyber_1788966923703",
                    style = "Cyberpunk",
                    cameraMotion = "Pan Right",
                    aspectRatio = "9:16",
                    durationSec = 5,
                    authorName = "KiraNeon",
                    likesCount = 89,
                    isLikedByUser = false,
                    isPostedToSocial = true,
                    createdAt = System.currentTimeMillis() - 3600000 * 12
                )
            )
            for (vid in defaultVideos) {
                dao.insertVideo(vid)
                // Add sample comment
                dao.insertComment(
                    CommentEntity(
                        id = UUID.randomUUID().toString(),
                        videoId = vid.id,
                        authorName = "Megumi",
                        text = "The camera motion and lighting on this generation are insane!",
                        timestamp = System.currentTimeMillis() - 3600000
                    )
                )
            }
        }
    }

    suspend fun generateVideo(
        title: String,
        prompt: String,
        imageUrl: String,
        style: String,
        cameraMotion: String,
        aspectRatio: String,
        durationSec: Int,
        thumbResName: String = ""
    ): Result<VideoEntity> = withContext(Dispatchers.IO) {
        val wallet = dao.getUserWalletSync() ?: return@withContext Result.failure(Exception("Wallet not found"))
        val cost = 10
        if (wallet.coinsBalance < cost) {
            return@withContext Result.failure(Exception("Insufficient Gojo Coins! You have ${wallet.coinsBalance} coins, but video generation costs $cost Gojo Coins. Complete tasks or request coins from the Admin."))
        }

        // Deduct coins
        val newBalance = wallet.coinsBalance - cost
        val newSpent = wallet.totalSpent + cost
        dao.updateWallet(wallet.copy(coinsBalance = newBalance, totalSpent = newSpent))

        // Log transaction
        dao.insertTransaction(
            CoinTransactionEntity(
                amount = -cost,
                type = "SPENT",
                description = "Generated video: '${if (title.isNotBlank()) title else "Custom Video"}' (-$cost Gojo Coins)"
            )
        )

        // Call Gemini for scene direction & enhancement
        val plan = geminiService.enhanceVideoPrompt(prompt, style, cameraMotion)

        val videoId = UUID.randomUUID().toString()
        val video = VideoEntity(
            id = videoId,
            title = if (title.isNotBlank()) title else "Generated: ${prompt.take(24)}...",
            prompt = plan.enhancedPrompt,
            imageUrl = imageUrl,
            videoThumbResName = if (thumbResName.isNotBlank()) thumbResName else if (style == "Anime") "video_thumb_infinity_1788966908207" else "video_thumb_cyber_1788966923703",
            style = style,
            cameraMotion = plan.cameraMotion,
            aspectRatio = aspectRatio,
            durationSec = durationSec,
            authorName = wallet.username,
            likesCount = 0,
            isLikedByUser = false,
            isPostedToSocial = false,
            createdAt = System.currentTimeMillis()
        )
        dao.insertVideo(video)

        // Progress "First Video" task
        updateTaskProgress("task_first_video", 1)

        // If anime style, progress anime task
        if (style.equals("Anime", ignoreCase = true)) {
            updateTaskProgress("task_anime_style", 1)
        }

        // Check active events for event participation task
        val activeEvents = dao.getActiveEvents().firstOrNull()
        if (!activeEvents.isNullOrEmpty()) {
            updateTaskProgress("task_event_participate", 1)
        }

        return@withContext Result.success(video)
    }

    suspend fun postVideoToSocial(video: VideoEntity): Result<Unit> = withContext(Dispatchers.IO) {
        val updated = video.copy(isPostedToSocial = true)
        dao.updateVideo(updated)

        // Progress "Post Video" task
        updateTaskProgress("task_post_video", 1)

        Result.success(Unit)
    }

    suspend fun toggleLikeVideo(video: VideoEntity): Boolean = withContext(Dispatchers.IO) {
        val newLiked = !video.isLikedByUser
        val newCount = if (newLiked) video.likesCount + 1 else (video.likesCount - 1).coerceAtLeast(0)
        dao.updateVideo(video.copy(isLikedByUser = newLiked, likesCount = newCount))

        if (newLiked) {
            // Increment like videos task
            val task = dao.getTaskById("task_like_videos")
            if (task != null && !task.isCompleted) {
                val newProgress = (task.currentProgress + 1).coerceAtMost(task.targetProgress)
                dao.updateTask(
                    task.copy(
                        currentProgress = newProgress,
                        isCompleted = newProgress >= task.targetProgress
                    )
                )
            }
        }
        newLiked
    }

    suspend fun addComment(videoId: String, text: String): CommentEntity = withContext(Dispatchers.IO) {
        val wallet = dao.getUserWalletSync()
        val author = wallet?.username ?: "GojoCreator"
        val comment = CommentEntity(
            id = UUID.randomUUID().toString(),
            videoId = videoId,
            authorName = author,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        dao.insertComment(comment)
        comment
    }

    private suspend fun updateTaskProgress(taskId: String, increment: Int) {
        val task = dao.getTaskById(taskId) ?: return
        if (task.isCompleted) return
        val newProg = (task.currentProgress + increment).coerceAtMost(task.targetProgress)
        dao.updateTask(
            task.copy(
                currentProgress = newProg,
                isCompleted = newProg >= task.targetProgress
            )
        )
    }

    suspend fun claimTaskReward(taskId: String): Result<Int> = withContext(Dispatchers.IO) {
        val task = dao.getTaskById(taskId) ?: return@withContext Result.failure(Exception("Task not found"))
        if (!task.isCompleted) {
            return@withContext Result.failure(Exception("Task is not completed yet!"))
        }
        if (task.isClaimed) {
            return@withContext Result.failure(Exception("Reward already claimed!"))
        }

        val wallet = dao.getUserWalletSync() ?: return@withContext Result.failure(Exception("Wallet not found"))
        val reward = task.rewardCoins

        // Check if double coins event is active!
        val activeEvents = dao.getActiveEvents().firstOrNull() ?: emptyList()
        val hasMultiplier = activeEvents.any { it.title.contains("Infinity Void", ignoreCase = true) }
        val finalReward = if (hasMultiplier) reward * 2 else reward

        // Update task as claimed
        dao.updateTask(task.copy(isClaimed = true))

        // Credit coins
        val newBalance = wallet.coinsBalance + finalReward
        val newEarned = wallet.totalEarned + finalReward
        dao.updateWallet(wallet.copy(coinsBalance = newBalance, totalEarned = newEarned))

        // Log transaction
        dao.insertTransaction(
            CoinTransactionEntity(
                amount = finalReward,
                type = "TASK_REWARD",
                description = "Task Reward: ${task.title} (+${finalReward} Gojo Coins${if (hasMultiplier) " [2x Event Bonus!]" else ""})"
            )
        )

        Result.success(finalReward)
    }

    // --- Admin Operations ---

    fun verifyAdminPassword(input: String): Boolean {
        return input.trim().equals("Gojo", ignoreCase = false) || input.trim().equals("gojo", ignoreCase = true)
    }

    suspend fun adminGrantCoins(amount: Int, reason: String): Result<Int> = withContext(Dispatchers.IO) {
        if (amount <= 0) {
            return@withContext Result.failure(Exception("Amount must be greater than 0"))
        }
        val wallet = dao.getUserWalletSync() ?: return@withContext Result.failure(Exception("Wallet not found"))
        val newBalance = wallet.coinsBalance + amount
        val newEarned = wallet.totalEarned + amount
        dao.updateWallet(wallet.copy(coinsBalance = newBalance, totalEarned = newEarned))

        dao.insertTransaction(
            CoinTransactionEntity(
                amount = amount,
                type = "ADMIN_GRANT",
                description = "Admin Grant: ${if (reason.isNotBlank()) reason else "Authorized by Admin"} (+$amount Gojo Coins)"
            )
        )

        Result.success(amount)
    }

    suspend fun adminCreateEvent(
        title: String,
        description: String,
        bonusCoins: Int,
        gradientHex: String = "#00E5FF",
        tag: String = "LIVE ADMIN EVENT"
    ): Result<AdminEventEntity> = withContext(Dispatchers.IO) {
        if (title.isBlank()) {
            return@withContext Result.failure(Exception("Event title cannot be empty"))
        }
        val event = AdminEventEntity(
            id = "event_${UUID.randomUUID()}",
            title = title,
            description = description,
            bonusCoins = bonusCoins,
            isActive = true,
            tag = tag,
            gradientColorHex = gradientHex
        )
        dao.insertEvent(event)
        Result.success(event)
    }

    suspend fun adminToggleEventStatus(event: AdminEventEntity): Result<Unit> = withContext(Dispatchers.IO) {
        dao.updateEvent(event.copy(isActive = !event.isActive))
        Result.success(Unit)
    }

    suspend fun adminResetUserWallet(amount: Int = 100): Result<Unit> = withContext(Dispatchers.IO) {
        val wallet = dao.getUserWalletSync() ?: UserWalletEntity()
        dao.updateWallet(wallet.copy(coinsBalance = amount))
        dao.insertTransaction(
            CoinTransactionEntity(
                amount = amount,
                type = "ADMIN_GRANT",
                description = "Admin reset balance to $amount Gojo Coins"
            )
        )
        Result.success(Unit)
    }
}
