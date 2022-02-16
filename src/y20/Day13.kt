package y20

import java.math.BigInteger

private const val day = "13"

private val Long.b: BigInteger
    get() = toBigInteger()
private val Int.b: BigInteger
    get() = toBigInteger()

fun main() {
    fun part1(input: List<String>): Int {
        val timestamp = input.first().toInt()
        val bus = input.last().split(",").filter { it != "x" }.map { it.toInt() }
        return generateSequence(1) { it + 1 }
            .firstNotNullOf { dt ->
                val valid = bus.filter { b -> (timestamp + dt) % b == 0 }
                if (valid.isNotEmpty()) valid.first() * dt else null
            }
    }

    fun part2(input: List<String>): Long {
        val bus = input.last().split(",")
            .mapIndexedNotNull { index, s ->
                if (s == "x") null
                else index.b to s.toLong().b
            }

        // each step combines two equations into one:
        //     x%A == B            &  x%C == D
        // ==> x == n*A+B [mod A]  &  x%C == D
        // ==> n*A+B == D [mod C]
        // ==> n = (D-B)*(A^-1) [mod C]
        // ==> x = B + A * n
        val result = bus.reduce { eq1, eq2 ->
            val (b, a) = eq1
            val (d, c) = eq2
            val n = ((d - b) * a.modInverse(c) % c + c) % c
            val divisor = a * c
            val rest = b + a * n
            (rest % divisor + divisor) % divisor to divisor
        }

        return (result.second - result.first).toLong()
    }

    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 370)
    val p2 = part2(input)
    println(p2)
    check(p2 == 894954360381385L)
}
