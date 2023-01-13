package opekope2.optigui.internal.mc_1_19_3

import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.registry.DefaultedRegistry
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.village.VillagerProfession
import net.minecraft.world.World
import opekope2.linker.FabricDynamicLinker
import opekope2.optigui.exception.LinkerException
import opekope2.optigui.provider.IRegistryLookupProvider

class RegistryLookupProvider : IRegistryLookupProvider {
    /**
     * The block registry (Registries.BLOCK).
     *
     * [Documentation (1.19.3)](https://maven.fabricmc.net/docs/yarn-1.19.3+build.5/net/minecraft/registry/Registries.html#BLOCK)
     */
    private val blockRegistry: DefaultedRegistry<Block> = loadRegistry("field_41175")

    /**
     * The entity type registry (Registries.ENTITY_TYPE).
     *
     * [Documentation (1.19.3)](https://maven.fabricmc.net/docs/yarn-1.19.3+build.5/net/minecraft/registry/Registries.html#ENTITY_TYPE)
     */
    private val entityTypeRegistry: DefaultedRegistry<EntityType<*>> = loadRegistry("field_41177")

    /**
     * The villager profession registry (Registries.VILLAGER_PROFESSION).
     *
     * [Documentation (1.19.3)](https://maven.fabricmc.net/docs/yarn-1.19.3+build.5/net/minecraft/registry/Registries.html#VILLAGER_PROFESSION)
     */
    private val villagerProfessionRegistry: DefaultedRegistry<VillagerProfession> = loadRegistry("field_41195")

    override fun lookupBlockId(block: Block): Identifier = blockRegistry.getId(block)

    override fun lookupEntityId(entity: Entity): Identifier = entityTypeRegistry.getId(entity.type)

    override fun lookupBiome(world: World, pos: BlockPos): Identifier? =
        world.getBiome(pos).key.let { if (it.isPresent) it.get().value else null }

    override fun lookupVillagerProfessionId(profession: VillagerProfession): Identifier =
        villagerProfessionRegistry.getId(profession)

    private companion object {
        /**
         * [Documentation (1.19.3)](https://maven.fabricmc.net/docs/yarn-1.19.3+build.5/net/minecraft/registry/Registries.html)
         */
        private val linker = FabricDynamicLinker("net.minecraft.class_7923")

        private fun <T> loadRegistry(intermediaryName: String): DefaultedRegistry<T> =
            linker.findStaticGetter(intermediaryName, DefaultedRegistry::class.java)?.invoke() as? DefaultedRegistry<T>
                ?: throw LinkerException("Failed to load registry.")
    }
}
