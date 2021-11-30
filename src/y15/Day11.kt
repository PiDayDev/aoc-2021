package y15

fun main() {

    fun String.hasStraight() = "abcdefghijklmnopqrstuvwxyz".windowed(3).any { it in this }
    fun String.isClear() = "ilo".toList().none { it in this }
    fun String.hasPairs() = windowed(2).filter { it[0] == it[1] }.toSet().size > 1

    fun String.isValid() = isClear() && hasPairs() && hasStraight()

    fun String.inc() = (toList().joinToString("") { (it - 'a').toString(26) }.toLong(26) + 1)
        .toString(26).map { 'a' + "$it".toInt(26) }.joinToString("") { "$it" }

    fun nextValid(input: String) = generateSequence(input) { it.inc() }
        .dropWhile { !it.isValid() }
        .first()

    val input = "cqjxjnds"
    val sol1 = nextValid(input)
    val sol2 = nextValid(sol1.inc())
    println(sol1)
    println(sol2)
}
