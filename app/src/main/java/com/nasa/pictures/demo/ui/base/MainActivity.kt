package com.nasa.pictures.demo.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import com.nasa.pictures.demo.databinding.ActivityMainBinding
import com.nasa.pictures.demo.di.AppDataBindingComponent
import com.nasa.pictures.demo.ui.grid.MainFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var appDataBindingComponent: AppDataBindingComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //To use our custom binding adapters.
        //Ideally we should set this from Application class, but Hilt doesn't support field injection
        //in Application class if Hilt is used in instrumentation and unit testing.
        //Ref: https://github.com/google/dagger/issues/2033
        DataBindingUtil.setDefaultComponent(appDataBindingComponent)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(
                    binding.fragmentContainer.id,
                    MainFragment(),
                    MainFragment::class.java.simpleName
                )
            }
        }
    }
}