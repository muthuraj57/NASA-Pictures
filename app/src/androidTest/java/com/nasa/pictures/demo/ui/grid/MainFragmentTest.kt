package com.nasa.pictures.demo.ui.grid

import android.view.View
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.Visibility.INVISIBLE
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.viewpager2.widget.ViewPager2
import com.google.common.truth.Truth.assertThat
import com.nasa.pictures.demo.R
import com.nasa.pictures.demo.repository.DataRepository
import com.nasa.pictures.demo.ui.base.MainActivity
import com.nasa.pictures.demo.ui.grid.common.GridItemViewHolder
import com.nasa.pictures.demo.ui.grid.common.HorizontalListItemViewHolder
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

/**
 * Created by Muthuraj on 20/03/21.
 */
@HiltAndroidTest
class MainFragmentTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var rule = ActivityScenarioRule(MainActivity::class.java)
    private lateinit var fragment: MainFragment

    @Inject
    lateinit var dataRepository: DataRepository

    @Before
    fun setup() {
        hiltRule.inject()
        rule.scenario.moveToState(Lifecycle.State.RESUMED)
        rule.scenario.onActivity {
            fragment =
                it.supportFragmentManager
                    .findFragmentByTag(MainFragment::class.java.simpleName) as MainFragment
        }
    }

    @Test
    fun whenFragmentIsLoaded_detailViewIsNotVisible() {
        onView(withId(R.id.detailView))
            .check(matches(withEffectiveVisibility(INVISIBLE)))
    }

    @Test
    fun whenFragmentIsLoaded_gridRecyclerViewIsVisible() {
        onView(withId(R.id.gridRecyclerView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun whenGridItemIsClicked_detailViewIsVisible() {
        onView(withId(R.id.gridRecyclerView))
            .perform(actionOnItemAtPosition<GridItemViewHolder>(0, click()))
        onView(withId(R.id.detailView)).check(matches(isDisplayed()))
    }

    @Test
    fun whenGridItemIsClicked_indicatorViewIsVisible() {
        onView(withId(R.id.gridRecyclerView))
            .perform(actionOnItemAtPosition<GridItemViewHolder>(0, click()))
        onView(withId(R.id.indicatorView)).check(matches(isDisplayed()))
    }

    @Test
    fun wheGridItemIsClicked_itsDetailViewIsShown() {
        //Click second grid item to open it's detail view.
        onView(withId(R.id.gridRecyclerView))
            .perform(actionOnItemAtPosition<GridItemViewHolder>(5, click()))

        val viewPager = findViewById<ViewPager2>(R.id.detailViewPager)
        assertThat(viewPager.currentItem).isEqualTo(5)
    }

    @Test
    fun wheIndicatorItemIsClicked_itsDetailViewIsShown() {
        //First click an item in the grid view to open detail view, which will show indicator page.
        onView(withId(R.id.gridRecyclerView))
            .perform(actionOnItemAtPosition<GridItemViewHolder>(0, click()))

        //Then click adjacent indicator item.
        onView(withId(R.id.indicatorView))
            .perform(actionOnItemAtPosition<HorizontalListItemViewHolder>(10, click()))

        val viewPager = findViewById<ViewPager2>(R.id.detailViewPager)
        assertThat(viewPager.currentItem).isEqualTo(10)
    }

    @Test
    fun whenDetailViewOpened_dataShownAreValid() {

        //Click second grid item to open it's detail view.
        onView(withId(R.id.gridRecyclerView))
            .perform(actionOnItemAtPosition<GridItemViewHolder>(4, click()))

        val recyclerView =
            findViewById<ViewPager2>(R.id.detailViewPager).getChildAt(0) as RecyclerView

        //We set offscreen limit as 1. So when detail page is opened, it will be the second child of
        //RecyclerView.
        val detailViewItem = recyclerView.getChildAt(1)
        val dataset = runBlocking { dataRepository.getDataSortedByLatest() }

        val openedData = dataset[4]
        assertThat(detailViewItem.findViewById<TextView>(R.id.title).text).isEqualTo(openedData.title)
        assertThat(detailViewItem.findViewById<TextView>(R.id.explanation).text).isEqualTo(
            openedData.explanation
        )
        assertThat(detailViewItem.findViewById<TextView>(R.id.copyrightName).text).isEqualTo(
            openedData.copyright
        )
    }

    @Test
    fun whenBackPressedAfterDetailIsOpened_detailViewIsClosed() {
        //Click on an item to open detail view first.
        onView(withId(R.id.gridRecyclerView))
            .perform(actionOnItemAtPosition<GridItemViewHolder>(0, click()))
        onView(withId(R.id.detailView)).check(matches(isDisplayed()))

        //pressBack from Espresso doesn't work. So use the one from UI automator.
        //Ref: https://stackoverflow.com/a/42872727/3423932
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            .pressBack()

        onView(withId(R.id.detailView)).check(matches(withEffectiveVisibility(INVISIBLE)))
    }


    private fun <T : View> findViewById(id: Int) = fragment.requireView().findViewById<T>(id)
}