package y15

private const val day = "19"


fun main() {
    fun part1(molecule: String, replacements: List<Pair<String, String>>) = replacements
        .flatMap { (k, v) ->
            val size = k.length
            (0..molecule.length - size).mapNotNull {
                val range = it until it + size
                if (molecule.substring(range) == k) molecule.replaceRange(range, v)
                else null
            }
        }
        .distinct()
        .size


    val input = readInput("Day${day}")
    val replacements = input.takeWhile { it.isNotBlank() }.map {
        it.split(" => ").let { (a, b) -> a to b }
    }
    val molecule = input.last()

    println(part1(molecule, replacements))

    println("------------------------------")

    val atomRegex = """(?=[A-Z]|$)""".toRegex()

    fun String.atomize() = split(atomRegex).filter { it.isNotBlank() }

    fun allSums(tot: Int, addends: Int): List<List<Int>> {
        if (addends <= 1) return listOf(listOf(tot))
        return (0..tot).flatMap { allSums(tot - it, addends - 1).map { list -> list + it } }
    }

    fun part2(molecule: String, replacements: List<Pair<String, String>>) {

        // first: classify the grammar alphabet into terminals and non-terminals
        val allSamples = (replacements.flatMap { listOf(it.first, it.second) } + molecule).toSet()
        val alphabet = allSamples.flatMap { it.atomize() }.distinct()
        val terminals = alphabet - replacements.toMap().keys
        val nonTerminals = alphabet - terminals.toSet()
        println("Alphabet : $alphabet")
        println("Terminals: $terminals")
        println("Non term.: $nonTerminals")

        // next: count occurrences of each terminal in the molecule
        val atoms = molecule.atomize()
        val counts = terminals.associateWith { atoms.count { c -> c == it } }
        val max = counts.values.maxOf { it }
        println("Terminal occurrences: $counts (max $max)")

        // next: classify replacements by how many terminals of each kind it adds and how many atoms it adds
        val rules = replacements
            .map { it.second.atomize() }
            .map { terminals.associateWith { t -> it.count { c -> c == t } } + ("len" to it.size - 1) }
            .distinct()
        val nonTermRules = rules.filter { (it - "len").values.toSet() == setOf(0) }
        check(nonTermRules.size == 1)
        val nonTermRule = nonTermRules.first()

        // finally, try all combinations using up to <max> total replacements + any number of replacements on nonTermRule
        val sums = allSums(max, rules.size)
        val validTransitions = sums.mapNotNull { sum ->
            val appliedSum = rules
                .zip(sum) { rule, num -> rule.mapValues { (_, v) -> v * num } }
                .reduce { m1, m2 -> m1.mapValues { (k, v) -> v + m2[k]!! } }
            val valid = counts.all { (k, v) -> appliedSum[k] == v }
            if (valid) appliedSum else null
        }.toSet()

        validTransitions.forEach {
            val rest = (atoms.size - 1 - it["len"]!!) / nonTermRule["len"]!!
            println("$it ===> ${rest + max}")
        }

    }
    part2(molecule, replacements)
}
