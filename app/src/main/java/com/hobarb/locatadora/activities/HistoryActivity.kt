package com.hobarb.locatadora.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.hobarb.locatadora.R
import com.hobarb.locatadora.adapters.HistoryAdapter
import com.hobarb.locatadora.models.HistoryModel
import com.hobarb.locatadora.utilities.CONSTANTS

class HistoryActivity : AppCompatActivity() {

    lateinit var historyModels:ArrayList<HistoryModel>
    lateinit var recyclerView: RecyclerView
    lateinit var historyAdapter: HistoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        val db = FirebaseFirestore.getInstance()

        historyModels = arrayListOf()
        recyclerView = findViewById(R.id.rv_history_ac_history)

        val docRef = db.collection(CONSTANTS.FIRESTORESTUFF.MAINTABLE).document(CONSTANTS.FIRESTORESTUFF.USERID).collection(CONSTANTS.FIRESTORESTUFF.HISTORY)
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents)
                    {

                        val historyModel = HistoryModel(document.data[CONSTANTS.MAPKEYS.LOCATION].toString(),document.data[CONSTANTS.MAPKEYS.DATETIME].toString())
                        historyModels.add(historyModel)
                        Toast.makeText(applicationContext, ""+historyModel, Toast.LENGTH_SHORT).show()

                    }

                    val linearLayoutManager =
                        LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
                    historyAdapter = HistoryAdapter(applicationContext, historyModels!!)
                    recyclerView!!.setLayoutManager(linearLayoutManager)
                    recyclerView!!.setAdapter(historyAdapter)




                } else {
                    Toast.makeText(applicationContext, "not exist", Toast.LENGTH_SHORT).show()
                }


            }
            .addOnFailureListener { exception ->
                Toast.makeText(applicationContext, ""+exception, Toast.LENGTH_SHORT).show()
            }

    }
}