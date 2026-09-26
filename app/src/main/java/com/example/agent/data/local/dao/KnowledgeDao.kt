package com.example.agent.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agent.data.local.entity.KnowledgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KnowledgeDao {
    @Query("SELECT * FROM knowledge_items ORDER BY isPinned DESC, timestamp DESC")
    fun getAllKnowledge(): Flow<List<KnowledgeEntity>>

    @Query("SELECT * FROM knowledge_items WHERE category = :category ORDER BY isPinned DESC, timestamp DESC")
    fun getKnowledgeByCategory(category: String): Flow<List<KnowledgeEntity>>

    @Query("SELECT * FROM knowledge_items WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchKnowledge(query: String): Flow<List<KnowledgeEntity>>

    @Query("SELECT * FROM knowledge_items WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' LIMIT :limit")
    suspend fun findRelevantKnowledge(query: String, limit: Int = 3): List<KnowledgeEntity>

    @Query("SELECT * FROM knowledge_items WHERE id = :id")
    suspend fun getKnowledgeById(id: Long): KnowledgeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKnowledge(item: KnowledgeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<KnowledgeEntity>)

    @Update
    suspend fun updateKnowledge(item: KnowledgeEntity)

    @Delete
    suspend fun deleteKnowledge(item: KnowledgeEntity)

    @Query("DELETE FROM knowledge_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM knowledge_items")
    suspend fun clearAllKnowledge()

    @Query("SELECT COUNT(*) FROM knowledge_items")
    suspend fun getKnowledgeCount(): Int
}
