package com.fd.bestpracticetry_codinginflow05

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.fragment.app.Fragment
import com.fd.bestpracticetry_codinginflow05.databinding.ActivityMainBinding
import com.fd.bestpracticetry_codinginflow05.features.bookmarks.BookmarksFragment
import com.fd.bestpracticetry_codinginflow05.features.breakingnews.BreakingNewsFragment
import com.fd.bestpracticetry_codinginflow05.features.searchnews.SearchNewsFragment
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalArgumentException


private const val TAG_BREAKING_NEWS_FRAGMENT = "TAG_BREAKING_NEWS_FRAGMENT"
private const val TAG_SEARCH_NEWS_FRAGMENT = "TAG_SEARCH_NEWS_FRAGMENT"
private const val TAG_BOOKMARKS_FRAGMENT = "TAG_BOOKMARKS_FRAGMENT"
private const val KEY_SELECTED_INDEX = "KEY_SELECTED_INDEX"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var breakingNewsFragment: BreakingNewsFragment
    private lateinit var searchNewsFragment: SearchNewsFragment
    private lateinit var bookmarksFragment: BookmarksFragment


    private val fragments : Array<Fragment> get() = arrayOf(breakingNewsFragment,searchNewsFragment,bookmarksFragment)

    private var selectedIndex=0

    /**
     * what get()= mean?
     * kotlin syntax easy baby
     *
     * get()= ...   == get() {return ...}
     */
    private val selectedFragment get() = fragments[selectedIndex]



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareFragmentsAndLastSelectedFragment(savedInstanceState)
        
        selectFragment(selectedFragment)

        setupBottomNavigationClick()

    }

    /**
     * prepare Fragment and check for savedInstanceState null
     *
     * load selected index from savedInstanceState
     */
    private fun prepareFragmentsAndLastSelectedFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            breakingNewsFragment = BreakingNewsFragment()
            searchNewsFragment = SearchNewsFragment()
            bookmarksFragment = BookmarksFragment()

            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, breakingNewsFragment, TAG_BREAKING_NEWS_FRAGMENT)
                .add(R.id.fragment_container, searchNewsFragment, TAG_SEARCH_NEWS_FRAGMENT)
                .add(R.id.fragment_container, bookmarksFragment, TAG_BOOKMARKS_FRAGMENT)
                .commit()
        } else {
            breakingNewsFragment =
                supportFragmentManager.findFragmentByTag(TAG_BREAKING_NEWS_FRAGMENT) as BreakingNewsFragment
            searchNewsFragment =
                supportFragmentManager.findFragmentByTag(TAG_SEARCH_NEWS_FRAGMENT) as SearchNewsFragment
            bookmarksFragment =
                supportFragmentManager.findFragmentByTag(TAG_BOOKMARKS_FRAGMENT) as BookmarksFragment

            selectedIndex = savedInstanceState.getInt(KEY_SELECTED_INDEX, 0)

        }
    }

    /**
     * attach given fragment
     */
    private fun selectFragment(selectedFragment: Fragment) {

        var transaction = supportFragmentManager.beginTransaction()

        fragments.forEachIndexed { index, fragment ->
            if (selectedFragment == fragment ){
                transaction =transaction.attach(fragment)
                selectedIndex = index
            }else{
                transaction = transaction.detach(fragment)
            }
        }
        transaction.commit()

        title = when (selectedFragment) {
            is BreakingNewsFragment -> getString(R.string.title_breaking_news)
            is SearchNewsFragment -> getString(R.string.title_search_news)
            is BookmarksFragment -> getString(R.string.title_bookmarks)
            else -> ""
        }

    }

    /**
     * select fragment by bottom navigation selected(clicked) id
     */
    private fun setupBottomNavigationClick() {
        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_breaking -> breakingNewsFragment
                R.id.nav_search -> searchNewsFragment
                R.id.nav_bookmarks -> bookmarksFragment
                else -> throw IllegalArgumentException("Unexpected itemId")
            }

            if (selectedFragment === fragment) {
                if (fragment is OnBottomNavigationFragmentReselectedListener) {
                    fragment.onBottomNavigationFragmentReselected()
                }
            } else {
                selectFragment(fragment)
            }

            true
        }
    }


    interface OnBottomNavigationFragmentReselectedListener {
        fun onBottomNavigationFragmentReselected()
    }

    override fun onBackPressed() {
        if (selectedIndex != 0){
            binding.bottomNav.selectedItemId = R.id.nav_breaking
        }else{
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED_INDEX , selectedIndex)
    }



}