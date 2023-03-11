package opekope2.optigui.internal.service

import net.minecraft.entity.Entity

interface EntityVariantLookupService {
    fun getVariant(entity: Entity): String?
}
