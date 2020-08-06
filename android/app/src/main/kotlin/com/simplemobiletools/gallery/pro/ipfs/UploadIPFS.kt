package com.simplemobiletools.gallery.pro.ipfs

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.simplemobiletools.gallery.pro.databases.GalleryDatabase
import com.simplemobiletools.gallery.pro.interfaces.MediumDao_Impl
import com.simplemobiletools.gallery.pro.models.IPFS
import com.simplemobiletools.gallery.pro.models.Medium
import io.ipfs.kotlin.defaults.InfuraIPFS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class UploadIPFS(val appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        uploadImages(appContext)
        return Result.success()
    }

    private fun uploadImages(context: Context) {
        MediumDao_Impl(GalleryDatabase.getInstance(appContext)).getNonIPFSMedia().forEach {
            Log.i("IPFS", "Will push to IPFS: " + it.path)
            val f = File(it.path);
            val hn = InfuraIPFS().add.file(f)

            Log.i("IPFS", "Finished: " + it.path + " WITH CID: " + hn.Hash)

            it.ipfs = hn.Hash;

            GalleryDatabase.getInstance(context).IPFSDao().insert(IPFS(null, it.path, hn.Hash))
            Log.i("IPFS", "Inserted into DB: " + it + " WITH CID: " + it.ipfs)
        };
    }
}
