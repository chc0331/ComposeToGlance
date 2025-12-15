package com.example.dsl.action

import android.os.Bundle
import android.os.Parcel

fun Bundle.toBytes(): ByteArray =
    Parcel.obtain().let { parcel ->
        writeToParcel(parcel, 0)
        parcel.marshall().also { parcel.recycle() }
    }

fun bundleFromBytes(bytes: ByteArray): Bundle =
    Parcel.obtain().let { parcel ->
        parcel.unmarshall(bytes, 0, bytes.size)
        parcel.setDataPosition(0)
        Bundle.CREATOR.createFromParcel(parcel).also { parcel.recycle() }
    }