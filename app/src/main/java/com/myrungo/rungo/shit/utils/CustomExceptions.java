package com.myrungo.rungo.shit.utils;

import android.support.annotation.NonNull;

public final class CustomExceptions {

    public static final class UnauthorizedUserException extends Exception {

        @NonNull
        final String message;

        public UnauthorizedUserException(@NonNull final String message) {
            super(message);
            this.message = message;
        }

        @NonNull
        @Override
        public String getMessage() {
            return message;
        }

    }

    public static final class NullUserInfoException extends Exception {

        @NonNull
        final String message;

        public NullUserInfoException(@NonNull final String message) {
            super(message);
            this.message = message;
        }

        @NonNull
        @Override
        public String getMessage() {
            return message;
        }

    }


    public static final class DBFieldsHasDifferentSctructureException extends Exception {

        @NonNull
        final String message;

        public DBFieldsHasDifferentSctructureException(@NonNull final String message) {
            super(message);
            this.message = message;
        }

        @NonNull
        @Override
        public String getMessage() {
            return message;
        }

    }

}
