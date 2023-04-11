package com.zybooks.cointoss

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var coinImageView: ImageView
    private lateinit var coinTossButton: Button
    private lateinit var aboutButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the ImageView and Button
        coinImageView = findViewById(R.id.coin_image_view)
        coinTossButton = findViewById(R.id.coin_toss_button)
        aboutButton = findViewById(R.id.about_button)

        // Set the onClickListener for the coin toss button
        coinTossButton.setOnClickListener {
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
            showResultDialog("Heads")
        } else {
            coinImageView.setImageResource(R.drawable.coin_tails)
            showResultDialog("Tails")
        }
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
}