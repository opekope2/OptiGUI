@file: JvmName("RedstoneComparatorUtil")

package opekope2.optigui.util

import net.minecraft.client.gui.screens.inventory.LecternScreen
import net.minecraft.util.Mth
import net.minecraft.world.inventory.*

/**
 * Computes the comparator output based on the screen's inventory.
 */
val AbstractContainerMenu.redstoneComparatorOutput: Int?
    get() {
        return AbstractContainerMenu.getRedstoneSignalFromContainer(
            when (this) {
                is BrewingStandMenu -> brewingStand
                is AbstractFurnaceMenu -> container
                is ChestMenu -> container
                is DispenserMenu -> dispenser
                is HopperMenu -> hopper
                is ShulkerBoxMenu -> container
                else -> return null
            }
        )
    }

/**
 * Computes the comparator output of a lectern based on its screen.
 */
val LecternScreen.redstoneComparatorOutput: Int
    get() {
        val f = if (this.numPages > 1) this.currentPage.toFloat() / (this.numPages.toFloat() - 1.0f) else 1.0f
        return Mth.floor(f * 14.0f) + 1
    }
