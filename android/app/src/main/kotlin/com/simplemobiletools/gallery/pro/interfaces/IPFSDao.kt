package com.simplemobiletools.gallery.pro.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.simplemobiletools.gallery.pro.models.IPFS

@Dao
interface IPFSDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(Ipfs: IPFS)

    @Query("SELECT ipfs FROM ipfs WHERE full_path = :path COLLATE NOCASE")
    fun getIPFS(path: String): String
}
