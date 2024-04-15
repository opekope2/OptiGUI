package opekope2.optigui.tester.packet.c2s

import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import opekope2.optigui.tester.MOD_ID
import opekope2.optigui.tester.test.EntityTestCase

data class PrepareEntityTestC2SPacket(val test: EntityTestCase) : FabricPacket {
    constructor(buf: PacketByteBuf) : this(buf.decodeAsJson(EntityTestCase.CODEC))

    override fun write(buf: PacketByteBuf) {
        buf.encodeAsJson(EntityTestCase.CODEC, test)
    }

    override fun getType() = TYPE

    companion object {
        val TYPE = PacketType.create(Identifier(MOD_ID, "prepare_entity_test"), ::PrepareEntityTestC2SPacket)!!
    }
}
