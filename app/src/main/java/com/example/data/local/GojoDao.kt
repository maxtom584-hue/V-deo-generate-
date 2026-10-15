package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GojoDao {

    // --- User Wallet ---
    @Query("SELECT * FROM user_wallet WHERE id = 1 LIMIT 1")
    fun getUserWallet(): Flow<UserWalletEntity?>

    @Query("SELECT * FROM user_wallet WHERE id = 1 LIMIT 1")
    suspend fun getUserWalletSync(): UserWalletEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: UserWalletEntity)

    @Update
    suspend fun updateWallet(wallet: UserWalletEntity)

    // --- Tasks ---
    @Query("SELECT * FROM tasks ORDER BY isClaimed ASC, isCompleted DESC, rewardCoins DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: String): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    // --- Videos ---
    @Query("SELECT * FROM videos WHERE isPostedToSocial = 1 ORDER BY createdAt DESC")
    fun getCommunityVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos ORDER BY createdAt DESC")
    fun getAllVideos(): Flow<List<VideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity)

    @Update
    suspend fun updateVideo(video: VideoEntity)

    @Query("DELETE FROM videos WHERE id = :videoId")
    suspend fun deleteVideo(videoId: String)

    // --- Admin Events ---
    @Query("SELECT * FROM admin_events ORDER BY isActive DESC")
    fun getAllEvents(): Flow<List<AdminEventEntity>>

    @Query("SELECT * FROM admin_events WHERE isActive = 1")
    fun getActiveEvents(): Flow<List<AdminEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<AdminEventEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: AdminEventEntity)

    @Update
    suspend fun updateEvent(event: AdminEventEntity)

    // --- Transactions ---
    @Query("SELECT * FROM coin_transactions ORDER BY timestamp DESC LIMIT 50")
    fun getRecentTransactions(): Flow<List<CoinTransactionEntity>>

    @Insert
    suspend fun insertTransaction(transaction: CoinTransactionEntity)

    // --- Comments ---
    @Query("SELECT * FROM video_comments WHERE videoId = :videoId ORDER BY timestamp ASC")
    fun getCommentsForVideo(videoId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)
}
