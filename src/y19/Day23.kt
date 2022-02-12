package y19

import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.atomic.AtomicLong

private const val day = 23

private const val NAT = 255L

@ExperimentalCoroutinesApi
fun main() {
    fun List<Long>.repeatedY(): Long? = this
        .filterIndexed { index, _ -> index % 2 == 0 }
        .zipWithNext()
        .firstOrNull { it.first == it.second && it.first >= 0 }
        ?.first

    fun simulateNetwork(
        codes: List<Long>,
        stopCondition: (List<Long>) -> Boolean
    ): Long {
        val ids = (0L..49L).toList()
        val channels = ids.associateWith { id -> LinkedList<Long>().apply { push(id) } } + (NAT to LinkedList<Long>())
        val processes = ids.associateWith { id -> AsyncIntCodeProcessor(codes, id) }

        val result = AtomicLong(0)
        val reports = mutableListOf<Long>()
        val jobs = mutableListOf<Job>()

        runBlocking {
            ids.forEach { id ->
                jobs += launch {
                    val process = processes[id]!!
                    result.set(process.asyncExecute(channels, stopCondition).first())
                    jobs.forEach { it.cancel("DONE") }
                }
            }
            jobs += launch {
                // NAT
                val ch0 = channels[0]!!
                val chNat = channels[NAT]!!
                while (true) {
                    if (ids.all { channels[it]!!.isEmpty() } && processes.values.all { it.failureCount > 1 } && chNat.size >= 2) {
                        val (x, y) = chNat.takeLast(2)
                        reports += y
                        reports.repeatedY()?.let { repeatedY ->
                            jobs.forEach { it.cancel("DONE") }
                            result.set(repeatedY)
                        }
                        ch0 += x
                        ch0 += y
                    }
                    yield()
                }
            }
        }
        return result.get()
    }

    fun part1(codes: List<Long>) = simulateNetwork(codes) { it.isNotEmpty() }

    fun part2(codes: List<Long>) = simulateNetwork(codes) { false }

    val input = readInput("Day${day}").codes()
    println(part1(input))
    println(part2(input))
}

class AsyncIntCodeProcessor(
    initialCodes: List<Long>,
    val id: Long
) : IntCodeProcessor(initialCodes) {

    var failureCount = 0

    suspend fun asyncExecute(
        channels: Map<Long, LinkedList<Long>>,
        stopCondition: (List<Long>) -> Boolean
    ): List<Long> {
        val input = channels[id]!!
        val buffer = mutableListOf<Long>()
        val result = mutableListOf<Long>()
        while (!halted()) {
            fun inputGetter(): Long {
                return if (input.isEmpty()) {
                    failureCount++
                    -1L
                } else {
                    failureCount = 0
                    input.pollFirst()
                }
            }

            executeStep(::inputGetter) {
                buffer.add(it)
                if (buffer.size == 3) {
                    val (address, x, y) = buffer
                    if (address == NAT) {
                        result += y
                    }
                    val output = channels[address]!!
                    output += x
                    output += y
                    buffer.clear()
                }
            }
            if (stopCondition(result)) break
            yield()
        }
        return result
    }

}
