package opekope2.optigui.internal.interaction

import net.minecraft.nbt.NbtCompound
import java.util.function.Supplier

internal class BookExtraProperties(var currentPage: Int, var pageCount: Int) : Supplier<NbtCompound> {
    override fun get() = NbtCompound().apply {
        putInt("currentPage", currentPage)
        putInt("pageCount", pageCount)
    }
}
