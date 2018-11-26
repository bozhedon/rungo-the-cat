package com.myrungo.rungo.main

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import com.myrungo.rungo.R

class MainNavigation @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val iconAccentRes = listOf(
        R.drawable.ic_home_false,
        R.drawable.ic_customize_false,
        R.drawable.ic_play,
        R.drawable.ic_challenge_false,
        R.drawable.ic_profile_false
    )
    private val iconActiveRes = listOf(
        R.drawable.ic_home_true,
        R.drawable.ic_customize_true,
        R.drawable.ic_play,
        R.drawable.ic_challenge_true,
        R.drawable.ic_profile_true
    )
    private val items = mutableListOf<ImageView>()
    private var onItemClickListener: (Int) -> Unit = {}
    private var position = -1

    var currentPosition: Int
        get() = position
        set(value) {
            if (value !in 0 until items.size || value == currentPosition) {
                return
            }

            if (currentPosition != -1) items[currentPosition].setImageResource(iconAccentRes[currentPosition])
            items[value].setImageResource(iconActiveRes[value])

            position = value
        }

    init {
        inflate(context, R.layout.view_navigation_main, this)

        items.clear()
        items.add(findViewById(R.id.nav_home))
        items.add(findViewById(R.id.nav_customize))
        items.add(findViewById(R.id.nav_play))
        items.add(findViewById(R.id.nav_challenge))
        items.add(findViewById(R.id.nav_profile))

        for (position in 0 until items.size) {
            items[position].setImageResource(iconAccentRes[position])
            items[position].setOnClickListener { onItemClickListener.invoke(position) }
        }
    }

    fun setOnItemClickListener(onItemClickListener: (Int) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }
}