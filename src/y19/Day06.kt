package y19

private const val day = "06"

fun main() {
    fun Map<String, String>.minDistances(center: String) =
        minDistances(center) {
            keys.filter { k -> this[k] == it } + values.filter { v -> this[it] == v }
        }


    fun List<String>.orbits(): Map<String, String> = map { it.split(")") }
        .associate { (center, orbiter) -> orbiter to center }

    fun part1(input: List<String>): Int {
        val orbits = input.orbits()
        val distances = orbits.minDistances("COM")
        return distances.values.sum()
    }

    fun part2(input: List<String>): Int {
        val orbits = input.orbits()
        val distances = orbits.minDistances(orbits["YOU"]!!)
        return distances[orbits["SAN"]]!!
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
