package y19


fun main() {


    val input = 197487..673251

    fun Int.matches(): Boolean {
        val pairs = toString().toList().windowed(2)
        return pairs.any { (a, b) -> a == b } &&
                pairs.all { (a, b) -> a <= b }
    }
    println(
        "Part 1 ➡ ${input.count { it.matches() }}"
    )

    val re = """(.)\1+""".toRegex()
    fun Int.matches2(): Boolean = matches() &&
            re.findAll(toString()).any { m -> m.value.length == 2 }

    println(
        "Part 2 ➡ ${input.count { it.matches2() }}"
    )
}
