package com.marxist.android.ui.fragments.feeds

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.R
import com.marxist.android.database.AppDatabase
import com.marxist.android.database.entities.LocalFeeds
import com.marxist.android.ui.activities.DetailsActivity
import com.marxist.android.ui.base.FeedItemClickListener
import kotlinx.android.synthetic.main.fragment_feeds.*

class FeedsFragment : Fragment(), FeedItemClickListener {
    private lateinit var appDatabase: AppDatabase
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        appDatabase = AppDatabase.getAppDatabase(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feeds, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (!appDatabase.isOpen) {
            return
        }
        val feedList = appDatabase.localFeedsDao().getAllFeeds()
        rvListView.setHasFixedSize(true)
        rvListView.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)

        rvListView.adapter = FeedListAdapter(mContext, feedList.toMutableList(), this@FeedsFragment)
    }

    override fun feedItemClickListener(
        article: LocalFeeds,
        adapterPosition: Int,
        view: View
    ) {
        val intent = Intent(mContext, DetailsActivity::class.java)
        intent.putExtra(DetailsActivity.ARTICLE, article)
        startActivity(intent)
    }
}