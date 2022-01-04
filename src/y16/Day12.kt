package y16

private const val day = "12"

open class AssembunnyProcess(
    val registers: Map<String, Int>,
    val pointer: Int = 0
) {
    constructor(vararg pairs: Pair<String, Int>) : this(registers = pairs.toMap())

    open fun executeProgram(input: List<String>): Map<String, Int> {
        var p = this
        while (p.pointer in input.indices) {
            val line = input[p.pointer]
            p = p.exec(line)
        }
        return p.registers
    }

    fun parse(line: String) = "$line 0".split(" ")

    fun valueOf(param:String) = registers[param]?:param.toInt()

    open fun exec(line: String): AssembunnyProcess {
        val (instr, param1, param2) = parse(line)
        val value1 = valueOf(param1)
        return when (instr) {
            "cpy" -> AssembunnyProcess(registers + (param2 to value1), pointer + 1)
            "inc" -> AssembunnyProcess(registers + (param1 to value1 + 1), pointer + 1)
            "dec" -> AssembunnyProcess(registers + (param1 to value1 - 1), pointer + 1)
            "jnz" -> AssembunnyProcess(
                registers,
                pointer + (if (value1 != 0) valueOf(param2) else 1)
            )
            else -> throw IllegalArgumentException(instr)
        }
    }

}

fun main() {

    fun part1(input: List<String>) =
        AssembunnyProcess("a" to 0, "b" to 0, "c" to 0, "d" to 0)
            .executeProgram(input)["a"]

    fun part2(input: List<String>) =
        AssembunnyProcess("a" to 0, "b" to 0, "c" to 1, "d" to 0)
            .executeProgram(input)["a"]

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
