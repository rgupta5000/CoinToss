package com.zybooks.cointoss

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var coinImageView: ImageView
    private lateinit var coinTossButton: Button
    private lateinit var aboutButton: Button
    private lateinit var flipSound: MediaPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the ImageView and Button
        coinImageView = findViewById(R.id.coin_image_view)
        coinTossButton = findViewById(R.id.coin_toss_button)
        aboutButton = findViewById(R.id.about_button)

        val rotateAnim = ObjectAnimator.ofFloat(coinImageView, "rotationY", 0f, 360f)
        rotateAnim.duration = 400

        flipSound = MediaPlayer.create(this, R.raw.coinflipsound)

        // Set the onClickListener for the coin toss button
        coinTossButton.setOnClickListener {
            flipSound.start()
            val flipAnim = AnimatorSet()
            flipAnim.playSequentially(
                ObjectAnimator.ofFloat(coinImageView, "rotationX", 0f, 90f),
                ObjectAnimator.ofFloat(coinImageView, "rotationX", 90f, 0f),
                ObjectAnimator.ofFloat(coinImageView, "rotationY", 0f, 360f)
            )
            flipAnim.duration = 400
            flipAnim.start()
            tossCoin()
        }

        aboutButton.setOnClickListener {
            val fragment = AboutFragment()
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.about_fragment_container, fragment)
                addToBackStack(null)
                commit()
            }

            Handler().postDelayed({
                supportFragmentManager.popBackStack()
            }, 3000) // 3000 milliseconds = 3 seconds
        }

        // Register the ImageView for the context menu
        registerForContextMenu(coinImageView)

        // Start the background music task
        startBackgroundMusic()
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)

        // Inflate the menu resource
        menuInflater.inflate(R.menu.coin_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        // Handle the selected menu item
        return when (item.itemId) {
            R.id.menu_option_flip -> {
                tossCoin()
                true
            }
            R.id.menu_option_reset -> {
                coinImageView.setImageResource(R.drawable.coin_image)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }



    private fun tossCoin() {
        // Generate a random number (either 0 or 1) to simulate a coin toss
        val randomNum = Random().nextInt(2)

        // Determine which coin image to display based on the random number
        if (randomNum == 0) {
            coinImageView.setImageResource(R.drawable.coin_heads)
        } else {
            coinImageView.setImageResource(R.drawable.coin_tails)
        }

        // Delay the result and show the dialog after one second
        Handler().postDelayed({
            val result = if (randomNum == 0) "Heads" else "Tails"
            showResultDialog(result)
        }, 1000) // 1000 milliseconds = 1 second

        startBackgroundMusic()
    }

    private fun showResultDialog(result: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("You got $result!")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ -> dialog.cancel() }
        val alert = builder.create()
        alert.window?.setGravity(Gravity.BOTTOM)
        alert.show()
    }

    private fun startBackgroundMusic() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<BackgroundMusicWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(workRequest)
    }
}