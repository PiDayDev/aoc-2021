package y16

private const val day = "21"

private fun String.exec(instruction: String): String {
    val tokens = "$instruction _ _ _ _ _ _ _ _".split(" ")
    val (verb, complement, token3) = tokens
    val (token5, token6, token7) = tokens.drop(4)
    return when ("$verb $complement") {
        "swap position" -> {
            val x = token3.toInt()
            val y = token6.toInt()
            mapIndexed { j, it -> if (j == x || j == y) this[x + y - j] else it }.joinToString("")
        }
        "swap letter" -> {
            val x = indexOf(token3)
            val y = indexOf(token6)
            mapIndexed { j, it -> if (j == x || j == y) this[x + y - j] else it }.joinToString("")
        }
        "rotate left" -> {
            val steps = token3.toInt() % length
            (this + this).drop(steps).take(length)
        }
        "rotate right" -> {
            val steps = token3.toInt() % length
            (this + this).drop((length - steps) % length).take(length)
        }
        "rotate based" -> {
            val pos = indexOf(token7)
            val steps = if (pos >= 4) pos + 2 else pos + 1
            exec("rotate right $steps !!!")
        }
        "antirotate based" -> {
            val candidates = (0..length).map { steps -> exec("rotate left $steps") }
            candidates.first { candidate -> this == candidate.exec("rotate based _ _ _ _ $token7") }
        }
        "reverse positions" -> {
            val x = token3.toInt()
            val y = token5.toInt()
            take(x) + take(y + 1).drop(x).reversed() + drop(y + 1)
        }
        "move position" -> {
            val x = token3.toInt()
            val y = token6.toInt()
            val list = toMutableList()
            val removed = list.removeAt(x)
            list.add(y, removed)
            list.joinToString("")
        }
        else -> this
    }
}

private fun reverse(instruction: String): String {
    val tokens = "$instruction _ _ _ _ _ _ _ _".split(" ")
    val (verb, complement, token3) = tokens
    return when ("$verb $complement") {
        "rotate left" -> "rotate right $token3"
        "rotate right" -> "rotate left $token3"
        "move position" -> "move position ${tokens[5]} _ _ $token3"
        "rotate based" -> "anti$instruction"
        else -> instruction
    }
}

fun main() {
    fun part1(input: List<String>) =
        input.fold("abcdefgh") { s, i ->
            s.exec(i)
        }

    fun part2(input: List<String>) =
        input.reversed().fold("fbgdceah") { s, i ->
            s.exec(reverse(i))
        }


    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
