package com.report.kurs.database

import androidx.room.Database
import androidx.room.RoomDatabase

object DatabaseName{
    fun Get(): String = "mineSweeperHistory"
}

@Database(entities = [ResultGameModel::class], version = 1)
abstract class Database: RoomDatabase() {
    abstract fun GetResultGameDao(): ResultGameDao
}