package com.simplemobiletools.gallery.pro.ipfs

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class UploadIPFS(appContext: Context, workerParams: WorkerParameters):
        Worker(appContext, workerParams) {
    override fun doWork(): Result {
        uploadImages()
        return Result.success()
    }

    private fun uploadImages() {
        TODO("Not yet implemented")
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
