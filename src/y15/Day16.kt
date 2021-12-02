package y15

private const val day = "16"

private val le = { a: Int, b: Int -> a <= b }
private val ge = { a: Int, b: Int -> a >= b }
private val ne = { a: Int, b: Int -> a != b }

val csiData = mapOf(
    "children" to 3,
    "cats" to 7,
    "samoyeds" to 2,
    "pomeranians" to 3,
    "akitas" to 0,
    "vizslas" to 0,
    "goldfish" to 5,
    "trees" to 3,
    "cars" to 2,
    "perfumes" to 1,
)


fun main() {
    data class Sue(val id: Int, val data: Map<String, Int>) {
        fun matches(other: Sue, comparisons: Map<String, (Int, Int) -> Boolean> = emptyMap()) = csiData.keys.none {
            val a = data[it]
            val b = other.data[it]
            val compare = comparisons[it] ?: ne
            a != null && b != null && compare(a, b)
        }
    }

    fun parse(input: List<String>) = input.map { row ->
        val data = row.split(',', ' ', ':').filter { it.isNotBlank() }
        val id = data[1].toInt()
        val keyValuePairs = data.drop(2).chunked(2)
        Sue(id, keyValuePairs.associate { (k, v) -> k to v.toInt() })
    }

    val sues = parse(readInput("Day${day}"))
    val realSue = Sue(-1, csiData)

    println(sues.first { it.matches(realSue) }.id)

    val part2Map = mapOf(
        "cats" to le,
        "trees" to le,
        "pomeranians" to ge,
        "goldfish" to ge,
    )
    println(sues.first { it.matches(realSue, part2Map) }.id)
}
