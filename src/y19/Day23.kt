package y19

import kotlinx.coroutines.*
import java.util.*

private const val day = 23

@ExperimentalCoroutinesApi
fun main() {

    fun part1(codes: List<Long>): Long {
        val ids = 0L..49L
        val channels = ids.associateWith { Stack<Long>().apply { push(it) } }

        val result = mutableListOf<Long>()
        val jobs = mutableListOf<Job>()

        runBlocking {
            ids.forEach { id ->
                val process = IntCodeProcessor23(codes, id)
                jobs += launch {
                    result += process.asyncExecute(channels) { it.isNotEmpty() }
                    jobs.forEach { it.cancel("DONE") }
                }
            }
        }
        return result.first()
    }

    fun part2(codes: List<Long>): Int {
        return codes.size
    }

    val input = readInput("Day${day}").codes()
    println(part1(input))
    println(part2(input))
}

class IntCodeProcessor23(
    initialCodes: List<Long>,
    val id: Long
) : IntCodeProcessor(initialCodes, false) {

    suspend fun asyncExecute(
        channels: Map<Long, Stack<Long>>,
        stopCondition: (List<Long>) -> Boolean
    ): List<Long> {
        val input = channels[id]!!
        val buffer = mutableListOf<Long>()
        val result = mutableListOf<Long>()
        while (!halted()) {
            fun inputGetter(): Long {
                if (input.isEmpty()) runBlocking { yield() }
                return if (input.isEmpty()) -1L else input.pop()
            }

            executeStep(::inputGetter) {
                buffer.add(it)
//                println("=OUT=[$id]=> ".padEnd(25) + "| " + "| ".repeat(id.toInt()) + "$it  [ buffer size = ${buffer.size} ]")
                if (buffer.size == 3) {
//                    println("=SEND=> $buffer")
                    val (address, x, y) = buffer
                    if (address == 255L) {
                        result += y
                        result += x
                    } else {
                        val output = channels[address]!!
                        output.push(y)
                        output.push(x)
                        buffer.clear()
                    }
                }
            }
            if (stopCondition(result)) break
            yield()
        }
        return result
    }

}
