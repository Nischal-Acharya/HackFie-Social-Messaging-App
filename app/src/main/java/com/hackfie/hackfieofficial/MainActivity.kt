package com.hackfie.hackfieofficial

import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hackfie.hackfieofficial.databinding.ActivityMainBinding
import com.hackfie.hackfieofficial.fragments.DevProfileFragment
import com.hackfie.hackfieofficial.fragments.SearchFragment
import com.hackfie.hackfieofficial.fragments.HackFieFragment
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding



    class MyApp : Application() {
        override fun onCreate() {
            super.onCreate()
            FirebaseApp.initializeApp(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
//        enableEdgeToEdge()


        setContentView(binding.root)


//
//        val profileUpdateFragment  = ProfileUpdateHackFie()
//        val fragmentManager: FragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.main_frame_layout, profileUpdateFragment).commit()




        replaceFragments(HackFieFragment())

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.hackfieBottomNav -> replaceFragments(HackFieFragment())
                R.id.developersBottomNav -> replaceFragments(SearchFragment())
//                R.id.uploadBottomNav -> replaceFragments(UploadFragment())
//                R.id.geniusBottomNav -> replaceFragments(GeniusBotFragment())
                R.id.devProfileBottomNav -> replaceFragments(DevProfileFragment())
                else -> {}
            }

            // Disable the selected item
            disableSelectedNavItem(binding.bottomNavigationView, item.itemId)

            true
        }



//        var bottomBar: SmoothBottomBar = findViewById(R.id.bottomBar)





    }

    private fun replaceFragments(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTranslation = fragmentManager.beginTransaction()
        fragmentTranslation.replace(R.id.main_frame_layout, fragment)
        fragmentTranslation.commitAllowingStateLoss()
    }

    private fun disableSelectedNavItem(navigationView: BottomNavigationView, selectedItemId: Int) {
        for (i in 0 until navigationView.menu.size()) {
            val menuItem = navigationView.menu.getItem(i)
            menuItem.isEnabled = menuItem.itemId != selectedItemId
        }
    }

}