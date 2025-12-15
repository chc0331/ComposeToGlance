package com.example.dsl.action

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import androidx.core.os.bundleOf
import androidx.glance.action.ActionParameters
import androidx.glance.action.mutableActionParametersOf
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallbackBroadcastReceiver
import androidx.glance.appwidget.action.ToggleableStateKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class WidgetActionCallbackBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("heec.choi","onReceive : ${intent.action}")
        goAsync {
            val glanceManager = GlanceAppWidgetManager(context)
            val extras = intent.extras
            if (extras != null) {
                val paramsBundle = requireNotNull(extras.getBundle(ExtraParameters)) {
                    "The intent must contain a parameters bundle using extra: $ExtraParameters"
                }
                val parameters = widgetActionParametersOf().apply {
                    paramsBundle.keySet().forEach { key ->
                        set(WidgetActionParameters.Key(key), paramsBundle[key])
                    }
                }
                val className = requireNotNull(extras.getString(ExtraCallbackClassName))
                val glanceId = glanceManager.getGlanceIdBy(extras.getInt(AppWidgetId))

                RunWidgetCallbackAction.run(context, className, glanceId, parameters)
            }
        }
    }

    companion object {
        internal const val AppWidgetId = "widgetId"
        internal const val ExtraCallbackClassName = "callbackClass"
        internal const val ExtraParameters = "extraParameters"

        internal fun createIntent(
            context: Context,
            receiverClass: Class<out WidgetActionCallbackBroadcastReceiver>,
            callbackClass: Class<out WidgetActionCallback>,
            appWidgetId: Int,
            parameters: WidgetActionParameters
        ) = Intent(
            context, receiverClass::class.java
        ).setPackage(context.packageName)
            .putExtra(ExtraCallbackClassName, callbackClass.canonicalName)
            .putExtra(AppWidgetId, appWidgetId)
            .putParameterExtras(parameters)

        private fun Intent.putParameterExtras(parameters: WidgetActionParameters): Intent {
            val parametersPairs = parameters.asMap().map { (key, value) ->
                key.name to value
            }.toTypedArray()
            putExtra(ExtraParameters, bundleOf(*parametersPairs))
            return this
        }

        @Suppress("DEPRECATION")
        internal fun getParameterExtras(extras: Bundle): WidgetActionParameters {
            val paramsBundle = extras.getBundle(ExtraParameters)
            return widgetActionParametersOf().apply {
                paramsBundle?.keySet()?.forEach { key ->
                    set(WidgetActionParameters.Key(key), paramsBundle[key])
                }
            }
        }
    }
}

@SuppressLint("LongLogTag")
internal fun BroadcastReceiver.goAsync(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    block: suspend CoroutineScope.() -> Unit
) {
    val coroutineScope = CoroutineScope(SupervisorJob() + coroutineContext)
    val pendingResult = goAsync()

    coroutineScope.launch {
        try {
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (t: Throwable) {
                Log.e(TAG, "BroadcastReceiver execution failed", t)
            } finally {
                // Nothing can be in the `finally` block after this, as this throws a
                // `CancellationException`
                /* [NGSWP_DEBUG */
                Log.d(TAG, "Cancel parent coroutine scope")
                /* NGSWP_DEBUG] */
                coroutineScope.cancel()
            }
        } finally {
            // This must be the last call, as the process may be killed after calling this.
            try {
                Log.i(TAG, "Finish broadcast $pendingResult")
                pendingResult?.finish()
            } catch (e: IllegalStateException) {
                // On some OEM devices, this may throw an error about "Broadcast already finished".
                // See b/257513022.
                Log.e(TAG, "Error thrown when trying to finish broadcast", e)
            }
        }
    }
}

private const val TAG = "CoroutineBroadcastReceiver"

