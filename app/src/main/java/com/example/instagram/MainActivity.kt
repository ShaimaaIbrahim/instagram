package com.example.instagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.instagram.databinding.ActivityMainBinding
import com.example.instagram.ui.activities.AddPostActivity
import com.example.instagram.ui.fragments.HomeFragment
import com.example.instagram.ui.fragments.NotificationsFragment
import com.example.instagram.ui.fragments.ProfileFragment
import com.example.instagram.ui.fragments.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    internal   var selectedFragment : Fragment? = null
   private lateinit var binding : ActivityMainBinding

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                selectedFragment = HomeFragment()
                moveToFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_search -> {
                selectedFragment = SearchFragment()
                moveToFragment(SearchFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_add -> {
                item.isChecked = false
                startActivity(Intent(this@MainActivity, AddPostActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_favorite -> {
                selectedFragment = NotificationsFragment()
                moveToFragment(NotificationsFragment())
                return@OnNavigationItemSelectedListener true
            }

            R.id.nav_profile -> {
                selectedFragment = ProfileFragment()
                moveToFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


      binding = DataBindingUtil.setContentView(this , R.layout.activity_main)

        val navView: BottomNavigationView = binding.navView

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)


      moveToFragment(HomeFragment())
    }


    private fun moveToFragment(fragment: Fragment)
    {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container, fragment)
        fragmentTrans.commit()
    }
}