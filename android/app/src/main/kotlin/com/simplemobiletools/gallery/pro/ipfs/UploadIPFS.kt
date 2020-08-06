package com.simplemobiletools.gallery.pro.ipfs

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.simplemobiletools.gallery.pro.R
import com.simplemobiletools.gallery.pro.databases.GalleryDatabase
import com.simplemobiletools.gallery.pro.extensions.cachedIPFS
import com.simplemobiletools.gallery.pro.models.IPFS
import com.simplemobiletools.gallery.pro.models.Medium
import io.ipfs.kotlin.defaults.InfuraIPFS
import java.io.File

private const val UPLOADING_NOTIFICATION_ID = 100
private const val UPLOADED_NOTIFICATION_ID = 101

class UploadIPFS(
        val appContext: Context,
        workerParams: WorkerParameters) : Worker(appContext, workerParams) {


    override fun doWork(): Result {
        uploadImages(appContext)
        return Result.success()
    }

    private fun uploadImages(context: Context) {
        val nonIPFSMedia = GalleryDatabase.getInstance(appContext).MediumDao().getNonIPFSMedia()

        val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(appContext)
        val uploadingNotification = configureNotifications(notificationManager, nonIPFSMedia)

        nonIPFSMedia.forEachIndexed { index, it ->
            val ipfsList: MutableList<Pair<String, String>> = mutableListOf()
            showUpdating(notificationManager, uploadingNotification, nonIPFSMedia, index)

            kotlin.runCatching {
                Log.i("IPFS", "Will push to IPFS: " + it.path)
                val f = File(it.path);
                val hn = InfuraIPFS().add.file(f)

                Log.i("IPFS", "Finished: " + it.path + " WITH CID: " + hn.Hash)

                it.ipfs = hn.Hash;
                ipfsList.add(it.path to hn.Hash)
                GalleryDatabase.getInstance(context).IPFSDao().insert(IPFS(null, it.path, hn.Hash))
                Log.i("IPFS", "Inserted into DB: " + it + " WITH CID: " + it.ipfs)
            }.onFailure { Log.e("IPFS", it.message) }
            cachedIPFS = ipfsList
        };
        showUpdated(notificationManager)
    }

    private fun configureNotifications(notificationManager: NotificationManagerCompat, nonIPFSMedia: List<Medium>): NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel("ipfs_notification") == null) {
                notificationManager.createNotificationChannel(
                        NotificationChannel(
                                "ipfs_notification",
                                "IPFS notifications",
                                NotificationManager.IMPORTANCE_LOW
                        )
                )
            }
        }
        return NotificationCompat.Builder(appContext, "ipfs_notification")
                .setContentTitle("IPFS Backup")
                .setContentText("Securing your gallery on IPFS")
                .setSmallIcon(R.drawable.ipfs)
                .setProgress(nonIPFSMedia.size, 0, false)
                .setPriority(0)
    }

    private fun showUpdating(notificationManager: NotificationManagerCompat, uploadingNotification: NotificationCompat.Builder, nonIPFSMedia: List<Medium>, index: Int) {
        notificationManager.notify(
                UPLOADING_NOTIFICATION_ID,
                uploadingNotification
                        .setProgress(nonIPFSMedia.size, index, false)
                        .build()
        )
    }

    private fun showUpdated(notificationManager: NotificationManagerCompat) {
        notificationManager.cancel(UPLOADING_NOTIFICATION_ID)
        notificationManager.notify(
                UPLOADED_NOTIFICATION_ID,
                NotificationCompat.Builder(appContext, "ipfs_notification")
                        .setContentTitle("IPFS Backup")
                        .setContentText("Your gallery is now secured on IPFS")
                        .setSmallIcon(R.drawable.ipfs)
                        .build()
        )
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

GalleryDatabase.getInstance(context).IPFSDao().insert(IPFS(null, it.path, hn.Hash))
Log.i("IPFS", "Inserted into DB: " + it + " WITH CID: " + it.ipfs)
};
}
}**/

