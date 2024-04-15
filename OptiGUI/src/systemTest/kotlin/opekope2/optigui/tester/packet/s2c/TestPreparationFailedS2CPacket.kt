package opekope2.optigui.tester.packet.s2c

import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import opekope2.optigui.tester.MOD_ID

class TestPreparationFailedS2CPacket() : FabricPacket {
    constructor(buf: PacketByteBuf) : this()

    override fun write(buf: PacketByteBuf) {
    }

    override fun getType() = TYPE

    companion object {
        val TYPE = PacketType.create(Identifier(MOD_ID, "test_preparation_failed"), ::TestPreparationFailedS2CPacket)!!
    }
}
