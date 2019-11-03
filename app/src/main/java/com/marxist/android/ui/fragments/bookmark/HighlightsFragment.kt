package com.marxist.android.ui.fragments.bookmark

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.marxist.android.R
import com.marxist.android.database.entities.LocalHighlights
import com.marxist.android.ui.base.ItemClickListener
import com.marxist.android.viewmodel.HighlightViewModel
import kotlinx.android.synthetic.main.fragment_bookmarks.*
import kotlinx.android.synthetic.main.fragment_bookmarks.view.*
import kotlinx.android.synthetic.main.layout_lottie_no_feed.*

class HighlightsFragment : Fragment(), ItemClickListener {
    override fun feedItemClickListener(article: Any, adapterPosition: Int, view: View) {
        if (article is LocalHighlights) {
            highLightViewModel.deleteHighlight(article)
            highlightsAdapter.notifyItemRemoved(adapterPosition)
            if(highlightsAdapter.itemCount == 0) {
                rvHighLights.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            }
        }
    }

    private lateinit var mContext: Context
    private lateinit var highLightViewModel: HighlightViewModel
    private lateinit var highlightsAdapter: HighlightsAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        highlightsAdapter = HighlightsAdapter(mContext, mutableListOf(), this@HighlightsFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
    }

    private fun initData() {
        highLightViewModel = ViewModelProviders.of(this).get(HighlightViewModel::class.java)
        highLightViewModel.getHighlights().observeOnce(this, Observer {
            if (it != null) {
                if (it.isNotEmpty()) {
                    rvHighLights.visibility = View.VISIBLE
                    emptyView.visibility = View.GONE
                    highlightsAdapter.addHighlights(it)
                } else {
                    rvHighLights.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
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
        view.rvHighLights.adapter = highlightsAdapter
        return view
    }
}