package y19

import java.util.concurrent.atomic.AtomicReference
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

    tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)
    fun lcm(a: Long, b: Long) = a * b / gcd(a, b)

    fun part2(input: List<String>, log: Boolean = false): Long {
        val initialMoons = input.toMoons()
        val lastStatus = AtomicReference(XYZ(0, 0, 0))
        val xProjections = mutableSetOf<List<Pair<Long, Long>>>()
        val yProjections = mutableSetOf<List<Pair<Long, Long>>>()
        val zProjections = mutableSetOf<List<Pair<Long, Long>>>()
        generateSequence(1) { it + 1 }.fold(initialMoons) { moons, n ->
            xProjections += moons.map { it.position.x to it.velocity.x }
            yProjections += moons.map { it.position.y to it.velocity.y }
            zProjections += moons.map { it.position.z to it.velocity.z }
            val combo = XYZ(xProjections.size, yProjections.size, zProjections.size)
            if (lastStatus.getAndSet(combo) == combo) {
                if (log) println("$n steps => $combo")
                return lcm(combo.x, lcm(combo.y, combo.z))
            }
            moons.step()
        }
        return -1
    }

    try {
        val testInput = readInput("Day${day}_test")
        check(part1(testInput, 10) == 179L)
        check(part2(testInput) == 2772L)
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")
    println(part1(input, 1000))
    println(part2(input, true))
}
