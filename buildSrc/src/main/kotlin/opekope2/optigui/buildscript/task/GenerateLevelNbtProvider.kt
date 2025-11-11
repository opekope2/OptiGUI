package opekope2.optigui.buildscript.task

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.net.URLClassLoader
import java.util.*

@CacheableTask
abstract class GenerateLevelNbtProvider : AbstractCodegenTask() {
    @get:Input
    abstract val packageName: Property<String>

    @get:Classpath
    abstract val minecraftClasspath: ConfigurableFileCollection

    @TaskAction
    fun run() {
        deleteOutputDir()
        val packageDir = outputDir.dir(packageName.get().replace('.', '/')).get()
        packageDir.asFile.mkdirs()

        val urls = minecraftClasspath.files.map { it.toURI().toURL() }.toTypedArray()
        val minecraftClassLoader = URLClassLoader(urls, javaClass.classLoader)

        val content = generateLevelNbtProvider(minecraftClassLoader)
        packageDir.file("LevelNbtProvider.kt").asFile.writeText(content)
    }

    private fun String.snakeCase(): String {
        val ve = trimPrefix().replace("[A-Z]".toRegex(), "_$0").lowercase()
        return ve
    }

    private val prefixesToTrim = setOf("get", "is")

    private fun String.trimPrefix(): String {
        for (prefix in prefixesToTrim) {
            if (startsWith(prefix)) return substring(prefix.length).replaceFirstChar(Char::lowercase)
        }
        return replaceFirstChar(Char::lowercase)
    }

    private val javaExcludedMethods = setOf("getClass", "hashCode", "toString")

    private fun generateLevelNbtProvider(classLoader: ClassLoader): String {
        val levelExcludedMethods = javaExcludedMethods + setOf(
            "damageSources", // no additional info
            "dimension", // no additional info
            "gatherChunkSourceStats", // debug
            "getBiomeManager", // no additional info
            "getBlockTicks", // always empty
            "getFluidTicks", // always empty
            "getFreeMapId", // always 0
            "getGameRules", // not synced
            "getLightEngine", // irrelevant
            "getMaxSection", // no additional info
            "getMinSection", // no additional info
            "getProfiler", // irrelevant
            "getProfilerSupplier", // irrelevant
            "getRandom", // irrelevant
            "getRecipeManager", // irrelevant?
            "getServer", // always null
            "nextSubTickCount", // mutates level
            "players", // irrelevant? too much info
            "potionBrewing", // irrelevant?
            "registryAccess", // ain't gonna dump the registries
        )
        val levelDataExcludedMethods = javaExcludedMethods + setOf(
            "getGameRules", // not synced
        )
        val classes = mapOf(
            "net.minecraft.world.level.chunk.ChunkSource" to javaExcludedMethods + setOf(
                "gatherStats", // debug
                "getLevel", // duplicate
                "getLightEngine", // irrelevant
            ),
            "net.minecraft.client.multiplayer.ClientLevel" to levelExcludedMethods + setOf(
                "effects", // irrelevant?
                "entitiesForRendering", // irrelevant? too much info
                "isLightUpdateQueueEmpty", // irrelevant
            ) + getMethodNames(
                classLoader.loadClass("net.minecraft.world.level.Level"),
                levelExcludedMethods
            ) - "getLevelData",
            "net.minecraft.client.multiplayer.ClientLevel\$ClientLevelData" to levelDataExcludedMethods + getMethodNames(
                classLoader.loadClass("net.minecraft.world.level.storage.LevelData"),
                levelDataExcludedMethods
            ),
            "net.minecraft.world.level.Level" to levelExcludedMethods,
            "net.minecraft.world.level.storage.LevelData" to levelDataExcludedMethods,
            "net.minecraft.world.TickRateManager" to javaExcludedMethods,
            "net.minecraft.world.level.border.WorldBorder" to javaExcludedMethods + setOf(
                "createSettings", // irrelevant
                "getCollisionShape", // irrelevant
            ),
        )
        val imports = classes.keys.toMutableList()
        imports += listOf(
            "net.minecraft.core.BlockPos",
            "net.minecraft.core.Holder",
            "net.minecraft.core.HolderLookup",
            "net.minecraft.nbt.CompoundTag",
            "net.minecraft.nbt.StringTag",
            "net.minecraft.world.flag.FeatureFlagSet",
            "net.minecraft.world.flag.FeatureFlags",
            "net.minecraft.world.level.dimension.DimensionType",
            "net.minecraft.world.scores.Scoreboard",
            "net.minecraft.world.scores.ScoreboardSaveData",
            "opekope2.optigui.interaction.IInteraction",
            "opekope2.optigui.util.encode",
        )
        imports.sort()

        val members = classes.entries.joinToString(
            separator = "\n\n",
            transform = { (className, excludedMethods) ->
                generateProvider(classLoader.loadClass(className), excludedMethods)
            }
        )

        // language=kotlin
        return """
            |package ${packageName.get()}
            |
            |${imports.joinToString(separator = "\n") { "import $it" }.replace('$', '.')}
            |
            |object LevelNbtProvider : IInteractionNbtProvider {
            |    override fun get(interaction: IInteraction, lookup: HolderLookup.Provider): CompoundTag {
            |        val level = interaction.level
            |        val result = encodeLevel(level, lookup)
            |        if (level is ClientLevel) result.merge(encodeClientLevel(level, lookup))
            |        return result
            |    }
            |
            |$members
            |
            |    private fun encodeBlockPos(value: BlockPos, lookup: HolderLookup.Provider) =
            |        encode(value, BlockPos.CODEC, lookup)
            |
            |    private fun encodeDimensionType(value: DimensionType, lookup: HolderLookup.Provider) =
            |        encode(value, DimensionType.DIRECT_CODEC, lookup)
            |
            |    private fun encodeFeatureFlagSet(value: FeatureFlagSet, lookup: HolderLookup.Provider) =
            |        encode(value, FeatureFlags.CODEC, lookup)
            |
            |    private fun encodeHolder(value: Holder<*>, lookup: HolderLookup.Provider) =
            |        StringTag.valueOf(value.getRegisteredName())
            |
            |    private fun encodeScoreboard(value: Scoreboard, lookup: HolderLookup.Provider) =
            |        ScoreboardSaveData(value).save(CompoundTag(), lookup)
            |}
        """.trimMargin()
    }

