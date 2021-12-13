private const val day = "13"

fun main() {
    fun List<String>.dots(): List<Pair<Int, Int>> = takeWhile { it.isNotBlank() }
        .map { it.split(",") }
        .map { (a, b) -> a.toInt() to b.toInt() }

    fun List<String>.folds(): List<Pair<String, Int>> = takeLastWhile { it.isNotBlank() }
        .map { it.substringAfter("along ").split("=") }
        .map { (a, b) -> a to b.toInt() }

    fun List<Pair<Int, Int>>.foldX(p: Int) = map { (x, y) -> if (x < p) x to y else 2 * p - x to y }.distinct()

    fun List<Pair<Int, Int>>.foldY(p: Int) = map { (x, y) -> if (y < p) x to y else x to 2 * p - y }.distinct()

    infix fun List<Pair<Int, Int>>.foldedOn(fold: Pair<String, Int>): List<Pair<Int, Int>> {
        val (foldDir, foldPos) = fold
        return when (foldDir) {
            "x" -> foldX(foldPos)
            else -> foldY(foldPos)
        }
    }

    fun List<String>.part1() = (dots() foldedOn folds().first()).size

    fun List<String>.part2(): String {
        val result = folds().fold(dots()) { dots, fold -> dots foldedOn fold }
        val xRange = result.minOf { it.first }..result.maxOf { it.first }
        val yRange = result.minOf { it.second }..result.maxOf { it.second }
        return yRange.joinToString("\n") { y ->
            xRange.joinToString("") { x ->
                if ((x to y) in result) "@@" else "  "
            }
        }
    }

    val input = readInput("Day${day}")
    println(input.part1())
    println(input.part2())
}
