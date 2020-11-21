package com.marxist.android.ui.fragments.notifications

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.R
import com.marxist.android.model.LocalNotifications
import com.marxist.android.ui.base.ItemClickListener
import com.marxist.android.utils.api.ApiClient
import com.marxist.android.utils.api.GitHubService
import com.marxist.android.utils.api.RetryWithDelay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragments_list.*
import kotlinx.android.synthetic.main.fragments_list.view.*
import kotlinx.android.synthetic.main.layout_lottie_no_feed.*
import org.koin.android.ext.android.inject

class NotificationsFragment : Fragment(), ItemClickListener {

    private val gitHubService: GitHubService by inject()

    private var disposable: Disposable? = null

    override fun feedItemClickListener(article: Any, adapterPosition: Int, view: View) {
        if (article is LocalNotifications) {
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
        lavEmptyImage.setAnimation(R.raw.notification)
    }

    private lateinit var mContext: Context
    private lateinit var notificationsAdapter: NotificationsAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        notificationsAdapter =
            NotificationsAdapter(mContext, mutableListOf(), this@NotificationsFragment)
    }

    private fun initData() {
        callBookApi()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    private fun callBookApi() {
        disposable = gitHubService.getNotifications()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .retryWhen(RetryWithDelay())
            .subscribe({
                if (it != null) {
                    if (it.notifications.isNotEmpty()) {
                        rvListView.visibility = View.VISIBLE
                        emptyView.visibility = View.GONE
                        notificationsAdapter.updateNotifications(it.notifications);
                    } else {
                        rvListView.visibility = View.GONE
                        emptyView.visibility = View.VISIBLE
                        txtTitle.text = getString(R.string.no_notifications_to_show)
                        showImage()
                    }
                }
            }, {
                it.printStackTrace()
                rvListView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
                showImage()
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragments_list, container, false)
        view.rvListView.setHasFixedSize(true)
        view.rvListView.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        view.rvListView.adapter = notificationsAdapter
        initData()
        return view
    }
}