package com.example.g2c.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.g2c.R
import com.example.g2c.activities.MainActivity
import com.example.g2c.model.CheckListModelClass

class Adapter(
    private var checkList: List<CheckListModelClass>,
    private val listener: MainActivity
    ) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_design_for_recycler_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            val checkListItem: TextView = findViewById(R.id.tv_check_list_text)
            checkListItem.text = checkList[position].checkListItemText
        }

    }

    override fun getItemCount(): Int {
        return checkList.size
    }

//    ViewHolder class -
inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener{
    init {
        itemView.setOnLongClickListener(this)
    }

    override fun onLongClick(v: View?): Boolean {
        val position = adapterPosition
        if (position != RecyclerView.NO_POSITION){
            listener.onLongItemClick(position)
        }
        return true
    }

}
    interface OnItemLongClickListener{
        fun onLongItemClick(position: Int)
    }
}