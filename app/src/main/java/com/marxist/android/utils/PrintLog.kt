package com.marxist.android.utils

import android.util.Log

object PrintLog {
    fun debug(tag: String, str: String) {
        /*if (!BuildConfig.DEBUG) {
            return
        }*/
        if (str.length > 4000) {
            Log.d("Marxist", tag + "-->" + str.substring(0, 4000))
            debug(tag, str.substring(4000))
        } else {
            Log.d("Marxist", "$tag-->$str")
        }
    }

    fun info(tag: String, str: String) {
        if (str.length > 4000) {
            Log.d("Marxist", tag + "-->" + str.substring(0, 4000))
            info(tag, str.substring(4000))
        } else {
            Log.d("Marxist", "$tag-->$str")
        }
    }
}