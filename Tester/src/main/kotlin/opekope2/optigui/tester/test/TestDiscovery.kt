package opekope2.optigui.tester.test

import java.io.File

object TestDiscovery {
    private val testResourcePacks =
        System.getProperty("optigui.tester.resource_packs")?.split(File.pathSeparator) ?: listOf()

    fun createTestRunners() = testResourcePacks.map(::TestRunner)
}
