package com.weathersnap.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: ReportEntity): Long

    @Query("SELECT * FROM reports ORDER BY savedAtMillis DESC")
    fun observeAll(): Flow<List<ReportEntity>>

    @Query("SELECT COUNT(*) FROM reports")
    fun observeCount(): Flow<Int>

    @Query("DELETE FROM reports WHERE id = :id")
    suspend fun deleteById(id: Long)
}
