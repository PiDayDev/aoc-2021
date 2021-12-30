package y16

private const val day = "11"

fun main() {

    fun minCostForMovingComponentsUpOneFloor(n: Int) = when (n) {
        0, 1 -> n
        else -> 2 * (n - 2) + /* two trips for each part except the last two (go up with two, come down with one) */
                1 /* go up with the last two parts */
    }

    fun totalCost(compByFloor: List<Int>): Int {
        val (f0, f1, f2, f3) = compByFloor
        return when {
            f0 > 0 -> minCostForMovingComponentsUpOneFloor(f0) + totalCost(listOf(0, f0 + f1, f2, f3))
            f1 > 0 -> minCostForMovingComponentsUpOneFloor(f1) + totalCost(listOf(0, 0, f1 + f2, f3))
            f2 > 0 -> minCostForMovingComponentsUpOneFloor(f2)
            else -> 0
        }
    }

    fun componentsByFloor(input: List<String>): List<Int> {
        val components = input
            .map {
                val generators = """(\w+) generator""".toRegex().findAll(it)
                val microchips = """(\w+)-compatible microchip""".toRegex().findAll(it)
                (generators + microchips).count()
            }
        return components
    }

    fun part1(input: List<String>): Int {
        val components = componentsByFloor(input)
        return totalCost(components)
    }


    fun part2(input: List<String>): Int {
        val components = componentsByFloor(input).toMutableList()
        components[0] += 4
        return totalCost(components)
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}

