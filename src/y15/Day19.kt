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


    fun part2(input: List<String>): Int {
        return input.size
    }

    val input = readInput("Day${day}")
    val replacements = input.takeWhile { it.isNotBlank() }.map {
        it.split(" => ").let { (a, b) -> a to b }
    }
    val molecule = input.last()

    println(part1(molecule, replacements))
    println(part2(input))
}
