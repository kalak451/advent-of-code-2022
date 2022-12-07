import java.util.*

fun <T> Sequence<T>.delimited(predicate: (T) -> Boolean): Sequence<List<T>> {
    val underlying = this
    return sequence {
        val buffer = mutableListOf<T>()
        for (current in underlying) {
            val shouldSplit = predicate(current)
            if (shouldSplit) {
                yield(buffer.toList())
                buffer.clear()
            } else {
                buffer.add(current)
            }
        }
        if (buffer.isNotEmpty()) {
            yield(buffer)
        }
    }
}

fun <T> Sequence<T>.split(predicate: (T) -> Boolean): Sequence<List<T>> {
    val underlying = this
    return sequence {
        val buffer = mutableListOf<T>()
        for (current in underlying) {
            val shouldSplit = predicate(current)
            if (shouldSplit) {
                if (buffer.isNotEmpty()) {
                    yield(buffer.toList())
                    buffer.clear()
                }
            }

            buffer.add(current)
        }
        if (buffer.isNotEmpty()) {
            yield(buffer)
        }
    }
}

fun String.allInts(): List<Int> {
    val pattern = Regex("-?\\d+")

    return pattern
        .findAll(this)
        .map { it.value.toInt() }
        .toList()
}

private fun <T> shortestPath(start: T, adj: (T) -> List<Pair<T, Long>>): Map<T, Long> {
    val dist: MutableMap<T, Long> = mutableMapOf()
    dist[start] = 0

    val queue = PriorityQueue<Pair<T, Long>>(compareBy { it.second })
    queue.add(Pair(start, 0))

    while (queue.isNotEmpty()) {
        val (t, _) = queue.poll()
        adj(t).forEach { (newT, cost) ->
            val distToNewT = dist[t]!! + cost
            if (distToNewT < (dist[newT] ?: Long.MAX_VALUE)) {
                dist[newT] = distToNewT
                queue.add(Pair(newT, distToNewT))
            }
        }
    }

    return dist.toMap()
}