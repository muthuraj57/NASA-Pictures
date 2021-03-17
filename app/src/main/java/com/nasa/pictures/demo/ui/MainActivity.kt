package com.nasa.pictures.demo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.nasa.pictures.demo.databinding.ActivityMainBinding
import com.nasa.pictures.demo.ui.grid.GridFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(
                    binding.fragmentContainer.id,
                    GridFragment(),
                    GridFragment::class.java.simpleName
                )
            }
        }
    }
}