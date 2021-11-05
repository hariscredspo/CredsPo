package com.haris.credspo.ui.progress

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.haris.credspo.ApiInterface
import com.haris.credspo.databinding.FragmentProgressBinding
import com.haris.credspo.models.UserBadgesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProgressFragment: Fragment() {
    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProgressViewModel

    private var tabsList = mutableListOf<TextView>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ProgressViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabsList.add(binding.progressTabSkills)
        tabsList.add(binding.progressTabAttributes)
        tabsList.add(binding.progressTabWaypoints)

        for (tab in 0 until tabsList.size) {
            tabsList[tab].setOnClickListener {
                viewModel.switchTab(requireContext(), tabsList, tab)
            }
        }

        binding.progressRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3, LinearLayoutManager.VERTICAL, false)

        // when switching between tabs
        viewModel.currentTab.observeForever { type ->
            val sharedPrefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
            val token = sharedPrefs.getString("BEARER_TOKEN", null)

            token?.let {
                val response: Call<UserBadgesResponse> = when(type) {
                    0 -> ApiInterface.create().getUserSkills(token = "Bearer $it")
                    1 -> ApiInterface.create().getUserAttributes(token = "Bearer $it")
                    else -> ApiInterface.create().getUserWaypoints(token = "Bearer $it")
                }
                response.enqueue(object: Callback<UserBadgesResponse> {
                    override fun onResponse(
                        call: Call<UserBadgesResponse>,
                        response: Response<UserBadgesResponse>
                    ) {
                        response.body()?.let { body ->
                            binding.progressRecyclerView.adapter = ProgressAdapter(requireContext(), body, type)
                        }
                    }

                    override fun onFailure(call: Call<UserBadgesResponse>, t: Throwable) {}
                })
            }
        }
    }
}