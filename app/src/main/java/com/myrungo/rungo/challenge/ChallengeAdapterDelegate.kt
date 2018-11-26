package com.myrungo.rungo.challenge

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import com.myrungo.rungo.R
import com.myrungo.rungo.inflate
import kotlinx.android.synthetic.main.item_challenge.view.*

class ChallengeAdapterDelegate(private val clickListener: (ChallengeItem) -> Unit = {}) : AdapterDelegate<MutableList<Any>>() {

    override fun isForViewType(items: MutableList<Any>, position: Int): Boolean =
        items[position] is ChallengeItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        ViewHolder(parent.inflate(R.layout.item_challenge))

    override fun onBindViewHolder(
        items: MutableList<Any>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) = (holder as ViewHolder).bind(items[position] as ChallengeItem)

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var challenge: ChallengeItem

        init {
            view.setOnClickListener { clickListener.invoke(challenge) }
        }

        fun bind(challenge: ChallengeItem) {
            this.challenge = challenge

            with(itemView) {
                item_challenge_distance.text = challenge.distance
                item_challenge_time.text = challenge.time

                Glide.with(this)
                    .load(challenge.awardRes)
                    .into(item_challenge_award_image)
            }
        }
    }
}