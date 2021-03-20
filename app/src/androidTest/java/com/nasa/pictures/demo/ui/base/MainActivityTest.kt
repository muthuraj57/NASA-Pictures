package com.nasa.pictures.demo.ui.base

import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.common.truth.Truth.assertThat
import com.nasa.pictures.demo.ui.grid.MainFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

/**
 * Created by Muthuraj on 20/03/21.
 */
@HiltAndroidTest
class MainActivityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @Rule(order = 1)
    @JvmField
    val rule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun whenActivityIsOpened_MainFragmentShouldBeAdded() {
        rule.scenario.moveToState(Lifecycle.State.CREATED)
        rule.scenario.onActivity {
            val fragment =
                it.supportFragmentManager.findFragmentByTag(MainFragment::class.java.simpleName)
            assertThat(fragment).isNotNull()
        }
    }
}