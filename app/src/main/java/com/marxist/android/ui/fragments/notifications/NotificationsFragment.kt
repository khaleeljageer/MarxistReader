package com.marxist.android.ui.fragments.notifications

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.R
import com.marxist.android.databinding.FragmentsListBinding
import com.marxist.android.model.LocalNotifications
import com.marxist.android.ui.base.ItemClickListener
import com.marxist.android.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment(R.layout.fragments_list), ItemClickListener {
    private val binding by viewBinding(FragmentsListBinding::bind)
    private val viewModel: NotificationViewModel by viewModels()

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

    private val notificationsAdapter: NotificationsAdapter by lazy {
        NotificationsAdapter(requireContext(), mutableListOf(), this)
    }

    private fun initData() {
        viewModel.getNotification()
        viewModel.notifications.observe(viewLifecycleOwner, {
            binding.rvListView.visibility = View.VISIBLE
            binding.noFeed.emptyView.visibility = View.GONE
            binding.progressLoader.visibility = View.GONE
            notificationsAdapter.updateNotifications(it)
        })
        viewModel.loading.observe(viewLifecycleOwner, {

            binding.rvListView.visibility = View.GONE
            binding.noFeed.emptyView.visibility = View.GONE
            binding.progressLoader.visibility = View.VISIBLE
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.rvListView) {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = notificationsAdapter
        }
        initData()
    }
}