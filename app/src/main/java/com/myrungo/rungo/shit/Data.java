package com.myrungo.rungo.shit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Data {
    private boolean isRunning;
    private long time;
    private long timeStopped;
    private boolean isFirstTime;
    private boolean signal;

    private double distanceM;
    private double curSpeed;
    private double maxSpeed;

    @Nullable
    private OnGpsServiceUpdate onGpsServiceUpdate;

    @NonNull
    private final List<LatLng> positions = new ArrayList<>();

    public interface OnGpsServiceUpdate {
        void update();
    }

    public void setOnGpsServiceUpdate(@NonNull final OnGpsServiceUpdate onGpsServiceUpdate) {
        this.onGpsServiceUpdate = onGpsServiceUpdate;
    }

    public void update(){
        onGpsServiceUpdate.update();
    }

    public Data() {
        isRunning = false;
        distanceM = 0;
        curSpeed = 0;
        maxSpeed = 0;
        timeStopped = 0;
        positions.clear();
    }

    public Data(@NonNull final OnGpsServiceUpdate onGpsServiceUpdate) {
        this();
        setOnGpsServiceUpdate(onGpsServiceUpdate);
    }

    public void addDistance(final double distance) {
        distanceM = distanceM + distance;
    }

    public double getDistance(){
        return distanceM;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getAverageSpeed(){
        final double average;
        String units;
        if (time <= 0) {
            average = 0.0;
        } else {
            average = (distanceM / (time / 1000.0)) * 3.6;
        }
        return average;
    }

    public double getAverageSpeedMotion(){
        final long motionTime = time - timeStopped;
        final double average;

        if (motionTime <= 0){
            average = 0.0;
        } else {
            average = (distanceM / (motionTime / 1000.0)) * 3.6;
        }

        return average;
    }

    public void setCurSpeed(final double curSpeed) {
        this.curSpeed = curSpeed;
        if (curSpeed > maxSpeed){
            maxSpeed = curSpeed;
        }
    }

    public void addPosition(@NonNull final LatLng position) {
        positions.add(position);
    }
    public LatLng getLastPosition(){
        return positions.get(positions.size()-1);
    }

    @NonNull
    public List<LatLng> getPositions() { return positions;}

    public boolean isFirstTime() {
        return isFirstTime;
    }

    public void setFirstTime(final boolean isFirstTime) {
        this.isFirstTime = isFirstTime;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(final boolean isRunning) {
        this.isRunning = isRunning;
    }

    public void setTimeStopped(final long timeStopped) {
        this.timeStopped += timeStopped;
    }

    public double getCurSpeed() {
        return curSpeed;
    }

    public long getTime() {
        return time;
    }

    public void setTime(final long time) {
        this.time = time;
    }

    public boolean isSignal() {return signal;}

    public void setSignal(final boolean x) {
        signal = x;
    }
}
