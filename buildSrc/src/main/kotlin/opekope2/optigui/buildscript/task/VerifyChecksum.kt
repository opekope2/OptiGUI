package opekope2.optigui.buildscript.task

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.security.MessageDigest

@CacheableTask
abstract class VerifyChecksum : DefaultTask() {
    init {
        checksum.convention(byteArrayOf())
        digest.convention(MessageDigest.getInstance("MD5")) // Not cryptographically secure, only used for checksum
    }

    @get:Input
    abstract val checksum: Property<ByteArray>

    @get:Input
    abstract val digest: Property<MessageDigest>

    fun checksum(checksum: String) {
        this.checksum.set(checksum.chunked(2).map { it.toInt(16).toByte() }.toByteArray())
    }

    @TaskAction
    fun run() {
        val checksum = checksum.get()
        if (checksum.isEmpty()) logger.warn("No expected checksum was specified")

        val digest = digest.get()
        for (inFile in inputs.files) {
            for (file in inFile.walk().sorted()) {
                if (file.isFile) digest.update(file.readBytes())
            }
        }

        val hash = digest.digest()
        logger.info("Checksum: {}", hash.toHexString())

        if (checksum.isNotEmpty()) {
            require(hash.contentEquals(checksum)) { "Expected checksum ${checksum.toHexString()}, got ${hash.toHexString()}" }
        }
    }

    private fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }
}
