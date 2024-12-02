package com.report.kurs.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("result_game")
data class ResultGameModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "date")
    val date: String = "",
    @ColumnInfo(name = "result")
    val result: String = "",
    @ColumnInfo(name = "sizeOfArena")
    val sizeOfArena: Int = 0,
    @ColumnInfo(name = "countOfMines")
    val countOfMines: Int = 0
)
