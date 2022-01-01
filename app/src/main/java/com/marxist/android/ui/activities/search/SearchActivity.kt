package com.marxist.android.ui.activities.search

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.marxist.android.data.model.WPPost
import com.marxist.android.databinding.ActivitySearchBinding
import com.marxist.android.model.ConnectivityType
import com.marxist.android.ui.activities.details.DetailsActivity
import com.marxist.android.ui.base.BaseActivity
import com.marxist.android.ui.base.ItemClickListener
import com.marxist.android.ui.fragments.feeds.FeedListAdapter
import com.marxist.android.utils.DeviceUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class SearchActivity : BaseActivity(), ItemClickListener {
    private val searchViewModel: SearchViewModel by viewModels()
    private val feedAdapter by lazy {
        FeedListAdapter(baseContext, mutableListOf(), this)
    }

    private val binding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        initListener()
        initObservers()
    }

    private val speechRecognizerResultsIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            if (result.resultCode == RESULT_OK && data != null) {
                val speechResult: ArrayList<String>? =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                if (speechResult != null) {
                    binding.edtSearch.text!!.clear()
                    binding.edtSearch.append(speechResult[0])
                    callSearch(speechResult[0])
                }
            }
        }

    private fun initListener() {
        binding.ivVoiceSearch.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.marxist.android")
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ta-IN")
            speechRecognizerResultsIntent.launch(intent)
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        with(binding.rvListView) {
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            adapter = feedAdapter
        }

        binding.edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                DeviceUtils.hideSoftKeyboard(this)
                val key = binding.edtSearch.text?.toString()
                if (key != null && key.isNotEmpty()) {
                    callSearch(key.toString())
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun initObservers() {
        searchViewModel.loading.observe(this, {
            if (it != null) {
                binding.rvListView.visibility = View.GONE
                binding.noFeed.emptyView.visibility = View.GONE
                binding.progressLoader.visibility = View.VISIBLE
            }
        })

        searchViewModel.wpPost.observe(this, {
            if (it != null) {
                binding.rvListView.visibility = View.VISIBLE
                binding.progressLoader.visibility = View.GONE
                binding.noFeed.emptyView.visibility = View.GONE
                feedAdapter.clear()
                feedAdapter.addFeed(it)
            }
        })

        searchViewModel.errorMessage.observe(this, {
            displayMaterialSnackBar("No result found", ConnectivityType.OTHER, binding.rootView)
        })
    }

    private fun callSearch(key: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(600)
            if (isActive) {
                if (key.length > 3) {
                    searchViewModel.resetQueryParams()
                    searchViewModel.search(key)
                }
            }
        }
    }

    override fun feedItemClickListener(article: Any, adapterPosition: Int, view: View) {
        if (article is WPPost) {
            val intent = Intent(baseContext, DetailsActivity::class.java)
            intent.putExtra(DetailsActivity.ARTICLE, article)
            startActivity(intent)
        }
    }
}