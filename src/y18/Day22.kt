package y18

import y18.Equipment.*

private const val depth = 10914
private val target = Pos22(9, 739)
private val targetNode = Node22(target, TORCH)

private enum class RegionType(val riskLevel: Int, val symbol: Char) {
    ROCKY(0, '.'),
    WET(1, '='),
    NARROW(2, '|');

    operator fun contains(e: Equipment): Boolean = e != when (this) {
        ROCKY -> NONE
        WET -> TORCH
        NARROW -> CLIMB
    }
}

private enum class Equipment { TORCH, CLIMB, NONE; }

private data class Pos22(val x: Int, val y: Int) {
    companion object {
        val geoCache = mutableMapOf(Pos22(0, 0) to 0L)
    }

    fun regionType(): RegionType =
        if (this == target) RegionType.ROCKY
        else when (erosionLevel() % 3L) {
            0L -> RegionType.ROCKY
            1L -> RegionType.WET
            else -> RegionType.NARROW
        }

    fun erosionLevel(): Long =
        (geologicIndex() + depth) % 20183L

    fun geologicIndex(): Long {
        val result = geoCache[this] ?: when {
            y == 0 -> x * 16807L
            x == 0 -> y * 48271L
            else -> Pos22(x - 1, y).erosionLevel() * Pos22(x, y - 1).erosionLevel()
        }
        geoCache[this] = result
        return result
    }

    operator fun rangeTo(p: Pos22) = (x..p.x).flatMap { x1 ->
        (y..p.y).map { y1 ->
            Pos22(x1, y1)
        }
    }

    fun neighbors(): List<Pos22> =
        listOf(copy(x = x + 1), copy(x = x - 1), copy(y = y + 1), copy(y = y - 1))
            .filter { it.x >= 0 && it.y >= 0 }
}

private data class Node22(val pos: Pos22, val equipment: Equipment) {
    fun destinations(): Map<Node22, Int> {
        val neighbors = pos.neighbors()
        val myRegion = pos.regionType()
        return neighbors.flatMap {
            val curr = mutableMapOf<Node22, Int>()
            val destRegion = it.regionType()
            for (e in Equipment.values()) {
                if (e in myRegion && e in destRegion) {
                    curr[Node22(it, e)] = if (e == equipment) 1 else 8
                }
            }
            curr.toList()
        }.toMap()
    }
}

fun main() {
    fun List<Pos22>.print() {
        associateWith { it.regionType() }.let { map ->
            for (y in 0..target.y) {
                for (x in 0..target.x) print(map[Pos22(x, y)]?.symbol ?: '#')
                println()
            }
        }
    }

    fun part1(): Int {
        val area = Pos22(0, 0)..target
        area.print()
        return area.sumOf { it.regionType().riskLevel } - target.regionType().riskLevel
    }

    fun part2(): Int {
        val start = Node22(Pos22(0, 0), TORCH)
        val todo = mutableMapOf(start to 0)
        val reached = mutableMapOf<Node22, Int>()
        while (todo.isNotEmpty() && targetNode !in reached.keys) {
            val minCost = todo.values.minOf { it }
            val candidates = todo.filterValues { it == minCost }
            todo -= candidates.keys
            reached += candidates
            candidates.toList()
                .flatMap { (node, pathCost) ->
                    node.destinations().toList().map { (dest, cost) -> dest to cost + pathCost }
                }.forEach { (node, pathCost) ->
                    if (node !in reached.keys && (todo[node] ?: Int.MAX_VALUE) > pathCost)
                        todo[node] = pathCost
                }
        }
        return reached[targetNode]!!
    }

    println(part1())
    println(part2())
}
