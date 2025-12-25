package com.widgetkit.core

private const val MAX_COUNT = 8

object ViewKey {
    object Battery {
        private const val START_VIEW_ID = 280
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

    object Bluetooth {
        private const val START_VIEW_ID = 320

        fun getEarBudsTextId(index: Int): Int {
            return START_VIEW_ID + index
        }

        fun getEarBudsProgressId(index: Int): Int {
            return START_VIEW_ID + index + MAX_COUNT
        }
    }
}
