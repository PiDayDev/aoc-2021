package y15

private const val day = "09"

fun main() {

    fun parse(row: String): Map<Pair<String, String>, Int> {
        val (a, b, d) = row.split(" (?:to|=) ".toRegex())
        val distance = d.toInt()
        return mutableMapOf(
            (a to b) to distance,
            (b to a) to distance
        )
    }

    fun parse(rows: List<String>) =
        rows.fold(mapOf<Pair<String, String>, Int>()) { acc, curr ->
            acc + parse(curr)
        }

    val input = readInput("Day${day}")

    val distances = parse(input)
    val cities = distances.keys.map { it.first }.toSet().toList()
    val permutationDistances = permutations(cities).map{ list -> list.windowed(2).sumOf { (a, b) -> distances[a to b]!! } }

    println(permutationDistances.minOf{it})
    println(permutationDistances.maxOf{it})
}
