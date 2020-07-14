package com.marxist.android.ui.fragments.books

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.marxist.android.R
import com.marxist.android.database.AppDatabase
import com.marxist.android.utils.GridItemSpace
import com.marxist.android.utils.toPixel
import com.marxist.android.viewmodel.BookListViewModel
import kotlinx.android.synthetic.main.fragments_books.*
import kotlinx.android.synthetic.main.fragments_books.view.*
import kotlinx.android.synthetic.main.layout_lottie_no_feed.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class EBooksFragment : Fragment() {
    private val bookListViewModel: BookListViewModel by viewModel()
    private val appDatabase: AppDatabase by inject()
    private val bookAdapter by lazy {
        BookListAdapter(mContext, mutableListOf(), appDatabase)
    }

    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initData() {
        showEmptyView()
        bookListViewModel.callBookApi()
        bookListViewModel.getLocalBooks().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it.isNotEmpty()) {
                    hideEmptyView()
                    rvListView.visibility = View.VISIBLE
                    bookAdapter.setItems(it)
                } else {
                    showEmptyView()
                }
            } else {
                showEmptyView()
            }
        })
    }

    private fun hideEmptyView() {
        rvListView.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
        lavEmptyImage.clearAnimation()
    }

    private fun showEmptyView() {
        rvListView.visibility = View.GONE
        emptyView.visibility = View.VISIBLE
        lavEmptyImage.setAnimation(R.raw.loading_books)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragments_books, container, false)

        with(view.rvListView) {
            addItemDecoration(GridItemSpace(mContext, 5.toPixel(context)))
            setHasFixedSize(true)
            adapter = bookAdapter
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
    }
}