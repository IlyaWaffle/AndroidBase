package com.example.multiplatformmaps.android.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MarksDao{

    @Query("SELECT fact_title, fact_text FROM marks WHERE id = :id")
    suspend fun findFactById(id: Long): MarksFactTuple?

    @Query("SELECT * FROM marks WHERE id = :markId")
    fun getById(markId: Long): Flow<MarkDbEntity?>
}