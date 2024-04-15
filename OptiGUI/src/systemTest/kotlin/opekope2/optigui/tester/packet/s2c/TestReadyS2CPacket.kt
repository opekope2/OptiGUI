package opekope2.optigui.tester.packet.s2c

import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import opekope2.optigui.tester.MOD_ID
import java.util.*

class TestReadyS2CPacket(val entityId: Optional<Int>) : FabricPacket {
    constructor(buf: PacketByteBuf) : this(buf.readOptional(PacketByteBuf::readVarInt))

    override fun write(buf: PacketByteBuf) {
        buf.writeOptional(entityId) { b, id -> b.writeVarInt(id) }
    }

    override fun getType() = TYPE

    companion object {
        val TYPE = PacketType.create(Identifier(MOD_ID, "test_ready"), ::TestReadyS2CPacket)!!
    }
}
