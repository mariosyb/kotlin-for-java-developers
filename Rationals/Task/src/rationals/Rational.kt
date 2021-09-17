package rationals

import java.math.BigInteger
import java.math.BigInteger.ONE
import java.math.BigInteger.ZERO

class Rational(val n: BigInteger, val d: BigInteger) : Comparable<Rational> { // al implementar comparable tambien se implementa el uso de Range(in)
    val numerator: BigInteger
    val denominator: BigInteger

    init {
        // normalizar
        require(d != ZERO) { "denominator cloud not be zero!!" }
        val g = n.gcd(d)
        val sign = d.signum().toBigInteger()
        // multiplicar por el signo
        numerator = n / g * sign
        denominator = d / g * sign
    }

    /*
    operator functions
     */
    operator fun unaryMinus(): Rational = Rational(-this.numerator, this.denominator)

    operator fun plus(other: Rational): Rational = Rational(
        (this.numerator * other.denominator) + (other.numerator * this.denominator),
        this.denominator * other.denominator
    )

    operator fun minus(other: Rational): Rational = Rational(
        (this.numerator * other.denominator) - (other.numerator * this.denominator),
        this.denominator * other.denominator
    )

    operator fun times(other: Rational): Rational = Rational(
        this.numerator * other.numerator,
        this.denominator * other.denominator
    )

    operator fun div(other: Rational): Rational = Rational(
        this.numerator * other.denominator,
        this.denominator * other.numerator
    )

    /*
      compareTo
     */
    override fun compareTo(other: Rational): Int {
        return (this.numerator * other.denominator - other.numerator * this.denominator).signum()
    }


    /**
     * equals hash y toStr
     */
    override fun toString(): String {
        return if (denominator == 1.toBigInteger())
            "$numerator"
        else
            "$numerator/$denominator"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rational

        if (numerator != other.numerator) return false
        if (denominator != other.denominator) return false

        return true
    }

    override fun hashCode(): Int {
        var result = numerator.hashCode()
        result = 31 * result + denominator.hashCode()
        return result
    }
}

infix fun Int.divBy(denominator: Int) =
    Rational(this.toBigInteger(), denominator.toBigInteger())

infix fun Long.divBy(denominator: Long) =
    Rational(this.toBigInteger(), denominator.toBigInteger())

infix fun BigInteger.divBy(denominator: BigInteger) =
    Rational(this, denominator)

fun String.toRational(): Rational {
    if (!contains("/")) { // this se puede omitir
        return Rational(toBigInteger(), ONE) // se omite this de nuevo
    }

    // se omite el this y ademas de usa destructuring
    // para asignar cada uno de los elementos del arreglo o lista
    // a las variables del lado izq, en el mismo orden que estan en la lista
    // en este caso la primero a numerator y la segunda a denominator etc..
    val (numerator, denominator) = split("/")

    return Rational(numerator.toBigInteger(), denominator.toBigInteger())
}

fun main() {
    val half = 1 divBy 2
    val third = 1 divBy 3

    val sum: Rational = half + third
    println(5 divBy 6 == sum)

    val difference: Rational = half - third
    println(1 divBy 6 == difference)

    val product: Rational = half * third
    println(1 divBy 6 == product)

    val quotient: Rational = half / third
    println(3 divBy 2 == quotient)

    val negation: Rational = -half
    println(-1 divBy 2 == negation)

    println((2 divBy 1).toString() == "2")

    println((-2 divBy 4).toString() == "-1/2")

    println("117/1098".toRational().toString() == "13/122")

    val twoThirds = 2 divBy 3
    println(half < twoThirds)

    println(half in third..twoThirds)

    println(2000000000L divBy 4000000000L == 1 divBy 2)

    println(
        "912016490186296920119201192141970416029".toBigInteger() divBy
                "1824032980372593840238402384283940832058".toBigInteger() == 1 divBy 2
    )
}

