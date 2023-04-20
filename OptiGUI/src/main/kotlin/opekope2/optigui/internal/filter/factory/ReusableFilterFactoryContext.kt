package opekope2.optigui.internal.filter.factory

import opekope2.filter.factory.FilterFactoryContext
import opekope2.optigui.internal.logger
import opekope2.optigui.resource.OptiGuiResource

internal class ReusableFilterFactoryContext : FilterFactoryContext() {
    lateinit var modId: String
    override lateinit var resource: OptiGuiResource

    override fun warn(message: String) {
        logger.warn("$modId loading ${resource.id}: $message")
    }
}
