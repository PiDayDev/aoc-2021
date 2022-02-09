package y19

import java.io.PrintStream
import java.util.concurrent.atomic.AtomicLong

private const val day = 21

fun main() {
    fun List<Long>.springBot(springScriptProgram: String, printStream: PrintStream = System.out): Long {
        printStream.println(springScriptProgram)
        val bot = IntCodeProcessor(this)
        val inputs = iterator {
            "$springScriptProgram\n".forEach {
                this.yield(it.code.toLong())
            }
        }
        val result = AtomicLong(0)
        bot.process(inputs) {
            if (it < 256L) printStream.print(it.toInt().toChar())
            result.set(it)
        }
        return result.get()
    }

    fun part1(codes: List<Long>) = codes
        .springBot(
            """
            NOT A J
            NOT C T
            AND D T
            OR T J
            WALK
            """.trimIndent()
        )

    fun part2(codes: List<Long>) =
        // !a || (!b || !c) && d && h
        codes.springBot(
            """
            NOT B T
            NOT C J
            OR T J
            AND D J
            AND H J
            NOT A T
            OR T J
            RUN
            """.trimIndent()
        )

    val codes = readInput("Day${day}").codes()
    println(part1(codes))
    println(part2(codes))
}
