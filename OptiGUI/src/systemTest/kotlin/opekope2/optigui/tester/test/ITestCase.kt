package opekope2.optigui.tester.test

import net.minecraft.entity.Entity
import net.minecraft.util.Identifier

interface ITestCase {
    val containerId: Identifier

    val expectedTexture: Identifier

    fun sendTestPreparationPacket()

    fun startInteraction(entity: Entity?)
}
