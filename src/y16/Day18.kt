package y16

private const val day = "18"

private fun String.nextRow() =
    ".$this."
        .windowed(3)
        .joinToString("") {
            when (it) {
                "^..", "..^", "^^.", ".^^" -> "^"
                else -> "."
            }
        }

private fun String.countSafe(rows: Int) =
    generateSequence(this) { it.nextRow() }
        .take(rows)
        .map { it.count { c -> c == '.' } }
        .sum()

fun main() {
    val firstRow = readInput("Day${day}").first()
    println(firstRow.countSafe(40))
    println(firstRow.countSafe(400_000))
}
