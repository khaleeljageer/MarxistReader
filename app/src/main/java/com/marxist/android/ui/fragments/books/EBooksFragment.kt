package com.marxist.android.ui.fragments.books

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.marxist.android.R
import com.marxist.android.data.model.DownloadResult
import com.marxist.android.database.entities.LocalBooks
import com.marxist.android.databinding.FragmentsBooksBinding
import com.marxist.android.utils.*
import com.marxist.android.utils.download.DownloadUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@AndroidEntryPoint
class EBooksFragment : Fragment(R.layout.fragments_books) {

    private val booksViewModel: BooksViewModel by viewModels()

    private val bookAdapter by lazy {
        BookListAdapter(mutableListOf(), ::downloadWithFlow)
    }

    private fun initData() {
        showEmptyView()
        booksViewModel.callBookApi()
        booksViewModel.booksLiveData.observe(viewLifecycleOwner, {
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

    private fun downloadWithFlow(book: LocalBooks) {
        CoroutineScope(Dispatchers.IO).launch {
            val targetPath = DeviceUtils.getRootDirPath(requireContext()).plus("/books")
            val url = Uri.parse(book.epub)
            val name = url.pathSegments.last()
            val path = File(targetPath, name)
            if (path.exists()) {
                withContext(Dispatchers.Main) {
                    DownloadUtil.openSavedBook(requireContext(), path.toString())
                }
            } else {
                downloadFile(path, book.epub).collect {
                    withContext(Dispatchers.Main) {
                        when (it) {
                            is DownloadResult.Success -> {
                                bookAdapter.setDownloading(book, false)
                                showSnackBar("File ready to open")
                            }
                            is DownloadResult.Error -> {
                                bookAdapter.setDownloading(book, false)
                                showSnackBar("Something went wrong")
                            }
                            DownloadResult.Loading -> {
                                bookAdapter.setDownloading(book, true)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    companion object {
        fun newInstance(): EBooksFragment {
            return EBooksFragment()
        }
    }
}