import java.util.*
import kotlin.math.min
import kotlin.math.sign

private const val day = "23"

/*
 0123456789
#############
#...........#10
###A#B#C#D###
  #A#B#C#D#
  #########
 11 13 15 17
 12 14 16 18
 */

private val arcs: List<Pair<Int, Int>> = (0..9).map { it to it + 1 } +
        listOf(
            2 to 11, 11 to 12, 4 to 13, 13 to 14,
            6 to 15, 15 to 16, 8 to 17, 17 to 18
        )

private val forbiddenSpots = setOf(2, 4, 6, 8)

private fun List<Pair<Int, Int>>.path(a: Int, b: Int): List<Int> {
    if (a == b) return listOf(a)
    var paths: List<List<Int>> = listOf(listOf(a))
    var ok: List<Int>? = null
    while (ok == null) {
        paths = paths.flatMap { p ->
            successors(p.last()).map { p + it }
        }
        ok = paths.firstOrNull { b in it }
    }
    return ok
}

private fun List<Pair<Int, Int>>.successors(a: Int) = reachableNodes(a).distinct()

private fun List<Pair<Int, Int>>.reachableNodes(from: Int) =
    filter { it.first == from }.map { it.second } +
            filter { it.second == from }.map { it.first }

private data class Cost(val valid: Boolean = true, val energy: Long = Long.MAX_VALUE)

private data class Diagram(
    val a1: Int, val a2: Int,
    val b1: Int, val b2: Int,
    val c1: Int, val c2: Int,
    val d1: Int, val d2: Int,
) {
    val positions
        get() = listOf(a1, a2, b1, b2, c1, c2, d1, d2)

    fun isFinal() = positions.distinct().size == 8 &&
            setOf(a1, a2) == setOf(11, 12) &&
            setOf(b1, b2) == setOf(13, 14) &&
            setOf(c1, c2) == setOf(15, 16) &&
            setOf(d1, d2) == setOf(17, 18)

    fun evolutions(): Map<Diagram, Cost> =
        ((0..18) - forbiddenSpots - positions.toSet())
            .flatMap {
                listOf(
                    copy(a1 = it), copy(a2 = it),
                    copy(b1 = it), copy(b2 = it),
                    copy(c1 = it), copy(c2 = it),
                    copy(d1 = it), copy(d2 = it),
                )
            }
            .map { it to moveTo(it) }
            .filter { it.second.valid }
            .shuffled()
            .toMap()

    fun moveTo(end: Diagram): Cost {
        val fail = Cost(valid = false)
        val after = end.positions
        val differences = positions.indices.filter {
            positions[it] != after[it]
        }
        if (differences.size != 1) return fail

        val index = differences.first()
        val src = positions[index]
        val dst = after[index]
        val path = arcs.path(src, dst)

        // does it cross any other amphipod ?
        val rest = positions.filterIndexed { j, _ -> index != j }
        if (rest.any { it in path }) return fail

        // does it move from hallway into hallway?
        if (src <= 10 && dst <= 10) return fail

        // does it move into wrong room?
        if (dst > 10) {
            if (index in (0..1) && dst !in (11..12)) return fail
            if (index in (2..3) && dst !in (13..14)) return fail
            if (index in (4..5) && dst !in (15..16)) return fail
            if (index in (6..7) && dst !in (17..18)) return fail
        }

        // does it move into wrongly occupied room?
        if (index in (0..1) && dst in (11..12)
            && positions.filterIndexed { j, _ -> j !in 0..1 }.any { it in 11..12 }
        ) return fail
        if (index in (2..3) && dst in (13..14)
            && positions.filterIndexed { j, _ -> j !in 2..3 }.any { it in 13..14 }
        ) return fail
        if (index in (4..5) && dst in (15..16)
            && positions.filterIndexed { j, _ -> j !in 4..5 }.any { it in 15..16 }
        ) return fail
        if (index in (6..7) && dst in (17..18)
            && positions.filterIndexed { j, _ -> j !in 6..7 }.any { it in 17..18 }
        ) return fail

        // does it move to forbidden spot?
        if (positions.any { it in forbiddenSpots }) return fail

        return Cost(energy = multiplier(index).toLong() * (path.size - 1))
    }

    fun multiplier(index: Int) = when (index) {
        0, 1 -> 1
        2, 3 -> 10
        4, 5 -> 100
        6, 7 -> 1000
        else -> 999999
    }

    fun heuristicPathCost(src: Int, dst: Int) =
        (arcs.path(src, dst).size - 1) * multiplier(positions.indexOf(src))

    fun lowerBound(): Long {
        val a = min(
            heuristicPathCost(a1, 11) + heuristicPathCost(a2, 12),
            heuristicPathCost(a1, 12) + heuristicPathCost(a2, 11),
        )
        val b = min(
            heuristicPathCost(b1, 13) + heuristicPathCost(b2, 14),
            heuristicPathCost(b1, 14) + heuristicPathCost(b2, 13),
        )
        val c = min(
            heuristicPathCost(c1, 15) + heuristicPathCost(c2, 16),
            heuristicPathCost(c1, 16) + heuristicPathCost(c2, 15),
        )
        val d = min(
            heuristicPathCost(d1, 17) + heuristicPathCost(d2, 18),
            heuristicPathCost(d1, 18) + heuristicPathCost(d2, 17),
        )
        return (d + c + b + a).toLong()
    }

    override fun toString(): String {
        val e = MutableList(19) { "." }
        e[a1] = "A"; e[a2] = "A"
        e[b1] = "B"; e[b2] = "B"
        e[c1] = "C"; e[c2] = "C"
        e[d1] = "D"; e[d2] = "D"
        return """
          ${e.take(11).joinToString("")} 
            ${e[11]} ${e[13]} ${e[15]} ${e[17]}   
            ${e[12]} ${e[14]} ${e[16]} ${e[18]} 
        """.trimIndent()
    }
}

