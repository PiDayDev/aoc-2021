package y17

private const val day = "07"

private data class Program(
    val name: String,
    val weight: Int,
    val holding: List<String> = emptyList()
)

private fun String.toProgram(): Program {
    val tokens = split("""[-> (,)]+""".toRegex()).filter { it.isNotBlank() }
    val (name, w) = tokens
    val weight = w.toInt()
    return Program(name, weight, tokens.drop(2))
}

private fun List<String>.toProgramMap() = this
    .map { it.toProgram() }
    .associateBy { it.name }

private fun Map<String, Program>.towerWeight(from: String): Int {
    val p = get(from)!!
    return p.weight + p.holding.sumOf { towerWeight(it) }
}

fun main() {

    fun part1(input: List<String>): Set<String> {
        val ps = input.toProgramMap()
        val children = ps.values.flatMap { it.holding }
        return ps.keys - children.toSet()
    }


    fun part2(input: List<String>): Int {
        val ps = input.toProgramMap()
        val weights = ps.keys.associateWith { ps.towerWeight(it) }
        val unbalanced = ps.values.mapNotNull {
            val childrenWeights = it.holding.map { e -> weights[e] }
            when {
                childrenWeights.toSet().size > 1 -> it to childrenWeights
                else -> null
            }
        }
        val (parent, chWeights) = unbalanced.minByOrNull { it.second.last() ?: Int.MAX_VALUE }!!
        val (bad, good) = chWeights.groupBy { it }.toList().sortedBy { it.second.size }.map { it.first }
        val indexBad = chWeights.indexOf(bad)
        val delta = good!! - bad!!
        val child = parent.holding[indexBad]
        return ps[child]!!.weight + delta
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
