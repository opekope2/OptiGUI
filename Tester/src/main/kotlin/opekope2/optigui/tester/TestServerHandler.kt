package opekope2.optigui.tester

import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.entity.SpawnReason
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import opekope2.optigui.tester.packet.c2s.PrepareBlockTestC2SPacket
import opekope2.optigui.tester.packet.c2s.PrepareEntityTestC2SPacket
import opekope2.optigui.tester.packet.s2c.TestPreparationFailedS2CPacket
import opekope2.optigui.tester.packet.s2c.TestReadyS2CPacket
import java.util.*
import kotlin.jvm.optionals.getOrNull

object TestServerHandler {
    private var lastTestBlockPos: BlockPos? = null
    private var lastTestEntity: Entity? = null

    fun prepareBlockTest(
        packet: PrepareBlockTestC2SPacket,
        player: ServerPlayerEntity,
        responseSender: PacketSender
    ) {
        clearPreviousTest(player.world)

        val test = packet.test
        player.world.setBlockState(test.pos, test.blockState)
        test.nbt.getOrNull()?.let { nbt -> player.world.getBlockEntity(test.pos)?.readNbt(nbt) }

        player.requestTeleport(test.pos.x.toDouble(), test.pos.y.toDouble(), test.pos.z.toDouble())

        lastTestBlockPos = test.pos
        responseSender.sendPacket(TestReadyS2CPacket(Optional.empty()))
    }

    fun prepareEntityTest(
        packet: PrepareEntityTestC2SPacket,
        player: ServerPlayerEntity,
        responseSender: PacketSender
    ) {
        clearPreviousTest(player.world)

        val test = packet.test
        val entity = Registries.ENTITY_TYPE[test.entityId]
            .spawn(player.world as ServerWorld, test.pos, SpawnReason.COMMAND)
        if (entity == null) {
            responseSender.sendPacket(TestPreparationFailedS2CPacket())
            return
        }
        test.nbt.getOrNull()?.let(entity::readNbt)

        player.requestTeleport(test.pos.x.toDouble(), test.pos.y.toDouble(), test.pos.z.toDouble())

        lastTestEntity = entity
        responseSender.sendPacket(TestReadyS2CPacket(Optional.of(entity.id)))
    }

    private fun clearPreviousTest(world: World) {
        if (lastTestBlockPos != null) {
            world.setBlockState(lastTestBlockPos!!, Blocks.AIR.defaultState)
            lastTestBlockPos = null
        }
        if (lastTestEntity != null) {
            lastTestEntity!!.discard()
            lastTestEntity = null
        }
    }
}
