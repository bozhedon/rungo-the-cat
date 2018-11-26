package com.myrungo.rungo.list

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import com.myrungo.rungo.R
import com.myrungo.rungo.inflate
import kotlinx.android.synthetic.main.item_header.view.*

class HeaderAdapterDelegate : AdapterDelegate<MutableList<Any>>() {

    override fun isForViewType(items: MutableList<Any>, position: Int) =
        items[position] is HeaderItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        ViewHolder(parent.inflate(R.layout.item_header))

    override fun onBindViewHolder(
        items: MutableList<Any>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) = (holder as ViewHolder).bind(items[position] as HeaderItem)

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(header: HeaderItem) {
            with(itemView) {
                item_header_text.text = header.text
            }
        }
    }
}