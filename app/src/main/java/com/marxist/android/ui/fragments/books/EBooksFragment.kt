package com.marxist.android.ui.fragments.books

import android.content.Context
import android.os.Bundle
import android.util.LongSparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.marxist.android.R
import com.marxist.android.database.entities.LocalBooks
import com.marxist.android.ui.base.BookClickListener
import com.marxist.android.utils.AppConstants
import com.marxist.android.utils.download.DownloadUtil
import com.marxist.android.viewmodel.BookListViewModel
import kotlinx.android.synthetic.main.fragments_list.*
import kotlinx.android.synthetic.main.fragments_list.view.*
import kotlinx.android.synthetic.main.layout_lottie_no_feed.*

class EBooksFragment : Fragment(), BookClickListener {
    private var downloadsPositions = LongSparseArray<Long>()
    override fun bookItemClickListener(adapterPosition: Int, book: LocalBooks) {
        if (book.isDownloaded) {
            DownloadUtil.openSavedBook(mContext, book)
        } else {
            if (book.downloadId == -1L) {
                val downloadID = DownloadUtil.queueForDownload(
                    mContext, book.title, book.epub,
                    book.pubDate, AppConstants.BOOKS, book.bookid
                )
                if (downloadID != 0L) {
                    bookAdapter.updateDownloadId(adapterPosition, downloadID)
                    downloadsPositions.put(downloadID, adapterPosition.toLong())
                }
            }
        }
    }

    private lateinit var bookListViewModel: BookListViewModel
    private lateinit var bookAdapter: BookListAdapter
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        bookAdapter = BookListAdapter(mContext, mutableListOf(), this@EBooksFragment)
    }

    private fun initData() {
        bookListViewModel = ViewModelProviders.of(this).get(BookListViewModel::class.java)
        if (bookListViewModel.getLocalBooksSize() == 0) {
            rvListView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            lavEmptyImage.setAnimation(R.raw.loading_books)
        }
        bookListViewModel.callBookApi()
        bookListViewModel.getLocalBooks().observe(this, Observer {
            if (it != null) {
                if (it.isNotEmpty()) {
                    rvListView.visibility = View.VISIBLE
                    emptyView.visibility = View.GONE
                    bookAdapter.setItems(it)
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragments_list, container, false)
        view.rvListView.setHasFixedSize(true)
        view.rvListView.layoutManager = GridLayoutManager(mContext, 2)
        view.rvListView.adapter = bookAdapter
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initData()
    }
}