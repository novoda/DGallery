package com.simplemobiletools.gallery.pro.ipfs

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.simplemobiletools.gallery.pro.databases.GalleryDatabase
import com.simplemobiletools.gallery.pro.interfaces.MediumDao_Impl
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
            MediumDao_Impl(GalleryDatabase.getInstance(appContext)).updateMedium(it)
        };
    }
}


/**
private fun handleSendMultipleImages(intent: Intent) {
val cont = this;
intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let {
it.forEach {
Log.i("TEST", it.toString())


GlobalScope.launch {
sendToIPFS(cont, Uri.parse(it.toString()))
}
}
}
}

suspend fun sendToIPFS(context: Context, uri: Uri) {
withContext(Dispatchers.IO) {
this.launch {
val outputFile: File = File.createTempFile("_ipfs_", "temp", context.cacheDir)
context.contentResolver.openInputStream(uri)?.copyTo(
out = outputFile.outputStream(),
bufferSize = 1024
)
val nh = InfuraIPFS().add.file(outputFile)
Log.i("TEST", nh.Name + " " + nh.Hash)
}
}
}
}
 **/
