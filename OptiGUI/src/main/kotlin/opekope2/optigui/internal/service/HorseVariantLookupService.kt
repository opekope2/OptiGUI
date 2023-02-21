package opekope2.optigui.internal.service

import net.minecraft.entity.passive.AbstractHorseEntity

interface HorseVariantLookupService {
    fun getHorseVariant(horse: AbstractHorseEntity): String?
}
