package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_wallet")
data class UserWalletEntity(
    @PrimaryKey val id: Int = 1,
    val coinsBalance: Int = 30,
    val totalEarned: Int = 30,
    val totalSpent: Int = 0,
    val username: String = "GojoCreator",
    val avatarUrl: String = ""
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val rewardCoins: Int,
    val currentProgress: Int = 0,
    val targetProgress: Int = 1,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val category: String = "GENERAL" // "GENERATION", "SOCIAL", "DAILY", "EVENT"
)

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val prompt: String,
    val imageUrl: String,
    val videoThumbResName: String = "",
    val style: String = "Anime",
    val cameraMotion: String = "Dynamic Zoom",
    val aspectRatio: String = "9:16",
    val durationSec: Int = 5,
    val authorName: String = "Satoru",
    val authorAvatarRes: String = "",
    val likesCount: Int = 12,
    val isLikedByUser: Boolean = false,
    val isPostedToSocial: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val motionIntensity: Float = 0.8f
)

@Entity(tableName = "admin_events")
data class AdminEventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val bonusCoins: Int,
    val isActive: Boolean = true,
    val tag: String = "SPECIAL EVENT",
    val gradientColorHex: String = "#00E5FF"
)

@Entity(tableName = "coin_transactions")
data class CoinTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Int,
    val type: String, // "EARNED", "SPENT", "ADMIN_GRANT", "TASK_REWARD", "EVENT_BONUS"
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "video_comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val videoId: String,
    val authorName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
