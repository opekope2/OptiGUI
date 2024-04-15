package opekope2.optigui.tester.test

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.tester.packet.c2s.PrepareEntityTestC2SPacket
import java.util.*

data class EntityTestCase(
    val entityId: Identifier,
    val pos: BlockPos,
    val nbt: Optional<NbtCompound>,
    override val expectedTexture: Identifier
) : ITestCase {
    override val containerId: Identifier
        get() = entityId

    override fun sendTestPreparationPacket() {
        ClientPlayNetworking.send(PrepareEntityTestC2SPacket(this))
    }

    override fun startInteraction(entity: Entity?) {
        val client = MinecraftClient.getInstance()

        Interaction.prepare(
            containerId,
            client.player!!,
            client.world!!,
            Hand.MAIN_HAND,
            entity?.let(::EntityHitResult),
            null
        )
        client.networkHandler!!.sendPacket(PlayerInteractEntityC2SPacket.interact(entity, false, Hand.MAIN_HAND))
    }

    companion object {
        @JvmField
        val CODEC: Codec<EntityTestCase> = RecordCodecBuilder.create { instance ->
            instance.group(
                Identifier.CODEC.fieldOf("blockState").forGetter(EntityTestCase::entityId),
                BlockPos.CODEC.fieldOf("pos").forGetter(EntityTestCase::pos),
                NbtCompound.CODEC.optionalFieldOf("nbt").forGetter(EntityTestCase::nbt),
                Identifier.CODEC.fieldOf("expectedTexture").forGetter(EntityTestCase::expectedTexture)
            ).apply(instance, ::EntityTestCase)
        }
    }
}
