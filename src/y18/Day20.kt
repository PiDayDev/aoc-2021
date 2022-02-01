package y18

import java.io.PrintStream
import java.lang.System.currentTimeMillis
import java.math.BigInteger
import java.math.BigInteger.ONE
import java.math.BigInteger.ZERO
import kotlin.random.Random

private const val mapFile = "Day20.map"

private data class Room(val n: Int, val e: Int) {
    fun door(room: Room) = Door(this, room)
    fun go(direction: Char): Door = when (direction) {
        'N' -> door(copy(n = n + 1))
        'S' -> door(copy(n = n - 1))
        'W' -> door(copy(e = e - 1))
        'E' -> door(copy(e = e + 1))
        else -> throw IllegalArgumentException("Direction $direction")
    }

    override fun toString() = "${n}N,${e}E"
}

private data class Door(val a: Room, val b: Room) {
    operator fun get(room: Room): Room? = when (room) {
        a -> b
        b -> a
        else -> null
    }
}

private val letters = 'A'..'Z'

private enum class Kind { AND, OR }

private sealed class X20 {
    abstract fun maxLength(): Int
    abstract fun maxDepth(): Int
    abstract fun rnd(): String
}

private class Leaf20(val path: String) : X20() {
    override fun maxLength() = path.length
    override fun maxDepth() = 0

    override fun toString() = "[$path]"
    override fun rnd() = path
}

private class Node20(
    var kind: Kind = Kind.AND,
    val level: Int = 0
) : X20() {
    val children: MutableList<X20> = mutableListOf()

    fun size(): BigInteger =
        if (kind == Kind.AND)
            children.fold(ONE) { a, it ->
                a * when (it) {
                    is Node20 -> it.size()
                    is Leaf20 -> ONE
                }
            }
        else
            children.fold(ZERO) { a, it ->
                a + when (it) {
                    is Node20 -> it.size()
                    is Leaf20 -> ONE
                }
            }

    fun consume(s: String): String {
        var rest = s
        while (rest.isNotEmpty()) {
            val leaf = rest.takeWhile { it in letters }
            children.add(Leaf20(leaf))
            rest = rest.drop(leaf.length)
            if (rest.isNotEmpty()) when (rest[0]) {
                '(' -> {
                    val end = rest.findMatchingParenthesis(0)
                    val part = rest.substring(0..end).removeSurrounding("(", ")")
                    val splitIndices = part.mapIndexedNotNull { index, c ->
                        if (c != '|') null
                        else if (!part.substring(0, index).hasBalance()) null
                        else index
                    }
                    val grandchildren = part.splitAt(splitIndices)

                    val child = Node20(Kind.OR, level + 1)
                    children += child
                    child.children += grandchildren.map { str -> Node20(level = level + 2).also { it.consume(str) } }
                    rest = rest.drop(end + 1)
                }
                '|', ')' -> check(false) { "OOPS $rest" }
            }
        }
        return rest
    }

    private val maxLen: Int by lazy {
        when (kind) {
            Kind.AND -> children.sumOf { it.maxLength() }
            Kind.OR -> children.maxOf { it.maxLength() }
        }
    }

    override fun maxLength() = maxLen

    override fun maxDepth() = 1 + (children.maxOfOrNull { it.maxDepth() } ?: 0)

    override fun rnd() = when (kind) {
        Kind.AND -> children.joinToString("") { it.rnd() }
        Kind.OR -> {
            val chLen = children.map { it.maxLength() }
            val sums = chLen.scan(0) { a, b -> a + b }.drop(1)
            val roulette = Random.Default.nextInt(sums.last())
            children[sums.indexOfFirst { it > roulette }].rnd()
        }
    }

    override fun toString() = children.joinToString(" $kind ", prefix = "(", postfix = ")")

}

private fun String.toTree(): Node20 {
    val root = Node20()
    root.consume(this)
    return root
}

private fun String.splitAt(indices: Iterable<Int>): List<String> {
    val splits = listOf(-1) + indices + listOf(length)
    return splits.zipWithNext { a, b -> a + 1 until b }.map { substring(it) }
}

private fun String.hasBalance() = count { it == ')' } == count { it == '(' }

private fun String.findMatchingParenthesis(index: Int): Int =
    when (this[index]) {
        '(' -> (index + 1 until length).filter { this[it] == ')' }.first { substring(index..it).hasBalance() }
        ')' -> (0 until index).filter { this[it] == '(' }.last { substring(it..index).hasBalance() }
        else -> throw IllegalArgumentException("Char at $index is ${this[index]}")
    }

