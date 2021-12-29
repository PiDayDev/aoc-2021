private const val day = "23"

/*
 GOAL
 0 2 4 6 8 10
 |1|3|5|7|9|
#...........#10
###A#B#C#D###
  #A#B#C#D#
  #A#B#C#D#
  #A#B#C#D#
  /  / \  \
 11 13 15 17
 12 14 16 18
 21 23 25 27
 22 24 26 28
 */

private val arcs: List<Pair<Int, Int>> = (0..9).map { it to it + 1 } +
        listOf(
            2 to 11, 11 to 12, 12 to 21, 21 to 22,
            4 to 13, 13 to 14, 14 to 23, 23 to 24,
            6 to 15, 15 to 16, 16 to 25, 25 to 26,
            8 to 17, 17 to 18, 18 to 27, 27 to 28
        )

private val forbiddenSpots = setOf(2, 4, 6, 8, 19, 20)

private fun List<Int>.permutations(): List<List<Int>> = when (size) {
    0 -> emptyList()
    1 -> listOf(this)
    2 -> listOf(this, this.reversed())
    else -> distinct().flatMap { element ->
        (this - element).permutations().map { it + element }
    }
}

private val memoizedPaths = mutableMapOf<Pair<Int, Int>, List<Int>>()

private fun List<Pair<Int, Int>>.fastPath(a: Int, b: Int) =
    memoizedPaths.getOrPut(a to b) { path(a, b) }

private class Diagram2(
    // AAAABBBBCCCCDDDD
    val positions: List<Int>,
    val cost: Int = 0
) : Comparable<Diagram2> {
    private val aRoom = listOf(11, 12, 21, 22)
    private val bRoom = listOf(13, 14, 23, 24)
    private val cRoom = listOf(15, 16, 25, 26)
    private val dRoom = listOf(17, 18, 27, 28)

    private val aIndices = 0 until 4
    private val bIndices = 4 until 8
    private val cIndices = 8 until 12
    private val dIndices = 12 until 16

    private val amberA = positions.slice(aIndices)
    private val bronze = positions.slice(bIndices)
    private val copper = positions.slice(cIndices)
    private val desert = positions.slice(dIndices)

    fun isFinal() = positions.distinct().size == 16 &&
            aRoom.toSet() == amberA.toSet() &&
            bRoom.toSet() == bronze.toSet() &&
            cRoom.toSet() == copper.toSet() &&
            dRoom.toSet() == desert.toSet()

    fun evolutions(): List<Diagram2> =
        ((0..28) - forbiddenSpots - positions.toSet())
            .asSequence()
            .flatMap {
                positions.indices.map { i ->
                    val list = positions.toMutableList()
                    list[i] = it
                    list
                }
            }
            .map { Diagram2(it) }
            .filter { !it.isDeadlock() }
            .map { it to moveTo(it) }
            .filter { (_, c) -> c.valid }
            .map { (d, c) -> Diagram2(d.positions, c.energy) }
            .toList()

    private fun isDeadlock(): Boolean {
        val c3 = 3 in copper
        val d3 = 3 in desert
        val a5 = 5 in amberA
        val d5 = 5 in desert
        val a7 = 7 in amberA
        val b7 = 7 in bronze
        return c3 && a5 || d3 && a5 || d5 && a7 || d5 && b7 || d3 && a7
    }

    fun moveTo(end: Diagram2): Day23Cost {
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
            if (index in aIndices && dst !in aRoom) return fail
            if (index in bIndices && dst !in bRoom) return fail
            if (index in cIndices && dst !in cRoom) return fail
            if (index in dIndices && dst !in dRoom) return fail
        }

        // does it move into wrongly occupied room?
        if (index in aIndices && dst in aRoom
            && positions.filterIndexed { j, _ -> j !in aIndices }.any { it in aRoom }
        ) return fail
        if (index in bIndices && dst in bRoom
            && positions.filterIndexed { j, _ -> j !in bIndices }.any { it in bRoom }
        ) return fail
        if (index in cIndices && dst in cRoom
            && positions.filterIndexed { j, _ -> j !in cIndices }.any { it in cRoom }
        ) return fail
        if (index in dIndices && dst in dRoom
            && positions.filterIndexed { j, _ -> j !in dIndices }.any { it in dRoom }
        ) return fail

        // does it move to forbidden spot?
        if (positions.any { it in forbiddenSpots }) return fail

        return Day23Cost(energy = multiplier(index) * (path.size - 1))
    }

    fun multiplier(index: Int) = when (index) {
        in aIndices -> 1
        in bIndices -> 10
        in cIndices -> 100
        in dIndices -> 1000
        else -> 999999
    }

    fun heuristicPathCost(src: Int, dst: Int): Int {
        val multiplier = multiplier(positions.indexOf(src))
        val pathLength = arcs.fastPath(src, dst).size - 1
        val actualLength =
            if (pathLength > 0) pathLength
            else outOfMyWay(src)
        return multiplier * actualLength
    }

    private fun outOfMyWay(src: Int): Int {
        val under = sequenceOf(aRoom, bRoom, cRoom, dRoom)
            .firstOrNull { src in it }
            ?.takeLastWhile { it != src }
            ?: return 0
        return if (hasIntruders(under)) 7 - under.size else 0

    }

    private fun hasIntruders(locations: Collection<Int>): Boolean {
        fun isWrong(location: Int): Boolean =
            when (location) {
                in aRoom -> location in positions - amberA.toSet()
                in bRoom -> location in positions - bronze.toSet()
                in cRoom -> location in positions - copper.toSet()
                in dRoom -> location in positions - desert.toSet()
                else -> false
            }

        return locations.any { isWrong(it) }
    }

    private fun comboCost(room: List<Int>, indices: List<Int>): Int {
        val (x1, x2, x3, x4) = indices
        val (r1, r2, r3, r4) = room
        return 0 +
                heuristicPathCost(x1, r1) +
                heuristicPathCost(x2, r2) +
                heuristicPathCost(x3, r3) +
                heuristicPathCost(x4, r4)
    }

    fun lowerBound(): Int {
        val a = aRoom.permutations().minOf { comboCost(it, amberA) }
        val b = bRoom.permutations().minOf { comboCost(it, bronze) }
        val c = cRoom.permutations().minOf { comboCost(it, copper) }
        val d = dRoom.permutations().minOf { comboCost(it, desert) }
        return a + b + c + d
    }

    override fun toString(): String {
        val e = MutableList(29) { "." }
        amberA.forEach { e[it] = "A" }
        bronze.forEach { e[it] = "B" }
        copper.forEach { e[it] = "C" }
        desert.forEach { e[it] = "D" }
        return """
          ${e.take(11).joinToString("")} 
            ${e[11]} ${e[13]} ${e[15]} ${e[17]}   
            ${e[12]} ${e[14]} ${e[16]} ${e[18]} 
            ${e[21]} ${e[23]} ${e[25]} ${e[27]}   
            ${e[22]} ${e[24]} ${e[26]} ${e[28]} 
        """.trimIndent()
    }

    override fun compareTo(other: Diagram2) = cost.compareTo(other.cost)

    override fun equals(other: Any?) = when {
        this === other -> true
        javaClass != other?.javaClass -> false
        positions != (other as Diagram2).positions -> false
        else -> true
    }

    override fun hashCode() = positions.hashCode()
}

