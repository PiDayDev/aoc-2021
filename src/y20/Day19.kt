package y20

private const val day = "19"

fun main() {
    fun List<String>.toRules() = this
        .takeWhile { it.isNotBlank() }
        .associate {
            val (src, dst) = it.split(": ")
            src to dst
        }

    fun List<String>.candidates() = takeLastWhile { it.isNotBlank() }

    val digit = """(?<!\{)\d(?!})""".toRegex()
    val space = " ".toRegex()

    fun countMatches(
        input: List<String>,
        toFinalForm: (key: String, value: String) -> String
    ): Int {
        val rules = input.toRules().toMutableMap()
        val a = rules.filterValues { it == "\"a\"" }.keys.toList().first()
        val b = rules.filterValues { it == "\"b\"" }.keys.toList().first()
        val done = mutableMapOf(a to "a", b to "b")
        rules.remove(a)
        rules.remove(b)
        while ("0" in rules.keys) {
            val next = mutableMapOf<String, String>()
            rules.forEach { (k, v) ->
                val v2 = done.toList().fold(v) { acc, pair ->
                    val (key, value) = pair
                    acc.replace("""(?<!\{)\b$key\b(?!})""".toRegex(), if (value.length == 1) value else "(${value})")
                }
                if (v2.contains(digit)) {
                    next[k] = v2
                } else {
                    done[k] = toFinalForm(k, v2)
                }
            }
            rules.putAll(next)
            done.keys.forEach {
                rules.remove(it)
            }
        }
        val regex = "^${done["0"]}$".toRegex()
        return input.candidates().count { regex.matches(it) }
    }

    fun part1(input: List<String>) =
        countMatches(input) { _, s -> s.replace(space, "") }

    fun part2(input: List<String>) =
        countMatches(input) { k, s ->
            val re = s.replace(space, "")
            when (k) {
                "8" -> "($re)+"
                "11" -> {
                    val (pre, post) = s.split(" ")
                    (1..48).joinToString("|") { n -> "($pre{$n}$post{$n})" }
                }
                else -> re
            }
        }

    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 122)
    val p2 = part2(input)
    println(p2)
    check(p2 == 287)
}
