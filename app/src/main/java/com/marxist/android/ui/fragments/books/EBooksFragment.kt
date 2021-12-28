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
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EBooksFragment : Fragment(R.layout.fragments_books) {

    private val booksViewModel: BooksViewModel by viewModels()

    @Inject
    lateinit var appDatabase: AppDatabase

    private val bookAdapter by lazy {
        BookListAdapter(requireContext(), mutableListOf(), appDatabase)
    }

    private fun initData() {
        showEmptyView()
        booksViewModel.callBookApi()
        booksViewModel.booksLiveData.observe(viewLifecycleOwner, Observer {
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
            addItemDecoration(GridItemSpace(requireContext(), 5.toPixel(context)))
        }

        initData()
    }
}