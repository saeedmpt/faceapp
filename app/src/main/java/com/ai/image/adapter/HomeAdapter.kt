package com.ai.image.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.ai.image.model.HomeModel
import com.ai.image.interfaces.ICallBack
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.ai.image.R
import androidx.core.content.ContextCompat
import android.widget.TextView
import java.util.ArrayList

class HomeAdapter(private val contInst: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data: List<HomeModel> = ArrayList()
    private var iCallBack: ICallBack<HomeModel>? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_home, parent, false)
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as ItemViewHolder
        holder.tvContent.text = data[position].content
        holder.tvName.text = data[position].name
        holder.ivImage.setImageDrawable(ContextCompat.getDrawable(contInst, data[position].image))
        holder.cvItem.setOnClickListener { v: View? ->
            iCallBack!!.callBack(
                data[position])
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setiCallBack(iCallBack: ICallBack<HomeModel>?) {
        this.iCallBack = iCallBack
    }

    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var cvItem: View = v.findViewById(R.id.cvItem)
        var tvName: TextView = v.findViewById(R.id.tvName)
        var tvContent: TextView = v.findViewById(R.id.tvContent)
        var ivImage: ImageView = v.findViewById(R.id.ivImage)

    }
}