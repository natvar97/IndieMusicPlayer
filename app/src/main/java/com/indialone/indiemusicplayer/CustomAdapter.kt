package com.indialone.indiemusicplayer

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.indialone.indiemusicplayer.databinding.ListItemBinding
import java.io.File

class CustomAdapter(
    private val activity: Activity,
    private val items: Array<String?>?
) : BaseAdapter() {
    override fun getCount(): Int {
        return items!!.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.list_item, null)
        val textSong = view.findViewById<TextView>(R.id.tv_song_name)
        textSong.isSelected = true
        textSong.text = items!![position]!!
        return view
    }
}
//class CustomAdapter(
//    private val activity: Activity,
//    private val items: Array<String?>?,
//    private val songs: ArrayList<File>
//) : RecyclerView.Adapter<CustomAdapter.CustomViewHolder>() {
//    class CustomViewHolder(itemView: ListItemBinding) : RecyclerView.ViewHolder(itemView.root){
//        val name = itemView.tvSongName
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
//        val view = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return CustomViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
//        holder.name.text = items!![position]
//        holder.itemView.setOnClickListener {
//            val intent = Intent(activity, MusicPlayerActivity::class.java)
//            intent.putExtra("songs", songs)
////            intent.putExtra("songName", songName)
//            intent.putExtra("position", position)
//            activity.startActivity(intent)
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return items!!.size
//    }
//
//}
