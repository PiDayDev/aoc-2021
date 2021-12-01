package y15

private const val day = "13"

private typealias HappyMap = Map<Pair<String, String>, Int>

fun main() {

    val rowRegex = Regex("""(\w+) would (gain|lose) ([0-9]+) happiness units by sitting next to (\w+)""")

    fun parse(input: List<String>): HappyMap = input.associate {
        val (subject, direction, value, fellow) = rowRegex.find(it)!!.destructured
        val delta = value.toInt() * (if (direction == "gain") +1 else -1)
        (subject to fellow) to delta
    }

    fun HappyMap.happiness(table: List<String>) =
        (table + table.first()).windowed(2).sumOf { (a, b) -> getOrDefault(a to b, 0) + getOrDefault(b to a, 0) }

    fun HappyMap.maxHappiness(
        people: List<String>
    ) = permutations(people.drop(1)).map { it + people.first() }.maxOf { happiness(it) }

    val input = readInput("Day${day}")

    val values = parse(input)
    val people = values.keys.map { it.first }.toSet().toList()

    println(values.maxHappiness(people))
    println(values.maxHappiness(people + "MYSELF"))
}
