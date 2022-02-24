package y20

private const val day = "25"

fun main() {
    fun transform(subject: Long) =
        generateSequence(1L) { it + 1 }
            .scan(1L) { value, _ ->
                value * subject % 20201227L
            }

    fun getLoopSize(publicKey: Long) =
        transform(7).takeWhile { it != publicKey }.count()

    fun encrypt(publicKey: Long, cardLoopSize: Int) =
        transform(publicKey).drop(cardLoopSize).first()

    fun handshake(cardPubKey: Long, doorPubKey: Long): Long {
        val cardLoopSize = getLoopSize(cardPubKey)
        val doorLoopSize = getLoopSize(doorPubKey)

        val encryptionKey1 = encrypt(doorPubKey, cardLoopSize)
        val encryptionKey2 = encrypt(cardPubKey, doorLoopSize)

        check(encryptionKey1 == encryptionKey2)

        return encryptionKey1
    }

    fun part1(input: List<String>): Long {
        val (cardPubKey, doorPubKey) = input.map { it.toLong() }
        return handshake(cardPubKey, doorPubKey)
    }

    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 2947148L)
}
