package com.marxist.android.ui.fragments.bookmark

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.marxist.android.R
import com.marxist.android.database.entities.LocalHighlights
import com.marxist.android.ui.base.ItemClickListener
import com.marxist.android.viewmodel.HighlightViewModel
import kotlinx.android.synthetic.main.fragment_bookmarks.view.*

class BookmarkFragment : Fragment(), ItemClickListener {
    override fun feedItemClickListener(article: Any, adapterPosition: Int, view: View) {
        if (article is LocalHighlights) {
            highLightViewModel.deleteHighlight(article)
            bookmarkAdapter.notifyItemRemoved(adapterPosition)
        }
    }

    private lateinit var mContext: Context
    private lateinit var highLightViewModel: HighlightViewModel
    private lateinit var bookmarkAdapter: BookmarkAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        bookmarkAdapter = BookmarkAdapter(mContext, mutableListOf(), this@BookmarkFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
    }

    private fun initData() {
        highLightViewModel = ViewModelProviders.of(this).get(HighlightViewModel::class.java)
        highLightViewModel.getHighlights().observe(this, Observer {
            if (it != null) {
                bookmarkAdapter.addHighlights(it)
            }
            highLightViewModel.getHighlights().removeObservers(this)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bookmarks, container, false)
        view.rvHighLights.setHasFixedSize(true)
        view.rvHighLights.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        view.rvHighLights.adapter = bookmarkAdapter
        return view
    }


}