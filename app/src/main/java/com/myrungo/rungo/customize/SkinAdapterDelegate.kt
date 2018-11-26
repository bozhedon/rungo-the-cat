package com.myrungo.rungo.customize

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import com.myrungo.rungo.R
import com.myrungo.rungo.inflate
import com.myrungo.rungo.visible
import kotlinx.android.synthetic.main.item_skin.view.*

class SkinAdapterDelegate(private val clickListener: (SkinItem) -> Unit = {}) : AdapterDelegate<MutableList<Any>>() {

    override fun isForViewType(items: MutableList<Any>, position: Int): Boolean =
        items[position] is SkinItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        ViewHolder(parent.inflate(R.layout.item_skin))

    override fun onBindViewHolder(
        items: MutableList<Any>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) = (holder as ViewHolder).bind(items[position] as SkinItem)

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var skin: SkinItem

        init {
            view.setOnClickListener { clickListener.invoke(skin) }
        }

        fun bind(skin: SkinItem) {
            this.skin = skin

            with(itemView) {
                Glide.with(this)
                    .load(skin.resId)
                    .into(item_skin_image)

                item_skin_block.visible(!skin.isAvailable)
            }
        }
    }
}