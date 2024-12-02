package com.report.kurs.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultGameDao {
    @Query("select * from result_game order by result_game.date desc")
    fun GetAllResults(): Flow<List<ResultGameModel>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun AddResult(result: ResultGameModel)

    @Delete
    fun DelereResult( result: ResultGameModel)
}