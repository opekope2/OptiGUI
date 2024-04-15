package opekope2.optigui.tester

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.MessageScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.world.CreateWorldScreen
import net.minecraft.client.gui.screen.world.WorldCreator
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.registry.RegistryKeys
import net.minecraft.sound.SoundCategory
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.world.Difficulty
import net.minecraft.world.gen.chunk.FlatChunkGenerator
import opekope2.optigui.tester.exception.TestsFailedException
import opekope2.optigui.tester.mixin.ICreateWorldScreenMixin
import opekope2.optigui.tester.packet.c2s.PrepareBlockTestC2SPacket
import opekope2.optigui.tester.packet.c2s.PrepareEntityTestC2SPacket
import opekope2.optigui.tester.packet.s2c.TestPreparationFailedS2CPacket
import opekope2.optigui.tester.packet.s2c.TestReadyS2CPacket
import opekope2.optigui.tester.test.TestDiscovery
import opekope2.optigui.tester.test.TestRunner
import java.util.*
import kotlin.jvm.optionals.getOrNull

object Tester : ClientModInitializer, ClientPlayConnectionEvents.Join, ClientTickEvents.EndTick {
    private val testWorldName = UUID.randomUUID().toString()

    private val testRunners = TestDiscovery.createTestRunners()
    private lateinit var testRunner: TestRunner
    private var allSuccessful = true

    private val taskQueue: Queue<Runnable> = LinkedList()

    @JvmStatic
    val isEnabled: Boolean = System.getProperty("optigui.tester.enabled") != null

    override fun onInitializeClient() {
        if (!isEnabled) return

        ClientPlayConnectionEvents.JOIN.register(this)
        ClientTickEvents.END_CLIENT_TICK.register(this)

        ClientPlayNetworking.registerGlobalReceiver(
            TestReadyS2CPacket.TYPE,
            ::onTestReady
        )
        ClientPlayNetworking.registerGlobalReceiver(
            TestPreparationFailedS2CPacket.TYPE,
            ::onTestPreparationFailed
        )

        ServerPlayNetworking.registerGlobalReceiver(
            PrepareBlockTestC2SPacket.TYPE,
            TestServerHandler::prepareBlockTest
        )
        ServerPlayNetworking.registerGlobalReceiver(
            PrepareEntityTestC2SPacket.TYPE,
            TestServerHandler::prepareEntityTest
        )

        runLater {
            MinecraftClient.getInstance().options.getSoundVolumeOption(SoundCategory.MASTER).value = 0.0
        }
    }

    private fun onTestReady(packet: TestReadyS2CPacket, player: ClientPlayerEntity, responseSender: PacketSender) {
        val entity = packet.entityId.getOrNull()?.let(player.world::getEntityById)
        testRunner.onTestPrepared(entity)
    }

    private fun onTestPreparationFailed(
        packet: TestPreparationFailedS2CPacket,
        player: ClientPlayerEntity,
        responseSender: PacketSender
    ) {
        testRunner.onTestPreparationFailed()
    }

    @JvmStatic
    fun onScreenChange(screen: Screen?) {
        if (::testRunner.isInitialized && screen != null) {
            testRunner.onScreenChange(screen)
        }
    }

    override fun onPlayReady(handler: ClientPlayNetworkHandler, sender: PacketSender, client: MinecraftClient) {
        runTests(client, 0)
    }

    override fun onEndTick(client: MinecraftClient) {
        while (taskQueue.isNotEmpty()) {
            taskQueue.remove().run()
        }
    }

    private fun runTests(client: MinecraftClient, ordinal: Int) {
        if (ordinal >= testRunners.count()) {
            finishTesting(client)
            return
        }

        testRunner = testRunners[ordinal]

        testRunner.runTests(client) { allSuccessful ->
            this.allSuccessful = this.allSuccessful and allSuccessful
            runLater {
                runTests(client, ordinal + 1)
            }
        }
    }

    private fun finishTesting(client: MinecraftClient) {
        client.disconnect(MessageScreen(Text.literal("Finishing testing")))
        client.levelStorage.createSessionWithoutSymlinkCheck(testWorldName).use {
            it.deleteSessionLock()
        }
        if (allSuccessful) {
            client.scheduleStop()
        } else {
            throw TestsFailedException("Some tests did not succeed")
        }
    }

    fun runLater(runnable: Runnable) {
        taskQueue += runnable
    }

    @JvmStatic
    fun loadTestWorld(client: MinecraftClient) {
        CreateWorldScreen.create(client, client.currentScreen)

        val createWorldScreen = client.currentScreen as CreateWorldScreen
        val creator = createWorldScreen.worldCreator
        val worldPresets = creator.generatorOptionsHolder.combinedRegistryManager[RegistryKeys.WORLD_PRESET]

        creator.setCheatsEnabled(true)
        creator.difficulty = Difficulty.EASY
        creator.gameMode = WorldCreator.Mode.CREATIVE
        creator.worldName = testWorldName
        creator.worldType = WorldCreator.WorldType(worldPresets.getEntry(worldPresets[Identifier("flat")]))
        creator.applyModifier { dynamicRegistryManager, dimensionsRegistryHolder ->
            dimensionsRegistryHolder.with(
                dynamicRegistryManager,
                FlatChunkGenerator(dynamicRegistryManager[RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET][Identifier("the_void")]!!.settings())
            )
        }

        (createWorldScreen as ICreateWorldScreenMixin).invokeCreateLevel()
    }
}
