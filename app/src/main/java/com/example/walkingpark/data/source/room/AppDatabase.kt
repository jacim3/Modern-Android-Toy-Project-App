package com.example.walkingpark.data.source.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ParkDB::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun parkDao(): ParkDao
}