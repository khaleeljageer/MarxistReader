package com.marxist.android.ui.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.marxist.android.ui.fragments.books.EBooksFragment
import com.marxist.android.ui.fragments.feeds.FeedsFragment
import com.marxist.android.ui.fragments.quotes.QuotesFragment
import com.marxist.android.ui.fragments.settings.SettingsFragment

/**
 * Created by Khaleel Jageer on 15/01/22.
 */
class FragmentsAdapter(
    fragmentActivity: FragmentActivity,
    private val tabList: List<Int>
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = tabList.size
    private val pageIds = tabList.map { it.hashCode().toLong() }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                FeedsFragment.newInstance()
            }
            1 -> {
                EBooksFragment.newInstance()
            }
            2 -> {
                QuotesFragment.newInstance()
            }
            3 -> {
                SettingsFragment.newInstance()
            }
            else -> {
                FeedsFragment.newInstance()
            }
        }
    }

    override fun getItemId(position: Int): Long = tabList[position].hashCode().toLong()

    override fun containsItem(itemId: Long): Boolean {
        return pageIds.contains(itemId)
    }


    /*
    private val feedsFragment =
    private val booksFragment = EBooksFragment()
    private val quoteFragment = QuotesFragment()
    private val settingsFragment = SettingsFragment()*/
}