private const val FREE_ROOM = "°"
private const val START_ROOM = "O"
private const val V_DOOR = " "
private const val H_DOOR = " "
private const val WALL = "█"

fun main() {

    fun Collection<Room>.draw(
        doors: Collection<Door> = emptyList(),
        out: PrintStream = System.out
    ) {
        val sn = map { it.n }.sorted().let { it.first()..it.last() }
        val we = map { it.e }.sorted().let { it.first()..it.last() }
        val start = Room(0, 0)
        sn.sortedDescending().forEachIndexed { j, n ->
            val curr = StringBuilder(WALL)
            val under = StringBuilder(WALL)
            we.forEach { e ->
                val room = Room(n, e)
                curr.append(if (room in this) if (room == start) START_ROOM else FREE_ROOM else "?")
                val roomE = Room(n, e + 1)
                curr.append(if (doors.any { it[room] == roomE }) V_DOOR else WALL)
                val roomS = Room(n - 1, e)
                under.append(if (doors.any { it[room] == roomS }) H_DOOR else WALL).append(WALL)
            }
            if (j == 0) out.println(WALL.repeat(under.length))
            out.println(curr)
            out.println(under)
        }
        out.println("N $sn | E $we")
    }

    fun follow(
        path: String,
        from: Room,
        doors: MutableSet<Door>,
        rooms: MutableSet<Room>
    ): Room =
        path.fold(from) { room, dir ->
            val door = room.go(dir)
            val dest = door.b
            doors += door
            rooms += dest
            dest
        }

    @Suppress("unused")
    fun drawMap(input: String): Int {
        val t0 = currentTimeMillis()
        val tree = input.removeSurrounding("^", "$").toTree()
        println("🌴🌲 TREE IS READY \uD83C\uDF33\uD83C\uDF8B | It took ${(currentTimeMillis() - t0) / 1000} seconds")
        println("TREE SIZE = ${tree.size()}")
        println("TREE LENGTH = ${tree.maxLength()}")
        println("TREE DEPTH = ${tree.maxDepth()}")

        val start = Room(0, 0)
        val rooms = mutableSetOf(start)
        val doors = mutableSetOf<Door>()

        val regex = input.toRegex()
        val t1 = currentTimeMillis()

        generateSequence(1) { it + 1 }
            .map { tree.rnd() }
            .filter { regex.matches(it) }
            .take(500_000)
            .forEachIndexed { index, it ->
                val length = it.length
                if (index % 10000 == 0) println(
                    "[$index] ${
                        length.toString().padStart(4)
                    } | ${it.take(120)}${if (length > 120) "..." else ""}"
                )
                follow(it, start, doors, rooms)
            }

        rooms.draw(doors)
        rooms.draw(doors, printStream(mapFile))

        println("Mapping these paths took ${(currentTimeMillis() - t1) / 1000} seconds")

        return input.length
    }

    fun distances(): Map<Pair<Int, Int>, Int> {
        val map = readInput(mapFile)
        val y0 = map.indexOfFirst { START_ROOM in it }
        val x0 = map[y0].indexOf(START_ROOM)

        fun Pair<Int, Int>.extend() = listOf(
            first to second - 2,
            first - 2 to second,
            first + 2 to second,
            first to second + 2,
        )

        fun isReachable(a: Pair<Int, Int>, b: Pair<Int, Int>): Boolean {
            val xDoor = (a.first + b.first) / 2
            val yDoor = (a.second + b.second) / 2
            return (map.getOrNull(yDoor)?.getOrNull(xDoor)?.toString() ?: WALL) in "$V_DOOR$H_DOOR"
        }

        return minDistances(x0 to y0) {
            it.extend().filter { next -> isReachable(it, next) }
        }
    }

    fun part1(distances: Map<Pair<Int, Int>, Int>) = distances.values.maxOf { it }
    fun part2(distances: Map<Pair<Int, Int>, Int>) = distances.count { it.value >= 1000 }

    /*
     * I couldn't figure out a decent way to use the reg.ex. ...
     * so I resorted to generating a bunch of valid paths and drawing the resulting map
       // drawMap(readInput("Day20").joinToString(""))
     * I'm not proud of this workaround, but it did its job
     */
    val distances = distances()
    println(part1(distances))
    println(part2(distances))
}