private class Solver2 {
    fun aStar(start: Diagram2): Int {
        var j = 0
        val lots = 999_999_999
        val queue: PriorityQ<Diagram2> = MyQ()
        queue.add(start)
        val distances = mutableMapOf<Diagram2, Int>()
        val fScore = mutableMapOf<Diagram2, Int>()
        val parents = mutableMapOf<Diagram2, Diagram2>()
        distances[start] = 0
        fScore[start] = start.lowerBound()
        while (queue.isNotEmpty()) {
            val v = queue.top()!!
            if (v.isFinal()) {
                return v.cost
            } else if (j++ % 2000 == 0) {
                println("[$j] ${distances.size} distances | ${v.cost}\n$v")
            }
            val neighbors = v.evolutions()
            val distV = distances[v]!!
            for (neighbor in neighbors) {
                val costFromV = distV + neighbor.cost
                val candidate = Diagram2(neighbor.positions, costFromV)
                if ((distances[candidate] ?: lots) > costFromV) {
                    distances[candidate] = costFromV
                    val fu = costFromV + candidate.lowerBound()
                    fScore[candidate] = fu
                    parents[candidate] = v
                    val updated = Diagram2(candidate.positions, fu)
                    queue.add(updated) || queue.updatePriority(candidate, updated)
                }
            }
        }
        return lots
    }

}


fun main() {
    fun List<String>.indicesOf(s: String): List<Int> {
        val j1 = lastIndexOf(s)
        val j2 = take(j1).lastIndexOf(s)
        val j3 = take(j2).lastIndexOf(s)
        val j4 = take(j3).lastIndexOf(s)
        return listOf(j1, j2, j3, j4)
    }

    fun List<String>.toDiagram(): Diagram2 {
        val them: List<String> = emptyList<String>() +
                this[2].split("#").filter { it.isNotBlank() } +
                "#D#C#B#A#".split("#").filter { it.isNotBlank() } +
                "#D#B#A#C#".split("#").filter { it.isNotBlank() } +
                this[3].split("#").filter { it.isNotBlank() } +
                ""

        val positions = listOf(
            11, 13, 15, 17,
            12, 14, 16, 18,
            21, 23, 25, 27,
            22, 24, 26, 28,
        )
        val list = them.indicesOf("A") + them.indicesOf("B") +
                them.indicesOf("C") + them.indicesOf("D")
        return Diagram2(list.map { positions[it] })
    }

    fun part2(input: List<String>): Int {
        val diagram = input.toDiagram()
        println(diagram)
        return Solver2().aStar(diagram)
    }

    val input = readInput("Day${day}")
    println(part2(input))
}
