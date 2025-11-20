import androidx.compose.runtime.Stable

@JvmDefaultWithCompatibility
/**
 * An ordered, immutable, collection of modifier element for the Proto library.
 *
 * This plays the same role as [androidx.compose.ui.Modifier], but for the Proto composables.
 */
@Stable
interface ProtoModifier {
    /**
     * Accumulates a value starting with [initial] and applying [operation] to the current value and
     * each element from outside in.
     *
     * Elements wrap one another in a chain from left to right; an [Element] that appears to the
     * left of another in a `+` expression or in [operation]'s parameter order affects all of the
     * elements that appear after it. [foldIn] may be used to accumulate a value starting from the
     * parent or head of the modifier chain to the final wrapped child.
     */
    fun <R> foldIn(initial: R, operation: (R, Element) -> R): R

    /**
     * Accumulates a value starting with [initial] and applying [operation] to the current value and
     * each element from inside out.
     *
     * Elements wrap one another in a chain from left to right; an [Element] that appears to the
     * left of another in a `+` expression or in [operation]'s parameter order affects all of the
     * elements that appear after it. [foldOut] may be used to accumulate a value starting from the
     * child or tail of the modifier chain up to the parent or head of the chain.
     */
    fun <R> foldOut(initial: R, operation: (Element, R) -> R): R

    /** Returns `true` if [predicate] returns true for any [Element] in this [ProtoModifier]. */
    fun any(predicate: (Element) -> Boolean): Boolean

    /**
     * Returns `true` if [predicate] returns true for all [Element]s in this [ProtoModifier] or if
     * this [ProtoModifier] contains no [Element]s.
     */
    fun all(predicate: (Element) -> Boolean): Boolean

    /**
     * Concatenates this modifier with another.
     *
     * Returns a [ProtoModifier] representing this modifier followed by [other] in sequence.
     */
    infix fun then(other: ProtoModifier): ProtoModifier =
        if (other === ProtoModifier) this else CombinedProtoModifier(this, other)

    @JvmDefaultWithCompatibility
    /** A single element contained within a [ProtoModifier] chain. */
    interface Element : ProtoModifier {
        override fun <R> foldIn(initial: R, operation: (R, Element) -> R): R =
            operation(initial, this)

        override fun <R> foldOut(initial: R, operation: (Element, R) -> R): R =
            operation(this, initial)

        override fun any(predicate: (Element) -> Boolean): Boolean = predicate(this)

        override fun all(predicate: (Element) -> Boolean): Boolean = predicate(this)
    }

    /**
     * The companion object `Modifier` is the empty, default, or starter [GlanceModifier] that
     * contains no [elements][Element]. Use it to create a new [GlanceModifier] using modifier
     * extension factory functions.
     */
    // The companion object implements `Modifier` so that it may be used  as the start of a
    // modifier extension factory expression.
    companion object : ProtoModifier {
        override fun <R> foldIn(initial: R, operation: (R, Element) -> R): R = initial

        override fun <R> foldOut(initial: R, operation: (Element, R) -> R): R = initial

        override fun any(predicate: (Element) -> Boolean): Boolean = false

        override fun all(predicate: (Element) -> Boolean): Boolean = true

        override infix fun then(other: ProtoModifier): ProtoModifier = other

        override fun toString(): String = "ProtoModifier"
    }
}

/**
 * A node in a [ProtoModifier] chain. A CombinedModifier always contains at least two elements; a
 * Modifier [outer] that wraps around the Modifier [inner].
 */
class CombinedProtoModifier(
    private val outer: ProtoModifier,
    private val inner: ProtoModifier,
) : ProtoModifier {
    override fun <R> foldIn(initial: R, operation: (R, ProtoModifier.Element) -> R): R =
        inner.foldIn(outer.foldIn(initial, operation), operation)

    override fun <R> foldOut(initial: R, operation: (ProtoModifier.Element, R) -> R): R =
        outer.foldOut(inner.foldOut(initial, operation), operation)

    override fun any(predicate: (ProtoModifier.Element) -> Boolean): Boolean =
        outer.any(predicate) || inner.any(predicate)

    override fun all(predicate: (ProtoModifier.Element) -> Boolean): Boolean =
        outer.all(predicate) && inner.all(predicate)

    override fun equals(other: Any?): Boolean =
        other is CombinedProtoModifier && outer == other.outer && inner == other.inner

    override fun hashCode(): Int = outer.hashCode() + 31 * inner.hashCode()

    override fun toString(): String =
        "[" +
                foldIn("") { acc, element ->
                    if (acc.isEmpty()) element.toString() else "$acc, $element"
                } +
                "]"
}