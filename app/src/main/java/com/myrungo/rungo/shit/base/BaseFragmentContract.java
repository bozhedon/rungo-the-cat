package com.myrungo.rungo.shit.base;

public interface BaseFragmentContract extends BaseContract {

    interface View extends BaseContract.View {
    }

    interface Presenter<V extends BaseContract.View> extends BaseContract.Presenter<V> {
    }

}
