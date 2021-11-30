package y15

private const val day = "12"

fun main() {
    val number = """[-+]?[0-9]+""".toRegex()

    fun String.numbers() = number
        .findAll(this)
        .map { it.value.toInt() }

    fun sumNumbers(input: List<String>) = input.flatMap { it.numbers() }.sum()

    println(sumNumbers(readInput("Day${day}", "json")))

    // Day12_noRed.json: derived from source input using Day12.sorry.js
    val sol2 = sumNumbers(readInput("Day${day}_noRed", "json"))
    println(sol2)
}
