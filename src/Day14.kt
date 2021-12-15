private const val day = "14"

private data class Polymer(val template: String, val insertions: Map<String, String>) {
    private fun step() = copy(template = template
        .windowed(2)
        .joinToString("", postfix = template.last().toString()) { "${it[0]}${insertions[it]}" }
    )

    operator fun times(n: Int) = (1..n).fold(this) { it, _ -> it.step() }

}

fun main() {
    fun parse(input: List<String>) = Polymer(
        template = input.first(),
        insertions = input.dropWhile { "->" !in it }
            .map { it.split(" -> ") }
            .associate { (a, b) -> a to b }
    )

    operator fun Map<String, Long>.times(n: Long) =
        mapValues { (_, v) -> v * n }

    fun List<Map<String, Long>>.summed() =
        reduce { a, b -> (a.keys + b.keys).distinct().associateWith { (a[it] ?: 0L) + (b[it] ?: 0L) } }

    fun iterate(input: List<String>, iterations: Int): Long {
        val (template, insertions) = parse(input)
        val step: Map<String, Map<String, Long>> = insertions.mapValues { (k, v) ->
            val e1 = "${k[0]}$v"
            val e2 = "$v${k[1]}"
            if (e1 == e2) mapOf(e1 to 2)
            else mapOf(e1 to 1, e2 to 1)
        }
        val zero = " $template ".windowed(2).map { mapOf(it to 1L) }.summed()
        val result = (1..iterations).fold(zero) { it, _ ->
            it.map { (atoms: String, count) ->
                (step[atoms] ?: mapOf(atoms to 1L)) * count
            }.summed()
        }
        val letters = result
            .toList()
            .flatMap { (k, v) ->
                listOf(mapOf(k.take(1) to v), mapOf(k.drop(1) to v))
            }
            .summed()
            .filterKeys { " " != it }
        println(letters.toList().sortedByDescending { it.second })
        return (letters.maxOf { it.value } - letters.minOf { it.value }) / 2
    }

    fun part1(input: List<String>) = iterate(input, 10)
    fun part2(input: List<String>) = iterate(input, 40)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
