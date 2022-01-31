package com.android.stepcounter.model;

public abstract class ListEvent {

        public static final int TYPE_HEADER = 0;
        public static final int TYPE_ITEM = 1;

        abstract public int getType();
    }