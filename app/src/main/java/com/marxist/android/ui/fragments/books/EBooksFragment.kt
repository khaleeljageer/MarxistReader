package com.marxist.android.ui.fragments.books

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.marxist.android.R
import com.marxist.android.database.AppDatabase
import com.marxist.android.databinding.FragmentsBooksBinding
import com.marxist.android.utils.GridItemSpace
import com.marxist.android.utils.toPixel
import com.marxist.android.utils.viewBinding
import com.marxist.android.viewmodel.BookListViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EBooksFragment : Fragment(R.layout.fragments_books) {

    private val bookListViewModel: BookListViewModel by viewModels()

    @Inject
    lateinit var appDatabase: AppDatabase

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
                    binding.rvListView.visibility = View.VISIBLE
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
        binding.rvListView.visibility = View.VISIBLE
        binding.noBooks.emptyView.visibility = View.GONE
        binding.noBooks.lavEmptyImage.clearAnimation()
    }

    private fun showEmptyView() {
        binding.rvListView.visibility = View.GONE
        binding.noBooks.emptyView.visibility = View.VISIBLE
        binding.noBooks.lavEmptyImage.setAnimation(R.raw.loading_books)
    }

    private val binding by viewBinding(FragmentsBooksBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.rvListView) {
            setHasFixedSize(true)
            adapter = bookAdapter
            addItemDecoration(GridItemSpace(mContext, 5.toPixel(context)))
        }

        initData()
    }
}