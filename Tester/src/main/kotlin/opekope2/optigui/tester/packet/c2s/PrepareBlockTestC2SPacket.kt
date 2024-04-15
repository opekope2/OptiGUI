package opekope2.optigui.tester.packet.c2s

import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import opekope2.optigui.tester.MOD_ID
import opekope2.optigui.tester.test.BlockTestCase

data class PrepareBlockTestC2SPacket(val test: BlockTestCase) : FabricPacket {
    constructor(buf: PacketByteBuf) : this(buf.decodeAsJson(BlockTestCase.CODEC))

    override fun write(buf: PacketByteBuf) {
        buf.encodeAsJson(BlockTestCase.CODEC, test)
    }

    override fun getType() = TYPE

    companion object {
        val TYPE = PacketType.create(Identifier(MOD_ID, "prepare_block_test"), ::PrepareBlockTestC2SPacket)!!
    }
}
