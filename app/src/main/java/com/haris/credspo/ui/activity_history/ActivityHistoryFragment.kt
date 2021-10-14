package com.haris.credspo.ui.activity_history

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.haris.credspo.ApiInterface
import com.haris.credspo.R
import com.haris.credspo.databinding.FragmentActivityHistoryBinding
import com.haris.credspo.models.ActivityHistoryResponse
import com.haris.credspo.models.DeleteResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityHistoryFragment : Fragment() {
    private var _binding:  FragmentActivityHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ActivityHistoryViewModel

    private var tabsList = mutableListOf<TextView>()
    private var currentTab: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentActivityHistoryBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ActivityHistoryViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabsList.add(binding.activityHistoryTabAll)
        tabsList.add(binding.activityHistoryTabSkills)
        tabsList.add(binding.activityHistoryTabAttributes)
        tabsList.add(binding.activityHistoryTabWaypoints)

        for (tab in 0 until tabsList.size) {
            tabsList[tab].setOnClickListener {
                viewModel.switchTab(requireContext(), tabsList, tab)
            }
        }

        binding.activityHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        viewModel.currentTab.observeForever { type ->
            println("SWITCHED TAB")
            val sharedPrefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
            val token = sharedPrefs.getString("BEARER_TOKEN", null)

            token?.let {
                ApiInterface.create().getActivityHistory(token = "Bearer $it", badgeTypeID = type, badgeID = 0)
                    .enqueue(object: Callback<ActivityHistoryResponse> {
                        override fun onResponse(
                            call: Call<ActivityHistoryResponse>,
                            response: Response<ActivityHistoryResponse>
                        ) {
                            response.body()?.let { body ->
                                binding.activityHistoryRecyclerView.adapter = ActivityHistoryAdapter(requireContext(), body, requireActivity().supportFragmentManager)
                            }
                        }
                        override fun onFailure(call: Call<ActivityHistoryResponse>, t: Throwable) {}
                    })
            }
        }
    }
}