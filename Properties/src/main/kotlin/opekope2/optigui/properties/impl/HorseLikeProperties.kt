package opekope2.optigui.properties.impl

import opekope2.optigui.properties.IHorseLikeProperties

/**
 * Default implementation of [IHorseLikeProperties].
 */
data class HorseLikeProperties(
    override val hasSaddle: Boolean
) : IHorseLikeProperties
