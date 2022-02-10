package y19

import java.math.BigInteger
import java.math.BigInteger.ONE
import java.math.BigInteger.ZERO
import java.util.*

private const val day = 22

private data class LinearExpression(val m: BigInteger, val q: BigInteger) {
    operator fun plus(b: BigInteger) = LinearExpression(m, q + b)
    operator fun minus(b: BigInteger) = LinearExpression(m, q - b)
    operator fun times(b: BigInteger) = LinearExpression(m * b, q * b)
    operator fun rem(b: BigInteger) = LinearExpression(((m % b) + b) % b, ((q % b) + b) % b)

    fun invert(modulus: BigInteger): LinearExpression {
        check(modulus.isProbablePrime(1000))
        // y = m*x+q ===> x = (y-q)*(m^-1)   (mod modulus)
        // where m^-1 is the modular multiplicative inverse of m
        val mInv = m.modInverse(modulus)
        return LinearExpression(m = mInv, q = -q * mInv) % modulus
    }

    fun evaluate(n: Int) = m * (n.toBigInteger()) + q

    fun pow(exp: BigInteger, modulus: BigInteger): LinearExpression {
        // apply once    : y = x*m+q
        // apply twice   : y = x*m^2+q*m+q
        // apply 3 times : y = x*m^3+q*m^2+q*m+q
        // ...
        // apply K times : y = x * m^K + q * SUM(m^i, i in 0..K-1)
        //               = y = x * mF + qF
        val mF = m.modPow(exp, modulus)
        val qF = q * (mF - ONE) * (m - ONE).modInverse(modulus)
        return LinearExpression(mF, qF) % modulus
    }

    override fun toString() = "$m * x + $q"
}

fun main() {

    /**
     * Convert the list of instructions into the final operation to be applied on card index
     */
    fun toLinearExpression(input: List<String>): LinearExpression {
        val rpn = input.fold(listOf<String>()) { list, shuffle ->
            val param = shuffle.substringAfterLast(" ")
            when {
                "new stack" in shuffle -> list + listOf("-1", "*", "1", "-")
                "increment" in shuffle -> list + listOf(param, "*")
                "cut" in shuffle -> list + listOf(param, "-")
                else -> throw IllegalStateException(shuffle)
            }
        }
        // apply rpn to X*1+0
        val startExpression = LinearExpression(m = ONE, q = ZERO)
        val stack = Stack<BigInteger>()
        val resultExpression = rpn.fold(startExpression) { xp, op ->
            try {
                val number = BigInteger(op)
                stack.push(number)
                check(stack.size == 1)
                xp
            } catch (nfe: NumberFormatException) {
                check(stack.size == 1)
                when (op) {
                    "+" -> (xp + stack.pop())
                    "-" -> (xp - stack.pop())
                    "*" -> (xp * stack.pop())
                    else -> xp
                }
            }
        }
        return resultExpression
    }

    fun part1(
        input: List<String>,
        size: BigInteger = 10007.toBigInteger()
    ) = toLinearExpression(input)
        .evaluate(2019) % size

    fun part2(
        input: List<String>,
        size: BigInteger = 119_315_717_514_047L.toBigInteger(),
        times: BigInteger = 101_741_582_076_661L.toBigInteger()
    ) = toLinearExpression(input)
        .invert(size)
        .pow(times, size)
        .evaluate(2020) % size

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
