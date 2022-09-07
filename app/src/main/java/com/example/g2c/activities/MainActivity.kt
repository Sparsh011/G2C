package com.example.g2c.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.g2c.R
import com.example.g2c.adapter.Adapter
import com.example.g2c.model.CheckListModelClass
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class MainActivity : AppCompatActivity(), Adapter.OnItemLongClickListener {
    private lateinit var databaseRef: DatabaseReference
    private lateinit var checkListRecyclerView: RecyclerView
    private lateinit var checkList: ArrayList<CheckListModelClass>
    private lateinit var adapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val addItem: FloatingActionButton = findViewById(R.id.btn_add_to_list)
        addItem.setOnClickListener {
            showAddItemDialog()

        }

//        Initialising recyclerview -
        setRecyclerView()

//        Fetching data from realtime database -
        getUserData()
    }

    private fun setRecyclerView(){
        checkList = arrayListOf()
        checkListRecyclerView = findViewById(R.id.rv_check_list)
        checkListRecyclerView.layoutManager = LinearLayoutManager(this)
        checkListRecyclerView.setHasFixedSize(true)
        adapter = Adapter(checkList, this@MainActivity)
        checkListRecyclerView.adapter = adapter
    }

    private fun getUserData() {
        databaseRef = FirebaseDatabase.getInstance().getReference("checkListItems")
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (items in snapshot.children) {
                        val item = items.getValue(CheckListModelClass::class.java)
                        checkList.add(item!!)
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Error: " + error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onLongItemClick(position: Int) {
        showDeleteDialog(position)
    }

    private fun showDeleteDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.delete_item, null)
        val btnDelete = dialogLayout.findViewById<Button>(R.id.btn_delete_item)

        builder.setView(dialogLayout).create().show()

        btnDelete.setOnClickListener{
            databaseRef = FirebaseDatabase.getInstance().getReference("checkListItems")
            databaseRef.child(checkList[position].checkListItemText!!).removeValue().addOnSuccessListener {
                Toast.makeText(applicationContext, "Item Deleted", Toast.LENGTH_SHORT).show()
            }
            val size = checkList.size
            checkList.clear()
            adapter.notifyItemRangeRemoved(0, size)
        }
    }

    private fun showAddItemDialog(){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.add_item, null)
        val checkItem = dialogLayout.findViewById<EditText>(R.id.et_check_list_item)
        val btnAdd = dialogLayout.findViewById<Button>(R.id.btn_add_check_list_item)

        btnAdd.setOnClickListener{
            val text = checkItem.text.toString()
            val item = CheckListModelClass(text)
            databaseRef = FirebaseDatabase.getInstance().getReference("checkListItems")

            databaseRef.child(text).setValue(item).addOnSuccessListener {
                Toast.makeText(applicationContext, "Item Uploaded", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(applicationContext, "Failed to save", Toast.LENGTH_SHORT).show()
            }
            val size = checkList.size
            checkList.clear()
            adapter.notifyItemRangeRemoved(0, size)
        }

        builder.setView(dialogLayout).create().show()
    }
}