package com.marxist.android.ui.fragments.notifications

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
import com.marxist.android.database.entities.LocalNotifications
import com.marxist.android.ui.base.ItemClickListener
import com.marxist.android.ui.fragments.bookmark.NotificationsAdapter
import com.marxist.android.viewmodel.NotificationViewModel
import kotlinx.android.synthetic.main.fragments_list.*
import kotlinx.android.synthetic.main.fragments_list.view.*
import kotlinx.android.synthetic.main.layout_lottie_no_feed.*

class NotificationsFragment : Fragment(), ItemClickListener {
    override fun feedItemClickListener(article: Any, adapterPosition: Int, view: View) {
        if (article is LocalNotifications) {
            notificationViewModel.deleteNotification(article)
            notificationsAdapter.notifyItemRemoved(adapterPosition)
            if (notificationsAdapter.itemCount == 0) {
                rvListView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
                showImage()
            }
        }
    }

    private fun showImage() {
        txtHelper.visibility = View.GONE
        lavEmptyImage.scale = 0.6f
        lavEmptyImage.setAnimation(R.raw.search_empty)
    }

    private lateinit var mContext: Context
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var notificationsAdapter: NotificationsAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        notificationsAdapter =
            NotificationsAdapter(mContext, mutableListOf(), this@NotificationsFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
    }

    private fun initData() {
        notificationViewModel = ViewModelProviders.of(this).get(NotificationViewModel::class.java)
        notificationViewModel.getNotifications().observeOnce(this, Observer {
            if (it != null) {
                if (it.isNotEmpty()) {
                    rvListView.visibility = View.VISIBLE
                    emptyView.visibility = View.GONE
                    notificationsAdapter.addNotifications(it)
                } else {
                    rvListView.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                    showImage()
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
        val view = inflater.inflate(R.layout.fragments_list, container, false)
        view.rvListView.setHasFixedSize(true)
        view.rvListView.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        view.rvListView.adapter = notificationsAdapter
        return view
    }
}