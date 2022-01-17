package y18

private const val day = "16"

@Suppress("UNUSED_PARAMETER")
data class Registers16(val registers: List<Int> = listOf(0, 0, 0, 0)) {
    fun set(register: Int, value: Int): Registers16 = registers
        .toMutableList()
        .also { it[register] = value }
        .let { Registers16(it) }

    fun get(register: Int) = registers[register]

    private fun bool(b: Boolean) = if (b) 1 else 0

    fun addr(a: Int, b: Int, c: Int) = set(c, get(a) + get(b))
    fun addi(a: Int, b: Int, c: Int) = set(c, get(a) + b)
    fun mulr(a: Int, b: Int, c: Int) = set(c, get(a) * get(b))
    fun muli(a: Int, b: Int, c: Int) = set(c, get(a) * b)
    fun banr(a: Int, b: Int, c: Int) = set(c, get(a) and get(b))
    fun bani(a: Int, b: Int, c: Int) = set(c, get(a) and b)
    fun borr(a: Int, b: Int, c: Int) = set(c, get(a) or get(b))
    fun bori(a: Int, b: Int, c: Int) = set(c, get(a) or b)
    fun setr(a: Int, b: Int, c: Int) = set(c, get(a))
    fun seti(a: Int, b: Int, c: Int) = set(c, a)
    fun gtir(a: Int, b: Int, c: Int) = set(c, bool(a > get(b)))
    fun gtri(a: Int, b: Int, c: Int) = set(c, bool(get(a) > b))
    fun gtrr(a: Int, b: Int, c: Int) = set(c, bool(get(a) > get(b)))
    fun eqir(a: Int, b: Int, c: Int) = set(c, bool(a == get(b)))
    fun eqri(a: Int, b: Int, c: Int) = set(c, bool(get(a) == b))
    fun eqrr(a: Int, b: Int, c: Int) = set(c, bool(get(a) == get(b)))

}

private fun countMatching(before: Registers16, instruction: List<Int>, after: Registers16) =
    findMatching(before, instruction, after).count()

private fun findMatching(
    before: Registers16,
    instruction: List<Int>,
    after: Registers16,
    exceptNames: Iterable<String> = emptySet()
): List<String> {
    val ops = getOpFn(before).filter { it.name !in exceptNames }
    val (_, a, b, c) = instruction
    return ops.filter { fn ->
        fn(a, b, c) == after
    }.map { fn -> fn.name }
}

private fun getOpFn(before: Registers16) =
    with(before) {
        listOf(
            ::addr, ::addi, ::mulr, ::muli, ::banr, ::bani, ::borr, ::bori,
            ::setr, ::seti, ::gtir, ::gtri, ::gtrr, ::eqir, ::eqri, ::eqrr
        )
    }

private fun exec(
    before: Registers16,
    instruction: List<Int>,
    opCodes: Map<Int, String>
): Registers16 {
    val (i, a, b, c) = instruction
    val opName = opCodes[i]
    val op = getOpFn(before).first { it.name == opName }
    return op(a, b, c)
}

fun main() {
    fun String.toState() = Registers16(substringAfter("[").substringBefore("]").split(", ").map { it.toInt() })

    fun String.toOp() = split(" ").map { it.toInt() }

    fun List<String>.toOps(): List<Triple<Registers16, List<Int>, Registers16>> = this
        .dropLastWhile { it.isNotBlank() }
        .dropLastWhile { it.isBlank() }
        .chunked(4)
        .map { (row1, row2, row3) ->
            Triple(row1.toState(), row2.toOp(), row3.toState())
        }

    fun List<String>.toProgram() = takeLastWhile { it.isNotBlank() }.map { it.toOp() }

    fun part1(input: List<String>): Int = input
        .toOps()
        .count { (before, op, after) ->
            countMatching(before, op, after) >= 3
        }

    fun part2(input: List<String>): Int {
        val ops = input.toOps()
        val idToOp = mutableMapOf<Int, String>()
        while (idToOp.size < 16) {
            ops
                .map { (before, op, after) -> op.first() to findMatching(before, op, after, idToOp.values) }
                .filter { it.second.size == 1 }
                .forEach { (id, names) -> idToOp[id] = names.first() }
        }
        idToOp.toSortedMap().forEach(::println)

        val program = input.toProgram()
        val result = program.fold(Registers16()) { data, instruction ->
            exec(data, instruction, idToOp)
        }
        return result.registers.first()
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
