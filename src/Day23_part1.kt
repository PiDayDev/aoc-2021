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

private val memoizedPaths = mutableMapOf<Pair<Int, Int>, List<Int>>()

private fun List<Pair<Int, Int>>.fastPath(a: Int, b: Int) =
    memoizedPaths.getOrPut(a to b) { path(a, b) }

internal fun List<Pair<Int, Int>>.path(a: Int, b: Int): List<Int> {
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

internal data class Day23Cost(val valid: Boolean = true, val energy: Int = Int.MAX_VALUE)

private data class Diagram1(
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

    fun evolutions(): Map<Diagram1, Day23Cost> =
        ((0..18) - forbiddenSpots - positions.toSet())
            .flatMap {
                listOf(
                    copy(a1 = it), copy(a2 = it),
                    copy(b1 = it), copy(b2 = it),
                    copy(c1 = it), copy(c2 = it),
                    copy(d1 = it), copy(d2 = it),
                )
            }
            .filter { !it.isDeadlock() }
            .map { it to moveTo(it) }
            .filter { it.second.valid }
            .shuffled()
            .toMap()

    private fun isDeadlock(): Boolean {
        val amberA = setOf(a1, a2)
        val bronze = setOf(b1, b2)
        val copper = setOf(c1, c2)
        val desert = setOf(d1, d2)
        val c3 = 3 in copper
        val d3 = 3 in desert
        val a5 = 5 in amberA
        val d5 = 5 in desert
        val a7 = 7 in amberA
        val b7 = 7 in bronze
        return c3 && a5 || d3 && a5 || d5 && a7 || d5 && b7 || d3 && a7
    }

    fun moveTo(end: Diagram1): Day23Cost {
        val fail = Day23Cost(valid = false)
        val after = end.positions
        val differences = positions.indices.filter {
            positions[it] != after[it]
        }
        if (differences.size != 1) return fail

        val index = differences.first()
        val src = positions[index]
        val dst = after[index]
        val path = arcs.fastPath(src, dst)

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

        return Day23Cost(energy = multiplier(index) * (path.size - 1))
    }

    fun multiplier(index: Int) = when (index) {
        0, 1 -> 1
        2, 3 -> 10
        4, 5 -> 100
        6, 7 -> 1000
        else -> 999999
    }

    fun heuristicPathCost(src: Int, dst: Int) =
        (arcs.fastPath(src, dst).size - 1) * multiplier(positions.indexOf(src))

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

private class Solver1 {
    fun aStar(start: Diagram1): Long {
        val lots = 999_999_999L
        val queue = PriorityQueue<Pair<Diagram1, Long>> { a, b ->
            (a.second - b.second).sign
        }
        queue.add(start to 0L)
        val distances = mutableMapOf<Diagram1, Long>()
        val fScore = mutableMapOf<Diagram1, Long>()
        val parents = mutableMapOf<Diagram1, Diagram1>()
        distances[start] = 0L
        fScore[start] = start.lowerBound()
        var j = 0
        while (queue.isNotEmpty()) {
            val (v, result) = queue.poll()
            if (v.isFinal()) {
                return result
            } else if (j++ % 1000 == 0) {
                println("[$j] ${distances.size} distances | $result\n$v")
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
    fun List<String>.toDiagram(): Diagram1 {
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
        return Diagram1(
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
        return Solver1().aStar(diagram)
    }

    val input = readInput("Day${day}")
    println(part1(input))
}
