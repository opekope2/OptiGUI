package opekope2.optigui.tester

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.MessageScreen
import net.minecraft.client.gui.screen.world.CreateWorldScreen
import net.minecraft.client.gui.screen.world.WorldCreator
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.registry.RegistryKeys
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.world.Difficulty
import net.minecraft.world.gen.chunk.FlatChunkGenerator
import opekope2.optigui.tester.mixin.ICreateWorldScreenMixin

object Tester : ClientModInitializer, ClientPlayConnectionEvents.Join, ClientTickEvents.EndTick {
    private val testWorldName = UUID.randomUUID().toString()

    @JvmField
    var finishTesting = false

    @JvmStatic
    val isEnabled: Boolean = System.getProperty("optigui.tester.enabled") != null

    override fun onInitializeClient() {
        if (!isEnabled) return
        ClientPlayConnectionEvents.JOIN.register(this)
        ClientTickEvents.END_CLIENT_TICK.register(this)
    }

    override fun onPlayReady(handler: ClientPlayNetworkHandler, sender: PacketSender, client: MinecraftClient) {
        // TODO start running tests
    }

    override fun onEndTick(client: MinecraftClient) {
        if (finishTesting) {
            finishTesting = false
            client.disconnect(MessageScreen(Text.literal("Finishing testing")))
            client.levelStorage.createSessionWithoutSymlinkCheck(TEST_WORLD_NAME).use {
                it.deleteSessionLock()
            }
            client.scheduleStop()
        }
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
