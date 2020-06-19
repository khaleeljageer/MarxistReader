package com.marxist.android.ui.fragments.books

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.marxist.android.R
import com.marxist.android.database.entities.LocalBooks
import com.marxist.android.model.ShowSnackBar
import com.marxist.android.ui.base.BookClickListener
import com.marxist.android.utils.RxBus
import com.marxist.android.viewmodel.BookListViewModel
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.android.synthetic.main.fragments_list.*
import kotlinx.android.synthetic.main.fragments_list.view.*
import kotlinx.android.synthetic.main.layout_lottie_no_feed.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class EBooksFragment : Fragment(), BookClickListener {
    override fun bookItemClickListener(adapterPosition: Int, book: LocalBooks) {

    }

    private val bookListViewModel: BookListViewModel by viewModel()
    private val bookAdapter by lazy {
        BookListAdapter(mContext, mutableListOf(), this@EBooksFragment)
    }

    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initData() {
        if (bookListViewModel.getLocalBooksSize() == 0) {
            rvListView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            lavEmptyImage.setAnimation(R.raw.loading_books)
        }
        bookListViewModel.callBookApi()
        bookListViewModel.getLocalBooks().observe(viewLifecycleOwner, Observer {
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
        bookAdapter.setListView(view.rvListView)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initData()
        getPermission()
    }

    private fun getPermission() {
        val permissions = arrayOf(
            WRITE_EXTERNAL_STORAGE,
            READ_EXTERNAL_STORAGE
        )
        val rationale = "Please provide Storage permission to download books..."
        val options =
            Permissions.Options()
                .setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning")
        Permissions.check(mContext, permissions, rationale, options, object : PermissionHandler() {
            override fun onGranted() {

            }

            override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
                RxBus.publish(ShowSnackBar("You can't use download feature"))
            }
        })
    }
}