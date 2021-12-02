package y15

private const val day = "23"

/**
 * hlf r sets register r to half its current value, then continues with the next instruction.
tpl r sets register r to triple its current value, then continues with the next instruction.
inc r increments register r, adding 1 to it, then continues with the next instruction.
jmp offset is a jump; it continues with the instruction offset away relative to itself.
jie r, offset is like jmp, but only jumps if register r is even ("jump if even").
jio r, offset is like jmp, but only jumps if register r is 1 ("jump if one", not odd).
 */
private data class Process(
    val registers: Map<String, Long> = mapOf("a" to 0L, "b" to 0L),
    val pointer: Int = 0
) {
    fun exec(row: String): Process {
        val (instr, param1, param2) = "$row 0".split(" ")
//        println("$instr | $param1 | $param2")
        val r = param1.take(1)
        return when (instr) {
            "hlf" -> Process(registers + (r to registers[r]!! / 2), pointer + 1)
            "tpl" -> Process(registers + (r to registers[r]!! * 3), pointer + 1)
            "inc" -> Process(registers + (r to registers[r]!! + 1), pointer + 1)
            "jmp" -> Process(registers, pointer + param1.toInt())
            "jie" -> Process(
                registers,
                pointer + (if (registers[r]!! % 2L == 0L) param2.toInt() else 1)
            )
            "jio" -> Process(
                registers,
                pointer + (if (registers[r]!! == 1L) param2.toInt() else 1)
            )
            else -> Process(registers, pointer + 1)
        }
    }
}

fun main() {
    fun finalB(process: Process, input: List<String>): Long {
        var process1 = process
        while (process1.pointer in input.indices) {
            process1 = process1.exec(input[process1.pointer])
        }
        return process1.registers["b"]!!
    }

    fun part1(input: List<String>) = finalB(Process(), input)

    fun part2(input: List<String>) = finalB(Process(registers = mapOf("a" to 1L, "b" to 0L)), input)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
