package y15

import java.io.FileNotFoundException

private const val day = "07"

private interface Op7 {
    fun exec(): Int
    fun isNumber() = false
}

private class Not(var k: String) : Op7 {
    override fun exec() = k.toInt() xor 65535
}

private class And(var a: String, var b: String) : Op7 {
    override fun exec() = a.toInt() and b.toInt()
}

private class Or(var a: String, var b: String) : Op7 {
    override fun exec() = a.toInt() or b.toInt()
}

private class Rsh(var a: String, var b: String) : Op7 {
    override fun exec() = a.toInt() shr b.toInt()
}

private class Lsh(var a: String, var b: String) : Op7 {
    override fun exec() = a.toInt() shl b.toInt()
}

private class Val(var k: String) : Op7 {
    constructor(n: Int) : this("$n")

    override fun exec() = k.toInt()
    override fun isNumber() = try {
        exec() >= 0
    } catch (e: Exception) {
        false
    }
}

fun main() {
    fun parseOp(op: String): Op7 = when {
        "NOT" in op -> Not(op.drop(4))
        "AND" in op -> {
            val (a, b) = op.split(" AND ")
            And(a, b)
        }
        "OR" in op -> {
            val (a, b) = op.split(" OR ")
            Or(a, b)
        }
        "RSHIFT" in op -> {
            val (a, b) = op.split(" RSHIFT ")
            Rsh(a, b)
        }
        "LSHIFT" in op -> {
            val (a, b) = op.split(" LSHIFT ")
            Lsh(a, b)
        }
        else -> Val(op)
    }

    fun parse(s: String): Pair<String, Op7> {
        val (op, wire) = s.split(" -> ")
        return wire to parseOp(op)
    }


    fun solveCircuit(ops: MutableMap<String, Op7>, goal:String="a"): Int {
        fun solve(s: String) = ops[s]?.let { if (it.isNumber()) "${it.exec()}" else null } ?: s

        while (ops.any { !it.value.isNumber() }) {
            ops.forEach { (_, op) ->
                when (op) {
                    is Not -> {
                        op.k = solve(op.k)
                    }
                    is And -> {
                        op.a = solve(op.a); op.b = solve(op.b)
                    }
                    is Or -> {
                        op.a = solve(op.a); op.b = solve(op.b)
                    }
                    is Rsh -> {
                        op.a = solve(op.a); op.b = solve(op.b)
                    }
                    is Lsh -> {
                        op.a = solve(op.a); op.b = solve(op.b)
                    }
                    is Val -> {
                        op.k = solve(op.k)
                    }
                }
            }
            val updated = ops
                .filter { (_, op) -> op !is Val }
                .mapNotNull { (wire, op) ->
                    try {
                        wire to Val(op.exec())
                    } catch (e: Exception) {
                        null
                    }
                }
            ops.putAll(updated)
        }

        return ops[goal]!!.exec()
    }

    fun part1(input: List<String>): Int {
        val ops = input.associate { parse(it) }.toMutableMap()
        return solveCircuit(ops)
    }

    fun part2(input: List<String>, bOverride: Int): Int {
        val ops = input.associate { parse(it) }.toMutableMap()
        ops["b"] = Val(bOverride)
        return solveCircuit(ops)
    }

    // test if implementation meets criteria from the description, like:
    try {
        val testInput = readInput("Day${day}_test")
        val testResult = part1(testInput)
        check(testResult == 4112) { "Test result: $testResult" }
    } catch (e: FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")
    val result1 = part1(input)
    println(result1)
    println(part2(input, result1))
}
