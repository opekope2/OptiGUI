@file:JvmName("Constants")

package opekope2.optigui.util

/**
 * OptiGUI mod ID.
 */
const val MOD_ID = "optigui"

/**
 * OptiGUI JSON filter resource documentation URL.
 */
const val JSON_RESOURCE_DOCS_URL = "https://opekope2.dev/OptiGUI/JSON.html"

/**
 * OptiGUI JSON resource schema URL.
 */
const val JSON_RESOURCE_SCHEMA_URL = "https://opekope2.dev/OptiGUI/json_resource.schema.json"

/**
 * OptiGUI filter debugger URL.
 */
const val DEBUGGER_URL = "https://opekope2.dev/OptiGUI/Debug.html"

/**
 * Root folder to look for OptiGUI custom GUI INI resources.
 */
const val OPTIGUI_INI_RESOURCES_ROOT = "gui"

/**
 * Root folder to look for OptiGUI custom GUI JSON resources.
 */
const val OPTIGUI_JSON_RESOURCES_ROOT = "$MOD_ID/gui"

/**
 * Root folder to look for OptiFine custom GUI properties.
 */
const val OF_PROPERTIES_RESOURCES_ROOT = "optifine/gui/container"

/**
 * The resource path `~` points to in OptiFine.
 */
// Leave the dot there, it is a dummy for resolveSibling(String)
const val OF_TILDE_PATH = "optifine/."

/**
 * Log key to tell OptiGUI about the resource being loaded.
 */
const val LOG_KEY_RESOURCE = "resource"

/**
 * Log key to tell OptiGUI about the resource pack being loaded.
 */
const val LOG_KEY_RESOURCE_PACK = "pack"
