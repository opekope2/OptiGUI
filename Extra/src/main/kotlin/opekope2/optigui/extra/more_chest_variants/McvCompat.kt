package opekope2.optigui.extra.more_chest_variants

import io.github.lieonlion.mcv.block.MoreChestBlockEntity
import net.minecraft.block.enums.ChestType
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.ScreenHandler
import net.minecraft.state.property.EnumProperty
import opekope2.lilac.api.registry.IRegistryLookup
import opekope2.optigui.annotation.BlockEntityProcessor
import opekope2.optigui.api.interaction.IBlockEntityProcessor
import opekope2.optigui.properties.impl.*
import java.time.LocalDate

@BlockEntityProcessor(MoreChestBlockEntity::class)
object McvCompat : IBlockEntityProcessor<MoreChestBlockEntity> {
    private val lookup = IRegistryLookup.getInstance()
    private val chestTypeEnum = EnumProperty.of("type", ChestType::class.java)

    override fun apply(chest: MoreChestBlockEntity): Any? {
        val world = chest.world ?: return null
        val state = world.getBlockState(chest.pos)
        val type = state.entries[chestTypeEnum]
        val screen = MinecraftClient.getInstance().currentScreen
        val screenHandler = (screen as? HandledScreen<*>)?.screenHandler as? GenericContainerScreenHandler

        return ChestProperties(
            commonProperties = CommonProperties(
                generalProperties = GeneralProperties(
                    container = lookup.lookupBlockId(state.block),
                    name = chest.customName?.string,
                    biome = lookup.lookupBiomeId(world, chest.pos),
                    height = chest.pos.y
                ),
                independentProperties = IndependentProperties(
                    date = LocalDate.now()
                )
            ),
            redstoneComparatorProperties = RedstoneComparatorProperties(
                comparatorOutput = ScreenHandler.calculateComparatorOutput(screenHandler?.inventory)
            ),
            isLarge = type != ChestType.SINGLE
        )
    }
}
