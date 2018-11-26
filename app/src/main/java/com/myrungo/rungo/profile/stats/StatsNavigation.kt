package com.myrungo.rungo.profile.stats

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.myrungo.rungo.R

class StatsNavigation @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val items = mutableListOf<TextView>()
    private var onItemClickListener: (Int) -> Unit = {}
    private var position = 0

    var currentTab: Int
        get() = position
        set(value) {
            if (value !in 0 until items.size || value == position) {
                return
            }

            items[position].setBackgroundResource(0)
            items[value].setBackgroundResource(R.drawable.stats_tab_background)

            position = value
        }

    init {
        inflate(context, R.layout.view_navigation_stats, this)

        items.clear()
        items.add(findViewById(R.id.tab_week))
        items.add(findViewById(R.id.tab_month))
        items.add(findViewById(R.id.tab_year))

        for (position in 0 until items.size) {
            items[position].setOnClickListener { onItemClickListener.invoke(position) }
        }

        onItemClickListener.invoke(position)
    }

    fun setOnItemClickListener(onItemClickListener: (Int) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }
}