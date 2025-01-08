@file: JvmName("Constants")

package opekope2.optigui.util

/**
 * OptiGUI mod ID.
 */
const val MOD_ID = "optigui"

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
 * NBT key for a screen's title.
 */
const val SCREEN_TITLE_KEY = "title"

/**
 * NBT key for redstone comparator output calculated from an inventory screen.
 */
const val COMPARATOR_OUTPUT_KEY = "comparator_output"

/**
 * NBT key for an inventory screen's inventory.
 */
const val INVENTORY_KEY = "inventory"

/**
 * NBT key for a book screen's current page.
 */
const val CURRENT_PAGE_KEY = "current_page"

/**
 * NBT key for a book screen's page count.
 */
const val PAGE_COUNT_KEY = "page_count"
