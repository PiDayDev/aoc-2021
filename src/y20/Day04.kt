package y20

private const val day = "04"

fun main() {
    val requiredFields = listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")

    fun List<String>.splitToDocuments() = joinToString(" ").split("  ")

    fun String.hasRequiredFields() = requiredFields.all { "$it:" in this }

    fun part1(input: List<String>) =
        input.splitToDocuments().count { it.hasRequiredFields() }

    val validations: Map<String, (String) -> Boolean> = mapOf(
        "byr" to { it.toInt() in 1920..2002 },
        "iyr" to { it.toInt() in 2010..2020 },
        "eyr" to { it.toInt() in 2020..2030 },
        "hgt" to {
            val value = it.dropLast(2).toInt()
            it.endsWith("in") && value in 59..76 || it.endsWith("cm") && value in 150..193
        },
        "hcl" to { it.matches("""^#[a-f0-9]{6}$""".toRegex()) },
        "ecl" to { it in "amb blu brn gry grn hzl oth".split(" ") },
        "pid" to { it.matches("""^[0-9]{9}$""".toRegex()) },
        "cid" to { true },
    )

    fun part2(input: List<String>): Int = input.splitToDocuments().count { doc ->
        doc.hasRequiredFields() && doc.split(" ").all { field ->
            val (name, value) = field.split(":", limit = 2)
            validations[name]?.invoke(value) ?: false
        }
    }

    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 182)
    val p2 = part2(input)
    println(p2)
    check(p2 == 109)
}
