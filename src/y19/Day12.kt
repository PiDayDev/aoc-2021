package y19

import kotlin.math.absoluteValue
import kotlin.math.sign

private const val day = 12

private data class XYZ(val x: Long = 0, val y: Long = 0, val z: Long = 0) {
    constructor(x: Int, y: Int, z: Int) : this(x.toLong(), y.toLong(), z.toLong())

    fun gravityTowards(other: XYZ) = (other - this).normalized()

    fun energy() = x.absoluteValue + y.absoluteValue + z.absoluteValue

    operator fun unaryMinus() = XYZ(-x, -y, -z)
    operator fun plus(other: XYZ) = XYZ(other.x + x, other.y + y, other.z + z)
    operator fun minus(other: XYZ) = plus(-other)
    fun normalized() = XYZ(x.sign, y.sign, z.sign)
}

private data class Moon(val position: XYZ, val velocity: XYZ = XYZ()) {
    fun applyGravityTowards(m: Moon): Moon {
        val deltaV = position.gravityTowards(m.position)
        return copy(velocity = velocity + deltaV)
    }

    fun move() = copy(position = position + velocity)

    fun energy() = position.energy() * velocity.energy()
}

fun main() {
    fun List<String>.toMoons() = this
        .map { row -> row.split(",").map { it.substringAfter("=").substringBefore(">").toLong() } }
        .map { (x, y, z) -> Moon(XYZ(x, y, z)) }

    fun List<Moon>.step(): List<Moon> = this
        .map { moon -> (this - moon).fold(moon) { curr, other -> curr.applyGravityTowards(other) } }
        .map { it.move() }


    fun part1(input: List<String>, steps: Int): Long =
        (1..steps).fold(input.toMoons()) { moons, _ ->
            moons.step()
        }.sumOf { it.energy() }

    fun part2(input: List<String>): Int {
        return 42
    }

    try {
        val testInput = readInput("Day${day}_test")
        check(part1(testInput, 10) == 179L)
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")
    println(part1(input, 1000))
    println(part2(input))
}
