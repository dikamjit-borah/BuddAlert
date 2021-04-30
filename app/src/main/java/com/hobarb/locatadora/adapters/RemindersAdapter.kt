package com.hobarb.locatadora.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hobarb.locatadora.R
import com.hobarb.locatadora.models.RemindersModel


class RemindersAdapter (var context: Context, reminders:ArrayList<RemindersModel>) : RecyclerView.Adapter<RemindersAdapter.RemindersAdapterViewHolder>(){

    var reminders:ArrayList<RemindersModel>

    init {
        this.reminders = reminders

    }

    inner class  RemindersAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val eventDate_tv: TextView = itemView.findViewById(R.id.tv_eventDate_ca_rem);
        val eventTime_tv: TextView = itemView.findViewById(R.id.tv_eventTime_ca_rem);
        val eventLocation_tv: TextView = itemView.findViewById(R.id.tv_eventLocation_ca_rem);
        val eventName_tv: TextView = itemView.findViewById(R.id.tv_eventName_ca_rem);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemindersAdapterViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_reminder, parent, false)
        return RemindersAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: RemindersAdapterViewHolder, position: Int) {
        holder.eventDate_tv.setText(""+reminders[position].eventDate)
        holder.eventTime_tv.setText(""+reminders[position].eventTime)
        holder.eventLocation_tv.setText(""+reminders[position].eventLocation)
        holder.eventName_tv.setText(""+reminders[position].eventName)

    }

    override fun getItemCount(): Int {
        return reminders.size
    }


}
