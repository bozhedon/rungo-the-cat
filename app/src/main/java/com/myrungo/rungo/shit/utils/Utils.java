package com.myrungo.rungo.shit.utils;

import android.support.annotation.NonNull;

public final class Utils {

    @NonNull
    public static String roundIfNeeded(double value) {
        final int res = (int) value;

        double fraction = res - value;
        boolean needed = fraction == 0.0;

        if (needed)
            return String.valueOf(res);
        else
            return String.valueOf(value);
    }

}
