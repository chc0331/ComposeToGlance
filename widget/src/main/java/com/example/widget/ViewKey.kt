package com.example.widget


private const val START_VIEW_ID = 280
private const val MAX_COUNT = 8

object ViewKey {
    object Battery {
        fun getBatteryTextId(index: Int): Int {
            return START_VIEW_ID + index
        }

        fun getBatteryProgressId(index: Int): Int {
            return START_VIEW_ID + index + MAX_COUNT
        }

        fun getBatteryIconId(index: Int): Int {
            return START_VIEW_ID + index + MAX_COUNT * 2
        }

        fun getChargingIconId(index: Int): Int {
            return START_VIEW_ID + index + MAX_COUNT * 3
        }
    }
}