package com.example.multiplatformmaps.android.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [
        MarkDbEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract  fun getMarksDao(): MarksDao
}