package opekope2.optigui.internal.glue

import net.minecraft.entity.passive.AbstractHorseEntity

interface OptiGlue {
    val version: String
    fun getHorseVariant(horse: AbstractHorseEntity): String?
}
