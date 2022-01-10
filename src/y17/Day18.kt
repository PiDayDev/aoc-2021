package y17

import java.util.*

private const val day = 18

private data class State(
    val registers: Map<String, Long> = emptyMap(),
    val pointer: Int = 0,
    val played: List<Long> = emptyList()
) {
    fun valueOf(s: String): Long = try {
        registers[s] ?: s.toLong()
    } catch (e: Exception) {
        0L
    }

    fun next() = copy(pointer = pointer + 1)

    fun next(register: Pair<String, Long>) = next().copy(registers = registers + register)
}

private open class Process(val register: Pair<String, Long>? = null) {
    val recovered = mutableListOf<Long>()
    var finished = false

    fun executeProgramUntil(
        initialState: State? = null,
        program: List<String>,
        stop: (Process) -> Boolean
    ): State {
        var state = initialState ?: State(registers = register?.let { mapOf(it.first to it.second) } ?: mapOf())
        while (!finished && !stop(this)) {
            state = execute(state, program[state.pointer])
            finished = state.pointer !in program.indices
            if (finished) println("FINE ${recovered.size}")
        }
        return state
    }

    open fun execute(state: State, instruction: String): State {
        val (cmd, p1, p2) = "$instruction _".split(" ")
        val v1 = state.valueOf(p1)
        val v2 = state.valueOf(p2)
        return when (cmd) {
            "snd" -> state.next().copy(played = state.played + v1)
            "set" -> state.next(p1 to v2)
            "add" -> state.next(p1 to v1 + v2)
            "mul" -> state.next(p1 to v1 * v2)
            "mod" -> state.next(p1 to v1 % v2)
            "jgz" -> if (v1 > 0) state.copy(pointer = state.pointer + v2.toInt()) else state.next()
            "rcv" -> {
                if (v1 != 0L) recovered += state.played.last()
                state.next()
            }
            else -> state.next()
        }
    }
}

private class MultiThreadProcess(
    register: Pair<String, Long>,
    val outgoing: Queue<Long>,
    val incoming: Queue<Long>
) : Process(register) {
    var waiting = false

    override fun execute(state: State, instruction: String): State {
        val (cmd, p1) = instruction.split(" ")
        val v1 = state.valueOf(p1)
        val result = when (cmd) {
            "snd" -> {
                outgoing.offer(v1)
                state.next().copy(played = state.played + v1)
            }
            "rcv" -> {
                if (incoming.isNotEmpty()) {
                    val element: Long = incoming.poll()
                    recovered.add(element)
                    waiting = false
                    state.next(register = p1 to element)
                } else {
                    waiting = true
                    state
                }
            }
            else -> super.execute(state, instruction)
        }
        return result
    }

}

fun main() {
    fun part1(input: List<String>): Long {
        val process = Process()
        process.executeProgramUntil(program = input) { it.recovered.isNotEmpty() }
        return process.recovered.last()
    }


    fun part2(input: List<String>): Int {
        val (ch0, ch1) = List(2) { LinkedList<Long>() }
        val pr0 = MultiThreadProcess(register = "p" to 0L, outgoing = ch0, incoming = ch1)
        val pr1 = MultiThreadProcess(register = "p" to 1L, outgoing = ch1, incoming = ch0)
        var state0: State? = null
        var state1: State? = null
        // lazy way to get out of the deadlock
        repeat(1000) {
            pr0.waiting = false
            pr1.waiting = false
            state0 = pr0.executeProgramUntil(initialState = state0, program = input) { pr0.waiting || pr0.finished }
            state1 = pr1.executeProgramUntil(initialState = state1, program = input) { pr1.waiting || pr1.finished }
        }
        return state1!!.played.size
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