private class Solver {
    fun aStar(start: Diagram): Long {
        val lots = 999_999_999L
        val queue = PriorityQueue<Pair<Diagram, Long>> { a, b ->
            (a.second - b.second).sign
        }
        queue.add(start to 0L)
        val distances = mutableMapOf<Diagram, Long>()
        val fScore = mutableMapOf<Diagram, Long>()
        val parents = mutableMapOf<Diagram, Diagram>()
        distances[start] = 0L
        fScore[start] = start.lowerBound()
        while (queue.isNotEmpty()) {
            val (v, result) = queue.poll()
            if (v.isFinal()) {
                return result
            }
            val neighbors = v.evolutions()
            for ((u, cost) in neighbors) {
                val costFromV = distances[v]!! + cost.energy
                if ((distances[u] ?: lots) > costFromV) {
                    distances[u] = costFromV
                    val fu = costFromV + u.lowerBound()
                    fScore[u] = fu
                    parents[u] = v
                    queue.removeIf { it.first == u }
                    queue.add(u to fu)
                }
            }
        }
        return lots
    }


}


fun main() {
    fun List<String>.toDiagram(): Diagram {
        val them = this[2].split("#").filter { it.isNotBlank() } +
                this[3].split("#").filter { it.isNotBlank() }

        val positions = listOf(11, 13, 15, 17, 12, 14, 16, 18)
        val a1 = them.indexOf("A")
        val a2 = them.lastIndexOf("A")
        val b1 = them.indexOf("B")
        val b2 = them.lastIndexOf("B")
        val c1 = them.indexOf("C")
        val c2 = them.lastIndexOf("C")
        val d1 = them.indexOf("D")
        val d2 = them.lastIndexOf("D")
        return Diagram(
            a1 = positions[a1],
            a2 = positions[a2],
            b1 = positions[b1],
            b2 = positions[b2],
            c1 = positions[c1],
            c2 = positions[c2],
            d1 = positions[d1],
            d2 = positions[d2],
        )
    }

    fun part1(input: List<String>): Long {
        val diagram = input.toDiagram()
        println(diagram)
        return Solver().aStar(diagram)
    }

    // test if implementation meets criteria from the description, like:
    try {
        val testInput = readInput("Day${day}_test")
        val test1 = part1(testInput)
        check(test1 == 12521L) { "Test 1 solved in $test1" }
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")
    println(part1(input))
}
