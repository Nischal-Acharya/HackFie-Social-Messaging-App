package com.hackfie.hackfieofficial.model

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hackfie.hackfieofficial.R

class AboutDevelopers : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_developers)

        // Enable edge-to-edge display if you have implemented this functionality
        // enableEdgeToEdge()

        // Setup Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Apply window insets to adjust padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup social icons
        setupSocialIcons()
    }

    private fun setupSocialIcons() {
        // Nishchal Acharya's social icons
        findViewById<ImageView>(R.id.nishchal_facebook).setOnClickListener {
            openBrowser("https://nishchalacharya.com.np/")
        }
        findViewById<ImageView>(R.id.nishchal_twitter).setOnClickListener {
            openBrowser("https://x.com/nishchal_acc")
        }
        findViewById<ImageView>(R.id.nishchal_linkedin).setOnClickListener {
            openBrowser("https://www.linkedin.com/in/nishchalacharya/")
        }
        findViewById<ImageView>(R.id.nishchal_github).setOnClickListener {
            openBrowser("https://github.com/Nischal-Acharya")
        }

        // Sahil Acharya's social icons
        findViewById<ImageView>(R.id.sahil_facebook).setOnClickListener {
            openBrowser("https://sahilacharya.com.np/")
        }
        findViewById<ImageView>(R.id.sahil_twitter).setOnClickListener {
            openBrowser("https://x.com/SahilAcr")
        }
        findViewById<ImageView>(R.id.sahil_linkedin).setOnClickListener {
            openBrowser("https://www.linkedin.com/in/sahilacharya/")
        }
        findViewById<ImageView>(R.id.sahil_github).setOnClickListener {
            openBrowser("https://github.com/AcrSahil")
        }

        // Anish Niraula's social icons
        findViewById<ImageView>(R.id.anish_facebook).setOnClickListener {
            openBrowser("https://niraulaanish.com.np/")
        }
        findViewById<ImageView>(R.id.anish_twitter).setOnClickListener {
            openBrowser("https://x.com/Anish01hck")
        }
        findViewById<ImageView>(R.id.anish_linkedin).setOnClickListener {
//            openBrowser("https://linkedin.com/in/anish")
        }
        findViewById<ImageView>(R.id.anish_github).setOnClickListener {
//            openBrowser("https://github.com/anish")
        }
    }

    private fun openBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
