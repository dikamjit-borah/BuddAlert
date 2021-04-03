package com.hobarb.locatadora.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hobarb.locatadora.R
import com.hobarb.locatadora.models.ContactsModel

class ContactsAdapter (var context: Context, contactsModels:ArrayList<ContactsModel>) :
    RecyclerView.Adapter<ContactsAdapter.ContactsAdapterViewHolder>() {

    var contactsModels:ArrayList<ContactsModel>
    init {
        this.contactsModels = contactsModels
    }

    inner class  ContactsAdapterViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var contactNo_tv: TextView
        var contactName_tv: TextView

        init {
            contactNo_tv = itemView.findViewById(R.id.tv_contactNo_ca_contact);
            contactName_tv = itemView.findViewById(R.id.tv_contactName_ca_contact);
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsAdapterViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_contact, parent, false)
        return ContactsAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactsAdapterViewHolder, position: Int) {
        holder.contactName_tv.setText(""+contactsModels[position].name)
        holder.contactNo_tv.setText(""+contactsModels[position].number)
    }

    override fun getItemCount(): Int {
        return contactsModels.size
    }

}
