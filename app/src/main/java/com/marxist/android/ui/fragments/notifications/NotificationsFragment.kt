package com.marxist.android.ui.fragments.notifications

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.R
import com.marxist.android.databinding.FragmentsListBinding
import com.marxist.android.model.LocalNotifications
import com.marxist.android.ui.base.ItemClickListener
import com.marxist.android.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.Disposable

@AndroidEntryPoint
class NotificationsFragment : Fragment(R.layout.fragments_list), ItemClickListener {
    private val binding by viewBinding(FragmentsListBinding::bind)

//    private val gitHubService: GitHubService by inject()

    private var disposable: Disposable? = null

    override fun feedItemClickListener(article: Any, adapterPosition: Int, view: View) {
        if (article is LocalNotifications) {
            notificationsAdapter.notifyItemRemoved(adapterPosition)
            if (notificationsAdapter.itemCount == 0) {
                binding.rvListView.visibility = View.GONE
                binding.noFeed.emptyView.visibility = View.VISIBLE
                showImage()
            }
        }
    }

    private fun showImage() {
        binding.noFeed.txtHelper.visibility = View.GONE
        binding.noFeed.lavEmptyImage.scale = 0.6f
        binding.noFeed.lavEmptyImage.setAnimation(R.raw.notification)
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
//        disposable = gitHubService.getNotifications()
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .retryWhen(RetryWithDelay())
//            .subscribe({
//                if (it != null) {
//                    if (it.notifications.isNotEmpty()) {
//                        binding.rvListView.visibility = View.VISIBLE
//                        binding.noFeed.emptyView.visibility = View.GONE
//                        notificationsAdapter.updateNotifications(it.notifications);
//                    } else {
//                        binding.rvListView.visibility = View.GONE
//                        binding.noFeed.emptyView.visibility = View.VISIBLE
//                        binding.noFeed.txtTitle.text = getString(R.string.no_notifications_to_show)
//                        showImage()
//                    }
//                }
//            }, {
//                it.printStackTrace()
//                binding.rvListView.visibility = View.GONE
//                binding.noFeed.emptyView.visibility = View.VISIBLE
//                showImage()
//            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvListView.setHasFixedSize(true)
        binding.rvListView.layoutManager =
            LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.rvListView.adapter = notificationsAdapter
        initData()
    }
}