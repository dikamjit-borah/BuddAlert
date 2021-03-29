package com.hobarb.locatadora.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hobarb.locatadora.R
import com.hobarb.locatadora.models.HistoryModel

class HistoryAdapter (var context: Context, historyModels:ArrayList<HistoryModel>) : RecyclerView.Adapter<HistoryAdapter.HistoryAdapterViewHolder>(){

    var historyModels:ArrayList<HistoryModel>

    init {
        this.historyModels = historyModels

    }

    inner class  HistoryAdapterViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var location_tv: TextView
         var datetime_tv: TextView

        init {
            location_tv = itemView.findViewById(R.id.tv_location_ca_history);
            datetime_tv = itemView.findViewById(R.id.tv_datetime_ca_history);
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapterViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_history, parent, false)
        return HistoryAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryAdapterViewHolder, position: Int) {
        holder.location_tv.setText(""+historyModels[position].location)
        holder.datetime_tv.setText(""+historyModels[position].datetime)

    }

    override fun getItemCount(): Int {
        return historyModels.size
    }


}
