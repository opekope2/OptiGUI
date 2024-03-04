@file: JvmName("RedstoneComparatorUtil")

package opekope2.optigui.util

import net.minecraft.client.gui.screen.ingame.LecternScreen
import net.minecraft.screen.*
import net.minecraft.util.math.MathHelper

/**
 * Computes the comparator output based on the screen's inventory.
 */
val ScreenHandler.redstoneComparatorOutput: Int?
    get() {
        return ScreenHandler.calculateComparatorOutput(
            when (this) {
                is BrewingStandScreenHandler -> inventory
                is AbstractFurnaceScreenHandler -> inventory
                is GenericContainerScreenHandler -> inventory
                is Generic3x3ContainerScreenHandler -> inventory
                is HopperScreenHandler -> inventory
                is ShulkerBoxScreenHandler -> inventory
                else -> return null
            }
        )
    }

/**
 * Computes the comparator output of a lectern based on its screen.
 */
val LecternScreen.redstoneComparatorOutput: Int
    get() {
        val f = if (this.pageCount > 1) this.pageIndex.toFloat() / (this.pageCount.toFloat() - 1.0f) else 1.0f
        return MathHelper.floor(f * 14.0f) + 1
    }
