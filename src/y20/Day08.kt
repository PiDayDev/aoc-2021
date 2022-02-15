package y20

private const val day = "08"

private data class Result8(val acc: Int, val terminated: Boolean = false)

fun main() {

    fun execute(input: List<String>): Result8 {
        var acc = 0
        var pos = 0
        val executed = mutableSetOf<Int>()
        while (pos in input.indices) {
            if (pos in executed) return Result8(acc)
            executed += pos
            val (cmd, arg) = input[pos].split(" ")
            when (cmd) {
                "acc" -> {
                    acc += arg.toInt()
                    pos++
                }
                "jmp" -> pos += arg.toInt()
                else -> pos++
            }
        }
        return Result8(acc, terminated = true)
    }

    fun part1(input: List<String>) = execute(input).acc


    fun part2(input: List<String>) = input
        .asSequence()
        .mapIndexed { j, command ->
            val (cmd, arg) = command.split(" ")
            val variation = input.toMutableList().also {
                it[j] = when (cmd) {
                    "nop" -> "jmp $arg"
                    "jmp" -> "nop $arg"
                    else -> command
                }
            }
            execute(variation)
        }
        .first { it.terminated }
        .acc

    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 1548)
    val p2 = part2(input)
    println(p2)
    check(p2 == 1375)
}
