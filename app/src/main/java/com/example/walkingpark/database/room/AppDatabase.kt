package com.example.walkingpark.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import kotlinx.coroutines.delay

@Database(entities = [ParkDB::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun parkDao(): ParkDao


    companion object {
        lateinit var appDatabase: AppDatabase
    }
}