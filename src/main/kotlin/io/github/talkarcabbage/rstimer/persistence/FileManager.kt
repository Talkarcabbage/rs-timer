package io.github.talkarcabbage.rstimer.persistence

import com.google.common.base.Throwables
import io.github.talkarcabbage.logger.LoggerManager
import java.io.*
import java.net.MalformedURLException
import java.net.URL
import java.nio.channels.Channels
import java.util.*
import java.util.logging.Level

/**
 *
 * @author Talkarcabbage <br></br>
 * I wrote those in case I ended up with a use for simplified file reading and writing.
 * The class handles FileNotFoundExceptions itself.
 */
class FileManager internal constructor() {
	companion object {
		internal val logger = LoggerManager.getInstance().getLogger("FileManager")

		/**
		 *
		 * @param fileName - The file to attempt to read from.
		 * @return - A string containing the contents of the file.
		 */
		fun readFile(fileName: String): String? {

			val toRead = File(fileName)
			if (!toRead.exists()) {
				logger.warning { "FileManager: Error reading file $fileName -- The file does not exist or is inaccessible." }
				return null
			}

			val stringB = StringBuilder()

			try {
				Scanner(toRead).use { scanIn ->

					while (scanIn.hasNext()) {
						stringB.append(scanIn.nextLine()+"\n")
					}

					scanIn.close()

					return stringB.toString()

				}
			} catch (e: FileNotFoundException) {
				logger.severe("Somehow we made it past the fileExists check!")
				logger.severe(Throwables.getStackTraceAsString(e))
			}

			return null
		}

		/**
		 * This method is a convenience way of reading lines from a text file into an array.
		 * @param fileName - Name of the file to attempt to read from.
		 *
		 * @return An array of each 'line' of the file separated by \n
		 */
		fun readFileSplit(fileName: String): Array<String>? {
			val toSplit = readFile(fileName)

			return toSplit?.split("\n".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
		}

		fun readFileSplitList(fileName: String): List<String> {
			val list = ArrayList<String>()

			val toRead = File(fileName)
			if (!toRead.exists()) {
				logger.warning { "FileManager: Error reading file $fileName -- The file does not exist or is inaccessible." }
				return list
			}

			try {
				Scanner(toRead).use { scanIn ->

					while (scanIn.hasNext()) {
						list.add(scanIn.nextLine())
					}


				}
			} catch (e: FileNotFoundException) {
				logger.log(Level.SEVERE, "Somehow we made it past the fileExists check! ", e)
			}

			return list
		}

		@JvmOverloads
		fun writeFile(fileName: String, text: String, append: Boolean = false): Boolean {

			val toWrite = File(fileName)

			try {
				PrintWriter(toWrite).use { pw ->
					if (append) {
						pw.append(text)
					} else {
						pw.write(text)
					}
					pw.close()
					return true

				}
			} catch (e: FileNotFoundException) {
				logger.severe("Could not write file: $fileName")
				logger.severe(Throwables.getStackTraceAsString(e))
			}

			return false
		}

		/**
		 * Downloads a file and saves it. Note: Referenced several sources from google on image downloading.
		 * @param url
		 * @param fileName
		 * @return
		 * @throws MalformedURLException If the url is invalid
		 */
		@Throws(MalformedURLException::class)
		fun downloadFile(fileName: String, url: String) {
			val website = URL(url)
			try {
				Channels.newChannel(website.openStream()).use { rbc -> FileOutputStream(fileName).use { fos -> fos.getChannel().transferFrom(rbc, 0, java.lang.Long.MAX_VALUE) } }
			} catch (e: IOException) {
				logger.severe("Error downloading assets")
				logger.severe(Throwables.getStackTraceAsString(e))
			}
		}

		/**
		 * Essentially a shortcut to the File.createNewFile(), which creates a blank file if it does not yet exist.
		 * @param string - A file name to create. This can be a local or full path.
		 */
		fun ensureExists(string: String) {
			val f = File(string)

			try {
				f.createNewFile() //NOSONAR
			} catch (e: IOException) {
				logger.severe("Error creating file: $string")
				logger.severe(Throwables.getStackTraceAsString(e))
			}

		}
	}
}