package com.myrungo.rungo.run

import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.PolylineOptions
import com.myrungo.rungo.BaseFragment
import com.myrungo.rungo.R
import com.myrungo.rungo.Scopes
import com.myrungo.rungo.cat.CatView
import com.myrungo.rungo.model.location.LocationService
import com.myrungo.rungo.run.alert.AlertFragment
import com.myrungo.rungo.visible
import kotlinx.android.synthetic.main.fragment_run.*
import toothpick.Toothpick

class RunFragment : BaseFragment(), RunView, AlertFragment.OnClickListener, OnMapReadyCallback {
    override val layoutRes = R.layout.fragment_run
    private var map: GoogleMap? = null

    @InjectPresenter
    lateinit var presenter: RunPresenter

    @ProvidePresenter
    fun providePresenter() = Toothpick
        .openScope(Scopes.PLAY)
        .getInstance(RunPresenter::class.java)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        map_view.onCreate(savedInstanceState)
        map_view.getMapAsync(this)

        play_button.setOnClickListener { presenter.onStartClicked() }
        stop_button.setOnClickListener { presenter.onStopClicked() }

        tab_run.setOnClickListener { presenter.onTabClicked(0) }
        tab_map.setOnClickListener { presenter.onTabClicked(1) }

        activity?.startService(Intent(activity, LocationService::class.java))
    }

    override fun showTime(curTime: String, challengeTime: String) {
        timer.text = curTime
        timer_divider.visible(challengeTime.isNotEmpty())
        timer_challenge.text = challengeTime
        timer_challenge.visible(challengeTime.isNotEmpty())
    }

    override fun showSpeed(curSpeed: Double, avgSpeed: Double) {
        speed.text = getString(R.string.speed, curSpeed)
        average_speed.text = getString(R.string.avg_speed, avgSpeed)
    }

    override fun showDistance(curDistance: String, challengeDistance: String) {
        distance.text = curDistance
        distance_divider.visible(challengeDistance.isNotEmpty())
        distance_challenge.text = challengeDistance
        distance_challenge.visible(challengeDistance.isNotEmpty())
    }

    override fun showSkin(skin: CatView.Skins) {
        cat_view.setSkin(skin)
    }

    override fun showMap(show: Boolean) {
        tab_run.setBackgroundResource(
            if (show) R.drawable.ic_run_accent
            else R.drawable.ic_run_active
        )
        tab_map.setBackgroundResource(
            if (show) R.drawable.ic_map_active
            else R.drawable.ic_map_accent
        )
        map_view.visible(show)
        cat_view.visible(!show)
        content_frame.visible(!show)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap
    }

    override fun showDialog(title: String, msg: String, tag: String) {
        AlertFragment.create(title, msg, tag).show(childFragmentManager, tag)
    }

    override fun showMessage(message: String?) {
        if (message != null) {
            val activity = activity

            if (activity != null) {
                Snackbar.make(
                    activity.findViewById<View>(android.R.id.content),
                    message,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

    }

    override fun run(isRun: Boolean) {
        screen_layout.setBackgroundResource(
            if (isRun) R.color.run_active_background
            else R.color.run_accent_background
        )
        play_button.setBackgroundResource(
            if (isRun) R.drawable.ic_run_pause
            else R.drawable.ic_run_play
        )
        stop_button.visible(!isRun)

        cat_view.post {
            if (isRun) cat_view.run()
            else cat_view.stop()
        }
    }

    override fun dialogPositiveClicked(tag: String) {
        presenter.exit()
        activity?.stopService(Intent(activity, LocationService::class.java))
    }

    override fun onBackPressed() {
        presenter.onStopClicked()
    }

    override fun drawRoute(location: Location) {
//        val polylineOptions = PolylineOptions().width(10f).color(Color.YELLOW)
//        polylineOptions.add(lastLatLng, LatLng(location.getLatitude(), location.getLongitude()))
//        val polyline = map.addPolyline(polylineOptions)
//        map?.addPolyline(polylineOptions)
    }

    override fun onResume() {
        super.onResume()
        map_view.onResume()
    }

    override fun onPause() {
        super.onPause()
        map_view.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map_view.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map_view.onLowMemory()
    }
}