import java.util.*

fun <T> Sequence<T>.delimited(predicate: (T, T) -> Boolean): Sequence<List<T>> {
    val underlying = this
    return sequence {
        val buffer = mutableListOf<T>()
        var last: T? = null
        for (current in underlying) {
            val shouldSplit = last?.let { predicate(it, current) } ?: false
            if (shouldSplit) {
                yield(buffer.toList())
                buffer.clear()
            }
            buffer.add(current)
            last = current
        }
        if (buffer.isNotEmpty()) {
            yield(buffer)
        }
    }
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