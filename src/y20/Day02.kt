package y20

private const val day = "02"

fun main() {

    fun part1(input: List<String>) = input.count {
        val (min, max, letter, password) = it.split("""[- :]+""".toRegex())
        password.count { c -> letter == "$c" } in min.toInt()..max.toInt()
    }

    fun part2(input: List<String>) = input.count {
        val (left, right, letter, password) = it.split("""[- :]+""".toRegex())
        val a = password[left.toInt() - 1]
        val b = password[right.toInt() - 1]
        a != b && letter in "$a$b"
    }

    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 467)
    val p2 = part2(input)
    println(p2)
    check(p2 == 441)
}
