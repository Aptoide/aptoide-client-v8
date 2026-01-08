package cm.aptoide.pt.exception_handler

import kotlin.coroutines.cancellation.CancellationException

interface ExceptionHandler {
  /**
   * Records an exception for monitoring/debugging purposes.
   * @param exception The exception to record
   */
  fun recordException(exception: Throwable)
}

/**
 * Extension function to run a block of code and record any exceptions using the provided ExceptionHandler.
 * Similar to runCatching, but also records exceptions to the exception handler.
 *
 * This function preserves structured concurrency by rethrowing [CancellationException].
 * CancellationExceptions are not recorded as they represent intentional cancellation, not errors.
 *
 * @param exceptionHandler The handler to record exceptions
 * @param block The block of code to execute
 * @return Result of the operation
 */
inline fun <R> runCatchingAndRecord(
  exceptionHandler: ExceptionHandler,
  block: () -> R
): Result<R> {
  return runCatching(block).onFailure { exception ->
    if (exception is CancellationException) throw exception
    exceptionHandler.recordException(exception)
  }
}
