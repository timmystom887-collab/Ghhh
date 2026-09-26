package com.example.agent.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agent.data.local.entity.ProactiveActionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProactiveActionDao {
    @Query("SELECT * FROM proactive_actions ORDER BY timestamp DESC")
    fun getAllProactiveActions(): Flow<List<ProactiveActionEntity>>

    @Query("SELECT * FROM proactive_actions WHERE status = 'PENDING' ORDER BY timestamp DESC")
    fun getPendingProactiveActions(): Flow<List<ProactiveActionEntity>>

    @Query("SELECT * FROM proactive_actions WHERE id = :id")
    suspend fun getActionById(id: Long): ProactiveActionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: ProactiveActionEntity): Long

    @Update
    suspend fun updateAction(action: ProactiveActionEntity)

    @Query("UPDATE proactive_actions SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Delete
    suspend fun deleteAction(action: ProactiveActionEntity)

    @Query("DELETE FROM proactive_actions")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM proactive_actions WHERE status = 'PENDING'")
    suspend fun getPendingCount(): Int
}
