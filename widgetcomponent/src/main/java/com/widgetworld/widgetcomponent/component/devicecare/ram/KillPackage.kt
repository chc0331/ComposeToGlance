package com.widgetworld.widgetcomponent.component.devicecare.ram

import android.app.ActivityManager
import android.content.Context
import android.text.TextUtils
import android.util.Log
import java.lang.reflect.Method

object KillPackage {

    fun invokeKill(context: Context, pkgName: String) {
        val activityManager = getActivityManagerObject(context)
        val killMethod = getKillBgAsUserMethod()
        invoke(activityManager, null, killMethod, pkgName, 0)
    }


    fun getActivityManagerObject(context: Context): Any? {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val serviceMethod = getMethod(ActivityManager::class.java, "getService")
        return invoke(am, null, serviceMethod) as Any?
    }

    fun getKillBgAsUserMethod(): Method? {
        val activityClass = getClass("android.app.IActivityManager")
        val method =
            getMethod(activityClass, "killBackgroundProcesses", String::class.java, Int::class.java)
        return method
    }

    fun getClass(className: String?): Class<*>? {
        if (TextUtils.isEmpty(className)) {
            return null
        }

        return try {
            Class.forName(className)
        } catch (e: ClassNotFoundException) {
            null
        }
    }

    fun getMethod(
        targetClass: Class<*>?,
        name: String?,
        vararg parameterTypes: Class<*>?
    ): Method? {
        if (targetClass == null || TextUtils.isEmpty(name)) {
            return null
        }

        return try {
            targetClass.getMethod(name, *parameterTypes)
        } catch (e: NoSuchMethodException) {
            try {
                targetClass.getDeclaredMethod(name, *parameterTypes)
            } catch (e2: NoSuchMethodException) {
                null
            }
        }
    }

    fun <T> invoke(receiver: Any?, defaultValue: T, method: Method?, vararg args: Any?): T {
        if (method == null) {
            return defaultValue
        }

        return try {
            method.isAccessible = true
            method.invoke(receiver, *args) as T
        } catch (e: Exception) {
            Log.i("heec.choi", "Exception : $e")
            defaultValue
        }
    }
}