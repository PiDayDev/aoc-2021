package y20

private const val day = "06"

fun main() {
    fun List<String>.splitToGroups() = joinToString(" ").split("  ")

    fun part1(input: List<String>) =
        input.splitToGroups().sumOf { group -> (group.toSet() - ' ').size }

    fun part2(input: List<String>) =
        input.splitToGroups().sumOf { group ->
            val answers = group.split(" ")
            ('a'..'z').count { answers.all { ans -> it in ans } }
        }

    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 6297)
    val p2 = part2(input)
    println(p2)
    check(p2 == 3158)
}
