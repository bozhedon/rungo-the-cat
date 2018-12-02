package com.myrungo.rungo.run.alert

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import com.myrungo.rungo.R
import kotlinx.android.synthetic.main.fragment_alert.*

class AlertFragment : DialogFragment() {
    private val clickListener
        get() = when {
            parentFragment is OnClickListener -> parentFragment as OnClickListener
            activity is OnClickListener -> activity as OnClickListener
            else -> object : OnClickListener {}
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.OverlayTheme)
        isCancelable = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        LayoutInflater.from(context).inflate(R.layout.fragment_alert, null)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        alert_title.text = arguments?.getString(ARG_TITLE)
        alert_message.text = arguments?.getString(ARG_MESSAGE)

        view?.setOnClickListener { dismiss() }
        alert_view.setOnClickListener { }
        alert_cancel_button.setOnClickListener {
            dismissAllowingStateLoss()
            clickListener.dialogNegativeClicked(arguments?.getString(ARG_TAG) ?: "alert_dialog_tag")
        }
        alert_accept_button.setOnClickListener {
            dismissAllowingStateLoss()
            clickListener.dialogPositiveClicked(arguments?.getString(ARG_TAG) ?: "alert_dialog_tag")
        }
    }

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_MESSAGE = "arg_message"
        private const val ARG_TAG = "arg_tag"

        fun create(title: String, message: String, tag: String) =
            AlertFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_MESSAGE, message)
                    putString(ARG_TAG, tag)
                }
            }
    }

    interface OnClickListener {
        fun dialogPositiveClicked(tag: String) {}
        fun dialogNegativeClicked(tag: String) {}
        fun dialogCanceled(tag: String) {}
    }
}