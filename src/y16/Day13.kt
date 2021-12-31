package y16

private const val input = 1364

fun main() {

    fun isOpen(x: Int, y: Int): Boolean {
        val sum = x * x + 3 * x + 2 * x * y + y + y * y + input
        val ones = sum.toString(2).count { it == '1' }
        return ones % 2 == 0
    }

    fun explore(stop: (Int, Collection<Pair<Int, Int>>) -> Boolean): Pair<Int, Int> {
        val start = 1 to 1
        var current = listOf(start)
        var distance = 0
        val visited = current.toMutableSet()
        while (!stop(distance, visited)) {
            current = current
                .flatMap { (y, x) -> listOf(y + 1 to x, y - 1 to x, y to x + 1, y to x - 1) }
                .filter { (y, x) -> x >= 0 && y >= 0 && isOpen(x, y) }
                .distinct()
                .filter { it !in visited }
            visited.addAll(current)
            distance++
        }
        return distance to visited.size
    }

    fun part1(): Int = explore { _, it -> 39 to 31 in it }.first

    fun part2(): Int = explore { distance, _ -> distance >= 50 }.second

    println(part1())
    println(part2())
}
