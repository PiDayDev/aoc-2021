private const val day = "18"

private fun String.addToLastNumber(n: Int): String =
    if (("""[0-9]+""".toRegex().find(this)) != null) {
        replace("""^(.*?)([0-9]+)([^0-9]*)$""".toRegex()) {
            it.groupValues[1] + (it.groupValues[2].toInt() + n) + it.groupValues[3]
        }
    } else {
        this
    }

private fun String.addToFirstNumber(n: Int): String =
    if (("""[0-9]+""".toRegex().find(this)) != null) {
        replace("""^([^0-9]*)([0-9]+)(.*)$""".toRegex()) {
            it.groupValues[1] + (it.groupValues[2].toInt() + n) + it.groupValues[3]
        }
    } else {
        this
    }

private fun String.magnitude(): Int =
    if ("""[\[\],]""".toRegex().find(this) == null)
        toInt()
    else
        """\[([0-9]+),([0-9]+)]""".toRegex().replace(this) { mr ->
            val mag = mr.groupValues[1].toInt() * 3 + mr.groupValues[2].toInt() * 2
            "$mag"
        }.magnitude()

data class Snail(val s: String) {
    operator fun plus(other: Snail) = Snail("[$s,${other.s}]").reduced()

    fun reduced() =
        generateSequence(this) { it.reduced1() }
            .zipWithNext()
            .first { (b, a) -> b.s == a.s }
            .second

    private fun reduced1(): Snail {
        val exploding = indexOfFirstExploding()
        if (exploding >= 0) {
            try {
                val pre = s.take(exploding - 1)
                val rest = s.drop(exploding)
                val (left, right) = rest.substringBefore("]").split(",").map { it.toInt() }
                val suf = rest.substringAfter("]")
                val result = pre.addToLastNumber(left) + "0" + suf.addToFirstNumber(right)
                return Snail(result)
            } catch (e: Exception) {
                e.printStackTrace()
                System.err.println(s)
                throw e
            }
        }
        return split()
    }

    private fun indexOfFirstExploding(): Int {
        var level = 0
        for (j in s.indices) {
            when {
                s[j] == '[' -> level++
                s[j] == ']' -> level--
                level >= 5 -> return j
            }
        }
        return -1
    }

    private fun split(): Snail {
        """[0-9][0-9]""".toRegex().find(s) ?: return this
        return Snail("""^(.*?)([0-9][0-9]+)(.*)$""".toRegex().replace(s) {
            val n = it.groupValues[2].toInt()
            "${it.groupValues[1]}[${n / 2},${n - n / 2}]${it.groupValues[3]}"
        })
    }

    fun magnitude() = s.magnitude()
}

fun main() {

    fun part1(input: List<String>) = input
        .drop(1)
        .fold(Snail(input.first())) { a: Snail, b: String ->
            (a + Snail(b)).reduced()
        }
        .magnitude()

    fun part2(input: List<String>): Int {
        val candidates = input.map { Snail(it) }
        return candidates.maxOf { x ->
            candidates.maxOf { y ->
                if (y == x) -1 else (x + y).magnitude()
            }
        }
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
