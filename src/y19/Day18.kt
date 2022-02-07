package y19

import kotlin.math.min

private const val day = 18

private operator fun String.minus(c: Char) = first { it != c }

private const val ENTRANCE = '@'
private const val SPACE = '.'
private const val WALL = '#'
private const val newVaults = "╔╗╚╝"

private fun neighbors(c: Char, map: Map<String, Int>): Map<Char, Int> =
    map.filter { c in it.key }.mapKeys { it.key - c }

private fun hash(path: String) = "${path.toSortedSet().toList()}->${path.last()}"

fun main() {
    fun Map<Pair<Int, Int>, Char>.neighbors(pos: Pair<Int, Int>): List<Pair<Int, Int>> {
        val c = this[pos]
        val (x, y) = pos
        return when (c) {
            '.' -> listOf(x + 1 to y, x - 1 to y, x to y - 1, x to y + 1).filter { it in this }
            else -> emptyList()
        }
    }


    fun List<String>.toGridAndPointsOfInterest() = this
        .flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, c ->
                if (c != WALL) (x to y) to c else null
            }
        }
        .toMap()
        .let { grid ->
            val poi = grid.filterValues { it != SPACE }.keys
            grid to poi
        }

    fun part1(input: List<String>): Int {
        val (grid, poi: Set<Pair<Int, Int>>) = input.toGridAndPointsOfInterest()

        val allDistances: Map<Pair<Pair<Int, Int>, Pair<Int, Int>>, Int> = poi
            .associateWith { start: Pair<Int, Int> ->
                minDistances(start) { (grid + (start to SPACE)).neighbors(it) }.filter { (k, _) -> k in poi - start }
            }
            .toList()
            .flatMap { (from, tos) -> tos.map { (k, v) -> (from to k) to v } }
            .toMap()

        val arcs: Map<String, Int> = allDistances.map { (x, cost) ->
            val (a, b) = x
            "${grid[a]}${grid[b]}".toSortedSet().joinToString("") to cost
        }.toMap()

        val nodes: List<Char> = arcs.keys.flatMap { it.toList() }.distinct()
        val keys = nodes.filter { it in 'a'..'z' }

        var candidates = mapOf(ENTRANCE.toString() to 0)
        val hashToCost = candidates.mapKeys { (k, _) -> hash(k) }.toMutableMap()
        val hashToPath = candidates.keys.associateBy { hash(it) }.toMutableMap()
        var j = 0
        while (candidates.isNotEmpty()) {
            val nextStates: List<Pair<String, Int>> = candidates.flatMap { (path, cost) ->
                val tail = path.last()
                val availableSteps = neighbors(tail, arcs).filterKeys { it !in 'A'..'Z' || it.lowercaseChar() in path }
                availableSteps.map { (node, dist) -> path + node to cost + dist }
            }
            candidates = nextStates
                .groupBy { (path) -> hash(path) }
                .mapNotNull { (h, equivalentStates) ->
                    val currentBest: Pair<String, Int>? = equivalentStates.minByOrNull { (_, cost) -> cost }
                    when {
                        currentBest == null -> null
                        currentBest.second >= (hashToCost[h] ?: Int.MAX_VALUE) -> null
                        else -> currentBest
                    }
                }
                .toMap()
            candidates.forEach { (path, cost) ->
                val h = hash(path)
                hashToCost[h] = cost
                hashToPath[h] = path
            }
            println("| ${++j} > ${candidates.size} <")
        }

        return hashToCost.filter { (h) -> h.toSet().containsAll(keys) }.minOf { (_, cost) -> cost }
    }

    fun List<String>.withFourVaults(symbols: String): List<String> {
        val y = indexOfFirst { ENTRANCE in it }
        val row = get(y)
        val x = row.indexOf(ENTRANCE)
        val (nw, ne, sw, se) = symbols.toList()
        return mapIndexed { r, s ->
            val pre = s.take(x - 1)
            val post = s.drop(x + 2)
            when (r) {
                y - 1 -> "$pre$nw$WALL$ne$post"
                y -> "$pre$WALL$WALL$WALL$post"
                y + 1 -> "$pre$sw$WALL$se$post"
                else -> s
            }
        }
    }

    fun part2(input: List<String>): Int {
        val actualGrid = input.withFourVaults(newVaults)
        val (grid, poi: Set<Pair<Int, Int>>) = actualGrid.toGridAndPointsOfInterest()
        val startingPoints: Set<Pair<Int, Int>> = grid.filterValues { it in newVaults }.keys

        val coordinateDistances = mutableMapOf<Pair<Pair<Int, Int>, Pair<Int, Int>>, Int>()
        val graphs = newVaults.map { it.toString() }.associateWith { Graph18<String>() }.toMutableMap()

        val unvisited = startingPoints.toMutableSet()
        val visited = mutableSetOf<Pair<Int, Int>>()
        while (unvisited.isNotEmpty()) {
            val queue = mutableSetOf<Pair<Int, Int>>()
            unvisited.forEach { start: Pair<Int, Int> ->
                visited.add(start)
                val id1 = grid[start]!!.toString()
                minDistances(start) { (grid + (start to '.')).neighbors(it) }
                    .filter { (k, _) -> k in poi - visited }
                    .forEach { (k, cost) ->
                        coordinateDistances[start to k] = cost
                        val id2 = grid[k]!!.toString()
                        val graph = graphs[id1]!!
                        graphs[id2] = graph
                        queue.add(k)
                        if (id2 !in graph.nodes)
                            graph.addEdge(id1, id2)
                    }
            }
            unvisited.addAll(queue)
            unvisited.removeAll(visited)
        }

        val arcs: Map<Pair<String, String>, Int> = coordinateDistances.map { (x, cost) ->
            val (a, b) = x
            ("${grid[a]}" to "${grid[b]}") to cost
        }.toMap()


        val nodes = arcs.keys.flatMap { it.toList() }.distinct().sorted()

        val fullDistances = arcs.toMutableMap()
        fun successors(node: String): Map<String, Int> = arcs
            .filterKeys { node == it.first || node == it.second }
            .mapKeys { (k) -> (k.toList() - node).first() }

        nodes.forEach { n ->
            val dist = mutableMapOf(n to 0)
            val unvisited1 = mutableSetOf(n)
            val visited1 = mutableSetOf(n)
            while (unvisited1.isNotEmpty()) {
                val next = unvisited1.minByOrNull { dist[it] ?: 999999 }!!
                val cost = dist[next]!!
                unvisited1.remove(next)
                visited1.add(next)
                successors(next).forEach { (e, c) ->
                    val oldCost = dist[e] ?: 999999
                    val newCost = cost + c
                    dist[e] = min(oldCost, newCost)
                    unvisited1.add(e)
                }
                unvisited1.removeAll(visited1)
            }
            dist.forEach { (e, c) ->
                fullDistances[n to e] = c
            }
        }
        return graphs.values.distinct().sumOf { graph ->
            println(graph)
            val orders = graph.allTopologicalSorts(fullDistances)
            val min = orders.toList().minByOrNull { it.second }!!
            println("${orders.size} orders")
            println("Min. $min")
            min.first.windowed(2).forEach { (a, b) ->
                println("$a-$b = ${fullDistances[a to b]} = ${fullDistances[b to a]}")
            }
            println("--------------")
            min.second
        }
    }

    try {
        val testInput = readInput("Day${day}_test")
        val part1 = part1(testInput)
        check(part1 == 136) { "$part1" }
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}


