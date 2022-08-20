package com.example.multiplatformmaps.android.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "marks",
    indices = [
        Index("name", unique = true)
    ]
)
data class MarkDbEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val latitude: String,
    val longitude: String,
    val name: String,
    val description: String,
    @ColumnInfo(name = "fact_title") val factTitle: String,
    @ColumnInfo(name = "fact_text") val factText: String
) {
    fun toMark(): Mark = Mark(
     id = id,
     latitude = latitude ,
     longitude = longitude ,
     name =name ,
     description = description ,
     factTitle = factTitle ,
     factText = factText
    )
}