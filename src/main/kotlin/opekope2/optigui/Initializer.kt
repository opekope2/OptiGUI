package opekope2.optigui

import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger("OptiGUI")
const val version = "@mod_version@"

@Suppress("unused")
fun initialize() {
    logger.info("OptiGUI $version initialized.")
}
