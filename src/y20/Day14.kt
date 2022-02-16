package y20

private const val day = "14"

fun main() {
    fun part1(input: List<String>): Long {
        var mask = ""
        val mem = mutableMapOf<Long, Long>()
        input.forEach {
            val (cmd, value) = it.split(" = ")
            if (cmd == "mask") {
                mask = value
            } else {
                val address = cmd.substringAfter("[").substringBefore("]").toLong()
                val binValue = value.toLong().toString(2).padStart(36, '0')
                val actualValue = binValue
                    .zip(mask) { a, b -> if (b == 'X') a else b }
                    .joinToString("")
                    .toLong(2)
                mem[address] = actualValue
            }
        }
        return mem.values.sum()
    }

    fun String.expand(): List<String> =
        if ('X' !in this) listOf(this)
        else listOf(replaceFirst('X', '0'), replaceFirst('X', '1')).flatMap { it.expand() }

    fun part2(input: List<String>): Long {
        var mask = ""
        val mem = mutableMapOf<Long, Long>()
        input.forEach {
            val (cmd, value) = it.split(" = ")
            if (cmd == "mask") {
                mask = value
            } else {
                val address = cmd.substringAfter("[").substringBefore("]").toLong()
                val binAddress = address.toString(2).padStart(36, '0')
                val maskedAddress = binAddress
                    .zip(mask) { a, b ->
                        when (b) {
                            '0' -> a
                            else -> b
                        }
                    }
                    .joinToString("")
                maskedAddress.expand().forEach { a -> mem[a.toLong(2)] = value.toLong() }
            }
        }
        return mem.values.sum()
    }

    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 5902420735773L)
    val p2 = part2(input)
    println(p2)
    check(p2 == 3801988250775L)
}
