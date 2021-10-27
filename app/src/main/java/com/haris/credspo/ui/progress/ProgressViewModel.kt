package com.haris.credspo.ui.progress

import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.haris.credspo.R

class ProgressViewModel : ViewModel() {
    val currentTab = MutableLiveData<Int>(0)

    fun switchTab(context: Context, tabsList: List<TextView>, tab: Int) {
        currentTab.value?.let {
            tabsList[it].setBackgroundResource(R.drawable.rect_rounded_360_light_gray)
            (tabsList[it] as TextView).setTextColor(ContextCompat.getColor(context, R.color.gray))
        }

        currentTab.postValue(tab)

        tabsList[tab].setBackgroundResource(R.drawable.rect_rounded_360_cyan)
        (tabsList[tab] as TextView).setTextColor(ContextCompat.getColor(context, R.color.white))
    }
}