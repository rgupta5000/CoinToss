package com.zybooks.cointoss

import android.content.Context
import android.media.MediaPlayer
import androidx.work.Worker
import androidx.work.WorkerParameters

class BackgroundMusicWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        // Add your background task here, such as playing background music
        val mediaPlayer = MediaPlayer.create(applicationContext, R.raw.backgroundsound)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        return Result.success()
    }
}