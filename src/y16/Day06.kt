package y16

private const val day = "06"

fun main() {
    fun part1(input: List<String>) = input
        .first()
        .indices
        .map { j -> input.map { it[j] }.groupBy { it }.maxByOrNull { (_, v) -> v.size } }
        .joinToString("") { "${it?.key}" }

    fun part2(input: List<String>)=input
        .first()
        .indices
        .map { j -> input.map { it[j] }.groupBy { it }.minByOrNull { (_, v) -> v.size } }
        .joinToString("") { "${it?.key}" }


    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
