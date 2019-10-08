package io.github.talkarcabbage.rstimer.persistence

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 *
 * @author Talkarcabbage
 */
class IOThreadManager internal constructor() {
	internal var exc: ExecutorService

	init {
		exc = Executors.newSingleThreadExecutor { r ->
			val t = Executors.defaultThreadFactory().newThread(r)
			t.isDaemon = true
			t
		}
	}

	/**
	 * Schedules the file i/o executor to save the specified contents to file
	 * @param fileName - The file to write to
	 * @param text - The text to write
	 * @param append - Whether or not to append the data to existing contents
	 */
	fun writeFile(fileName: String, text: String, append: Boolean) {
		exc.execute { FileManager.writeFile(fileName, text, append) }
	}

	fun readFile(fileName: String): Future<IOThreadManager.ReaderCallable> {
		return exc.submit<IOThreadManager.ReaderCallable> { ReaderCallable(fileName) }
	}

	inner class ReaderCallable(val fileName: String) : Callable<String> {

		@Throws(Exception::class)
		override fun call(): String? {
			return FileManager.readFile(fileName)
		}
	}

	/**
	 * Invoke a task to be run on the File I/O thread. This will
	 * prevent other tasks, such as reading, from occurring concurrently.
	 * @param r
	 */
	fun invokeLater(r: Runnable) {
		exc.execute(r)
	}

	fun invokeLater(r: () -> Unit) {
		exc.execute(r)
	}

	companion object {

		val instance = IOThreadManager()
	}
}