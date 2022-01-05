package y16

private const val day = "24"
private const val open = ".0123456789"
private const val lots = 666666

private typealias RC = Pair<Int, Int>

private data class AirDuct(val map: List<String>) {
    private val ri = map.indices
    private val ci = map.first().indices

    val locations: Map<String, RC> = (0..9).mapNotNull { n ->
        try {
            val digit = "$n"
            val r = map.indexOfFirst { digit in it }
            val c = map[r].indexOf(digit)
            digit to (r to c)
        } catch (e: Exception) {
            null
        }
    }.toMap()

    val arcs: Map<String, Map<String, Int>> =
        locations.map { (name, rc) -> name to minPathsToLocations(rc) }.toMap()

    private fun isValid(r: Int, c: Int) = r in ri && c in ci && map[r][c] in open

    private fun isValid(rc: RC) = isValid(rc.first, rc.second)

    private fun moves(r: Int, c: Int) = listOf(r + 1 to c, r - 1 to c, r to c + 1, r to c - 1).filter { isValid(it) }

    private fun moves(rc: RC) = moves(rc.first, rc.second)

    private fun minPathsToLocations(from: RC): Map<String, Int> {
        val visited = mutableMapOf<RC, Int>()
        var distance = 0
        var queue = listOf(from)

        while (queue.isNotEmpty()) {
            queue.associateWithTo(visited) { distance }
            distance++
            queue = queue.flatMap { moves(it) }.distinct() - visited.keys
        }
        return locations.map { (name, rc) -> name to (visited[rc] ?: lots) }.toMap()
    }

    fun length(path: List<String>) =
        path.windowed(2).sumOf { (a, b) -> arcs[a]?.get(b) ?: lots }
}

fun main() {
    val start = listOf("0")
    fun part1(input: List<String>): Int {
        val maze = AirDuct(input)
        val destinations = maze.locations.keys - start
        return permutations(destinations.toList())
            .map { start + it }
            .minOf { maze.length(it) }
    }

    fun part2(input: List<String>): Int {
        val maze = AirDuct(input)
        val destinations = maze.locations.keys - start
        return permutations(destinations.toList())
            .map { start + it + start }
            .minOf { maze.length(it) }
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
