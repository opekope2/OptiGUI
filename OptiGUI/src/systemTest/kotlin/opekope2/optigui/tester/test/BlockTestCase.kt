package opekope2.optigui.tester.test

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket
import net.minecraft.registry.Registries
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.tester.mixin.IClientPlayerInteractionManagerMixin
import opekope2.optigui.tester.packet.c2s.PrepareBlockTestC2SPacket
import java.util.*

data class BlockTestCase(
    val blockState: BlockState,
    val pos: BlockPos,
    val nbt: Optional<NbtCompound>,
    override val expectedTexture: Identifier
) : ITestCase {
    override val containerId: Identifier
        get() = Registries.BLOCK.getId(blockState.block)

    override fun sendTestPreparationPacket() {
        ClientPlayNetworking.send(PrepareBlockTestC2SPacket(this))
    }

    override fun startInteraction(entity: Entity?) {
        val client = MinecraftClient.getInstance()
        val hit = BlockHitResult(
            pos.toCenterPos(),
            Direction.UP,
            pos,
            false
        )

        Interaction.prepare(
            containerId,
            client.player!!,
            client.world!!,
            Hand.MAIN_HAND,
            hit,
            null
        )
        (client.interactionManager as IClientPlayerInteractionManagerMixin).invokeSendSequencedPacket(client.world) {
            PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hit, it)
        }
    }

    companion object {
        @JvmField
        val CODEC: Codec<BlockTestCase> = RecordCodecBuilder.create { instance ->
            instance.group(
                BlockState.CODEC.fieldOf("blockState").forGetter(BlockTestCase::blockState),
                BlockPos.CODEC.fieldOf("pos").forGetter(BlockTestCase::pos),
                NbtCompound.CODEC.optionalFieldOf("nbt").forGetter(BlockTestCase::nbt),
                Identifier.CODEC.fieldOf("expectedTexture").forGetter(BlockTestCase::expectedTexture)
            ).apply(instance, ::BlockTestCase)
        }
    }
}
