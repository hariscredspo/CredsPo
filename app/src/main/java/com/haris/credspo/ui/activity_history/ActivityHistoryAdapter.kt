package com.haris.credspo.ui.activity_history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.haris.credspo.ApiInterface
import com.haris.credspo.R
import com.haris.credspo.models.ActivityHistoryResponse
import com.haris.credspo.models.ActivityModel
import com.haris.credspo.models.DeleteResponse
import com.haris.credspo.ui.TwoButtonDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityHistoryAdapter(
    private val context: Context,
    private val activityHistoryResponse: ActivityHistoryResponse,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<ActivityHistoryAdapter.ActivityHistoryViewHolder>() {

    inner class ActivityHistoryViewHolder(itemView: View) :  RecyclerView.ViewHolder(itemView) {
        fun bind(item: ActivityModel) {
            itemView.findViewById<TextView>(R.id.activity_history_item_date).text = item.date
            itemView.findViewById<TextView>(R.id.activity_history_item_name).text = item.title

            val listener = View.OnClickListener {
                println("CONFIRMED")
                val sharedPrefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
                sharedPrefs.getString("BEARER_TOKEN", null)?.let {
                    ApiInterface.create().deleteActivity("Bearer $it", id = item.id)
                        .enqueue(object: Callback<DeleteResponse> {
                            override fun onResponse(
                                call: Call<DeleteResponse>,
                                response: Response<DeleteResponse>
                            ) {
                                val pos = activityHistoryResponse.data.activities.indexOf(item)
                                activityHistoryResponse.data.activities.remove(item)
                                notifyItemRemoved(pos)
                            }

                            override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                                Toast.makeText(context, "Could not delete activity", Toast.LENGTH_LONG).show()
                            }
                        })
                }
            }

            itemView.findViewById<ImageView>(R.id.activity_history_item_delete_background).setOnClickListener {
                val dialog = TwoButtonDialogFragment(listener, View.OnClickListener{}, "Delete activity?",  "Do you want to delete ${item.title}?", "YES", "NO")
                dialog.show(fragmentManager, "ConfirmationDialogFragment")
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ActivityHistoryViewHolder {
        return ActivityHistoryViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_activity_history_field, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ActivityHistoryViewHolder, position: Int) {
        holder.bind(activityHistoryResponse.data.activities[position])
    }

    override fun getItemCount(): Int {
        return activityHistoryResponse.data.activities.size
    }
}