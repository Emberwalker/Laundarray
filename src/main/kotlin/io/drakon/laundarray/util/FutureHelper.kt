package io.drakon.laundarray.util

import java.util.concurrent.FutureTask
import java.util.concurrent.RunnableFuture

/**
 * Wrapper around Java's built-in future capabilities to make it more Kotlin-y.
 *
 * @author Arkan <arkan@drakon.io>
 */
public object FutureHelper {

    /**
     * Creates a future for the passed lambda and optionally starts it before returning it.
     *
     * @param func The lambda to wrap in a Future.
     * @param startNow Whether or not to start the Future before returning it.
     * @return A Future for the func task.
     */
    [suppress("UNCHECKED_CAST")] // Maybe a tiny rough edge in Kotlin's compiler there.
    public fun getFuture<T>(func: () -> T, startNow:Boolean = false): RunnableFuture<T> {
        val f = FutureTask(func)
        if (startNow) f.run()
        return f as RunnableFuture<T>
    }

}