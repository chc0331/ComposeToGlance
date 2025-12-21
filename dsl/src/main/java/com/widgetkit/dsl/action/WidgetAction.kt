package com.widgetkit.dsl.action

import android.content.Context
import androidx.core.os.bundleOf
import androidx.glance.GlanceId
import com.widgetkit.dsl.action.WidgetActionCallbackBroadcastReceiver.Companion.getParameterExtras
import java.util.Collections
import kotlin.collections.component1
import kotlin.collections.component2

interface WidgetActionCallback {
    suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: WidgetActionParameters
    )
}

class WidgetActionParameters internal constructor(
    internal val map: MutableMap<Key<out Any>, Any> = mutableMapOf()
) {

    class Key<T : Any>(val name: String) {

        infix fun to(value: T): Pair<T> = Pair(this, value)

        override fun equals(other: Any?): Boolean = other is Key<*> && name == other.name

        override fun hashCode(): Int = name.hashCode()

        override fun toString(): String = name
    }

    class Pair<T : Any> internal constructor(
        internal val key: Key<T>,
        internal val value: T
    ) {
        override fun equals(other: Any?): Boolean =
            other is Pair<*> && key == other.key && value == other.value

        override fun hashCode(): Int = key.hashCode() + value.hashCode()

        override fun toString(): String = "(${key.name}, $value)"
    }

    operator fun <T : Any> contains(key: Key<T>): Boolean = map.containsKey(key)

    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any> get(key: Key<T>): T? = map[key] as T?

    fun <T : Any> getOrDefault(key: Key<T>, defaultValue: T): T = get(key) ?: defaultValue

    fun asMap(): Map<Key<out Any>, Any> = Collections.unmodifiableMap(map)

    operator fun <T : Any> set(key: Key<T>, value: T?): T? {
        val mapValue = get(key)
        when (value) {
            null -> remove(key)
            else -> map[key] = value
        }
        return mapValue
    }

    /**
     * Removes an item from this MutableParameters.
     *
     * @param key the parameter to remove
     * @return the original value of the parameter
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> remove(key: Key<T>) = map.remove(key) as T?

    /**
     * Removes all parameters from this MutableParameters.
     */
    fun clear() = map.clear()

    override fun equals(other: Any?): Boolean =
        other is WidgetActionParameters && map == other.map

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = map.toString()

    fun isEmpty(): Boolean = map.isEmpty()
}

fun widgetActionParametersOf(
    vararg pairs: WidgetActionParameters.Pair<out Any>
): WidgetActionParameters = WidgetActionParameters(
    mutableMapOf(*pairs.map { it.key to it.value }.toTypedArray())
)

class RunWidgetCallbackAction(
    val receiverClass: Class<out WidgetActionCallbackBroadcastReceiver>,
    val callbackClass: Class<out WidgetActionCallback>,
    val parameters: WidgetActionParameters
) {
    companion object {

        suspend fun run(
            context: Context,
            className: String,
            glanceId: GlanceId,
            parameters: WidgetActionParameters
        ) {
            val workClass = Class.forName(className)

            if (!WidgetActionCallback::class.java.isAssignableFrom(workClass)) {
                error("Provided class must implement ActionCallback.")
            }

            val actionCallback =
                workClass.getDeclaredConstructor().newInstance() as WidgetActionCallback
            actionCallback.onAction(context, glanceId, parameters)
        }
    }
}

fun WidgetActionParameters.toBytes(): ByteArray =
    asMap()
        .map { (key, value) -> key.name to value }
        .toTypedArray()
        .let {
            @Suppress("DEPRECATION") // bundleOf is deprecated
            bundleOf(WidgetActionCallbackBroadcastReceiver.ExtraParameters to bundleOf(*it))
        }
        .toBytes()

fun actionParametersFromBytes(bytes: ByteArray): WidgetActionParameters =
    getParameterExtras(bundleFromBytes(bytes))