private val AZ = ('A'..'Z').toList().map { it.toString() }


internal class Graph18<E> {
    private val adjacencyMap: MutableMap<E, MutableList<E>> = mutableMapOf()
    internal val nodes = mutableSetOf<E>()
    var count = 0

    fun addEdge(src: E, dest: E) {
        val list = adjacencyMap[src] ?: mutableListOf()
        list.add(dest)
        adjacencyMap[src] = list
        nodes += src
        nodes += dest
    }

    private fun findAllTopologicalOrders(
        visited: MutableMap<E, Boolean>,
        inDegree: MutableMap<E, Int>,
        stack: MutableList<E>,
        result: MutableMap<List<E>, Int>,
        distances: Map<Pair<E, E>, Int>
    ) {
        // To indicate whether all topological are found or not
        var flag = false
        for (i in nodes) {
            // If inDegree is 0 and not yet visited then only choose that vertex
            if (visited[i] != true && (inDegree[i] ?: 0) == 0) {

                // including in result
                visited[i] = true
                stack.add(i)
                for (adjacent in (adjacencyMap[i] ?: emptyList())) {
                    inDegree[adjacent] = (inDegree[adjacent] ?: 0) - 1
                }
                findAllTopologicalOrders(visited, inDegree, stack, result, distances)

                // resetting visited, res and inDegree for backtracking
                visited[i] = false
                stack.removeAt(stack.size - 1)
                for (adjacent in (adjacencyMap[i] ?: emptyList())) {
                    inDegree[adjacent] = (inDegree[adjacent] ?: 0) + 1
                }
                flag = true
            }
        }
        // We reach here if all vertices are visited.
        if (!flag) {
            count++
            val list = stack.toList().dropLastWhile { it is String && it in AZ }
            val cost = list
                .windowed(2)
                .sumOf { (a, b) -> distances[a to b]!! }
            result[list] = cost
        }
    }

    // The function does all Topological Sort.
    fun allTopologicalSorts(distances: Map<Pair<E, E>, Int>): MutableMap<List<E>, Int> {
        // Mark all the vertices as not visited
        val visited = mutableMapOf<E, Boolean>()
        val inDegree = mutableMapOf<E, Int>()

        for (i in nodes) {
            for (v in (adjacencyMap[i] ?: emptyList())) {
                inDegree[v] = (inDegree[v] ?: 0) + 1
            }
        }
        val stack = ArrayList<E>()
        val result = mutableMapOf<List<E>, Int>()
        findAllTopologicalOrders(visited, inDegree, stack, result, distances)
        return result
    }

    override fun toString(): String = "Graph18(nodes=$nodes, arcs=$adjacencyMap)"
}


