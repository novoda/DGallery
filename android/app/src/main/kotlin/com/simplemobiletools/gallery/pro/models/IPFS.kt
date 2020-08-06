package com.simplemobiletools.gallery.pro.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "ipfs", indices = [Index(value = ["full_path"], unique = true)])
data class IPFS(
        @PrimaryKey(autoGenerate = true) var id: Int?,
        @ColumnInfo(name = "full_path") var fullPath: String,
        @ColumnInfo(name = "ipfs") var filename: String)