    private fun generateProvider(`class`: Class<*>, excludedMethods: Collection<String>): String {
        val className = `class`.simpleName
        val methods = getMethods(`class`, excludedMethods).toMutableList()
        methods.sortBy { it.name.snakeCase() }

        // language=kotlin
        return """
            |    private fun encode$className(value: $className, lookup: HolderLookup.Provider) = CompoundTag().apply {
            |${methods.joinToString(separator = "\n", transform = ::emitPutCall)}
            |    }
        """.trimMargin()
    }

    private fun getMethods(`class`: Class<*>, excludedMethods: Collection<String>) = `class`.methods.asSequence()
        .filter { !Modifier.isStatic(it.modifiers) && Modifier.isPublic(it.modifiers) && !it.isSynthetic }
        .filter { it.parameterCount == 0 }
        .filter { it.returnType != Void.TYPE }
        .filter { it.name !in excludedMethods }

    private fun getMethodNames(`class`: Class<*>, excludedMethods: Collection<String>) =
        getMethods(`class`, excludedMethods).map(Method::getName)

    private val indent = " ".repeat(4 * 2)

    private fun emitPutCall(method: Method): String {
        val returnType = method.returnType
        val putMethodName =
            if (returnType.isPrimitive) "put" + returnType.name.replaceFirstChar(Char::uppercase)
            else when (returnType) {
                UUID::class.java -> "putUUID"
                String::class.java -> "putString"
                ByteArray::class.java -> "putByteArray"
                IntArray::class.java -> "putIntArray"
                LongArray::class.java -> "putLongArray"
                else -> null
            }

        val methodName = method.name
        val key = methodName.snakeCase()

        return when {
            putMethodName != null -> """$indent$putMethodName("$key", value.$methodName())"""
            returnType.isEnum -> """${indent}putString("$key", value.$methodName().name)"""
            else -> """${indent}put("$key", encode${returnType.simpleName}(value.$methodName(), lookup))"""
        }
    }
}
