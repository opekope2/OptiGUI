package opekope2.optigui.tester.test

import com.google.gson.JsonParser
import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.Entity
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import opekope2.optigui.internal.TextureReplacer
import opekope2.optigui.registry.ContainerDefaultGuiTextureRegistry
import opekope2.optigui.registry.RetexturableScreenRegistry
import org.slf4j.LoggerFactory
import java.io.InputStreamReader
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Function
import kotlin.jvm.optionals.getOrNull

class TestRunner(private val resourcePack: String) {
    private val logger = LoggerFactory.getLogger("OptiGUI/Tester")

    private var testCases: MutableList<IdentifiedTestCase>? = null
    private var onComplete: ((allSuccessful: Boolean) -> Unit)? = null
    private var allSuccessful = true

    var isRunning = false
        private set

    fun runTests(client: MinecraftClient, onComplete: (allSuccessful: Boolean) -> Unit) {
        if (isRunning) throw IllegalStateException("Tests are already running")

        allSuccessful = true
        isRunning = true
        this.onComplete = onComplete
        loadTestResourcePack(client).thenRun { startTesting(client) }
    }

    private fun loadTestResourcePack(client: MinecraftClient): CompletableFuture<Void> {
        client.resourcePackManager.enable(resourcePack)
        return client.reloadResources()
    }

    private fun startTesting(client: MinecraftClient) {
        testCases = loadTests(client.resourceManager)
        startCurrentTest()
    }

    private fun loadTests(resourceManager: ResourceManager): MutableList<IdentifiedTestCase> {
        val testResources = resourceManager.findResources("test") { id ->
            id.namespace == "optigui" && id.path.endsWith(".test.json")
        }

        return testResources.mapNotNullTo(LinkedList()) { (id, testResource) ->
            val testCase = InputStreamReader(testResource.inputStream).use {
                val json = JsonParser.parseReader(it)
                TEST_CODEC.parse(JsonOps.INSTANCE, json)
            }.result().getOrNull()?.map(Function.identity(), Function.identity())

            if (testCase == null) {
                logger.error("$id: cannot parse test")
                null
            } else if (testCase.containerId !in ContainerDefaultGuiTextureRegistry) {
                logger.error("$id: no default GUI texture for container ${testCase.containerId}")
                null
            } else {
                IdentifiedTestCase(id, testCase)
            }
        }
    }

    private fun startCurrentTest() {
        val testCase = testCases!!.firstOrNull()
        if (testCase != null) {
            testCase.testCase.sendTestPreparationPacket()
            return
        }

        finishTesting(MinecraftClient.getInstance())
    }

    private fun runCurrentTestAndAdvanceToNext() {
        val (id, test) = testCases!!.removeFirst()

        val actualTexture = TextureReplacer.replaceTexture(ContainerDefaultGuiTextureRegistry[test.containerId]!!)
        if (test.expectedTexture == actualTexture) {
            logger.info("$id: success")
        } else {
            logger.error("$id: expected ${test.expectedTexture}, got $actualTexture")
            allSuccessful = false
        }

        MinecraftClient.getInstance().player!!.closeHandledScreen()
    }

    private fun finishTesting(client: MinecraftClient) {
        client.resourcePackManager.disable(resourcePack)

        val onComplete = this.onComplete!!

        testCases = null
        this.onComplete = null
        isRunning = false

        onComplete(allSuccessful)
    }

    fun onTestPrepared(entity: Entity?) {
        testCases!!.first().testCase.startInteraction(entity)
    }

    fun onTestPreparationFailed() {
        val (id) = testCases!!.first()
        logger.error("$id: failed to prepare")
        allSuccessful = false
    }

    fun onScreenChange(screen: Screen) {
        if (screen !in RetexturableScreenRegistry) return
        runCurrentTestAndAdvanceToNext()
        startCurrentTest()
    }

    private data class IdentifiedTestCase(val testResource: Identifier, val testCase: ITestCase)

    companion object {
        @JvmField
        val TEST_CODEC: Codec<Either<BlockTestCase, EntityTestCase>> =
            Codec.either(BlockTestCase.CODEC, EntityTestCase.CODEC)
    }
}
