package com.hobarb.locatadora.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.LoadBundleTask
import com.hobarb.locatadora.R
import com.hobarb.locatadora.models.ContactsModel
import com.hobarb.locatadora.utilities.CONSTANTS
import com.hobarb.locatadora.utilities.SharedPrefs
import com.hobarb.locatadora.utilities.views.Loader

class ContactsAdapter(var context: Context, contactsModels: ArrayList<ContactsModel>) :
    RecyclerView.Adapter<ContactsAdapter.ContactsAdapterViewHolder>() {

    var contactsModels:ArrayList<ContactsModel>
    init {
        this.context = context
        this.contactsModels = contactsModels
    }

    inner class  ContactsAdapterViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var contactNo_tv: TextView
        var contactName_tv: TextView
        var deleteContact_iv: ImageView

        init {
            contactNo_tv = itemView.findViewById(R.id.tv_contactNo_ca_contact);
            contactName_tv = itemView.findViewById(R.id.tv_contactName_ca_contact);
            deleteContact_iv = itemView.findViewById(R.id.iv_delete_ca_contact);
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsAdapterViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_contact, parent, false)
        return ContactsAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactsAdapterViewHolder, position: Int) {
        holder.contactName_tv.setText("" + contactsModels[position].name)
        holder.contactNo_tv.setText("" + contactsModels[position].number)
        holder.deleteContact_iv.setOnClickListener {

            val db = FirebaseFirestore.getInstance()
            val sharedPrefs = SharedPrefs(context)
            val identifier = sharedPrefs.readPrefs(CONSTANTS.SHARED_PREF_KEYS.IDENTIFIER)

            val docRef = db.collection(CONSTANTS.FIRESTORESTUFF.MAINTABLE).document(identifier)
                .collection(CONSTANTS.FIRESTORESTUFF.CONTACTS)
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {

                        db.collection(CONSTANTS.FIRESTORESTUFF.MAINTABLE).document(identifier)
                            .collection(CONSTANTS.FIRESTORESTUFF.CONTACTS).document(task.result.documents[position].id).delete().addOnCompleteListener {
                                task ->
                                if (task.isSuccessful)
                                {
                                    Toast.makeText(context, "Contact deleted!", Toast.LENGTH_SHORT).show()
                                    contactsModels.removeAt(position)
                                    this.notifyItemChanged(position) //Just pass



                                }
                                else
                                {
                                    Toast.makeText(context, "Somttttttt", Toast.LENGTH_SHORT).show()
                                }
                            }

                }
            }

        }
    }

    override fun getItemCount(): Int {
        return contactsModels.size
    }

}
