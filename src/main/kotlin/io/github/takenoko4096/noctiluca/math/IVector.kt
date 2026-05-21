package io.github.takenoko4096.noctiluca.math

interface IVector<T : IVector<T, U>, U : Number> {
    val isZero: Boolean

    val components: List<U>

    override fun hashCode(): Int

    override operator fun equals(other: Any?): Boolean

    @Destructive
    fun calculate(operator: (U) -> U): T

    @Destructive
    fun calculate(other: T, operator: (U, U) -> U): T

    @Destructive
    fun calculate(other1: T, other2: T, operator: (U, U, U) -> U): T

    @Destructive
    infix fun add(other: T): T

    @Destructive
    infix fun subtract(other: T): T

    @Destructive
    infix fun scale(scalar: U): T

    @Destructive
    infix fun divide(scalar: U): T

    @Destructive
    fun invert(): T

    @Destructive
    fun clamp(min: T, max: T): T

    fun format(pattern: String, digits: Int): String

    override fun toString(): String

    fun copy(): T

    operator fun plus(other: T): T = copy().add(other)

    operator fun minus(other: T): T = copy().subtract(other)

    operator fun unaryMinus(): T = copy().invert()

    operator fun times(scale: U): T = copy().scale(scale)

    operator fun div(divider: U): T = copy().divide(divider)

    operator fun get(index: Int): U = components[index]
}
