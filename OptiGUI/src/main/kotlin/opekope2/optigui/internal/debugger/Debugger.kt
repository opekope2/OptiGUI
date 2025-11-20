package opekope2.optigui.internal.debugger

import com.google.gson.GsonBuilder
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.ResourceLocation
import opekope2.optigui.filter.texture_changer.TextureChangerFilter
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.interaction.InteractionTarget
import opekope2.optigui.internal.I18n
import opekope2.optigui.internal.TextureChanger
import opekope2.optigui.util.NbtFilterEvaluation
import org.apache.commons.codec.binary.Base64OutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.apache.commons.compress.compressors.gzip.GzipParameters
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.zip.Deflater
import kotlin.jvm.optionals.getOrNull

internal object Debugger {
    private val LOGGER = LoggerFactory.getLogger(Debugger::class.java)
    private const val BUFFER_SIZE = 128 * 1024
    private val GSON = GsonBuilder().disableHtmlEscaping().create()
    private val GZIP_PARAMETERS = GzipParameters().also {
        it.bufferSize = BUFFER_SIZE
        it.compressionLevel = Deflater.BEST_COMPRESSION
    }
    private val DEBUG_DATA_CODEC = Codec.mapPair(
        ResourceLocation.CODEC.fieldOf("resource"),
        NbtFilterEvaluationMapCodec
    ).codec()

    fun getEncodedDebugData(interaction: IInteraction) = try {
        val debugData = getDebugData(JsonOps.INSTANCE, interaction).ifError {
            LOGGER.atError()
                .addArgument(I18n.OPTIGUI_INSPECTOR_DESCRIPTION_DEBUG_CLICKED_ERROR.supplyTranslation())
                .addArgument(it.messageSupplier)
                .log("{}: {}")
        }.resultOrPartial().getOrNull() ?: return null

        ByteArrayOutputStream(BUFFER_SIZE).use { buffer ->
            Base64OutputStream(buffer, true, 0, null).use { base64 ->
                GzipCompressorOutputStream(base64, GZIP_PARAMETERS).use { gzip ->
                    OutputStreamWriter(gzip).use { GSON.toJson(debugData, it) }
                }
            }
            buffer.toString(Charsets.UTF_8)
        }
    } catch (e: IOException) {
        LOGGER.atError()
            .addArgument(I18n.OPTIGUI_INSPECTOR_DESCRIPTION_DEBUG_CLICKED_ERROR.supplyTranslation())
            .setCause(e)
            .log("{}")
        null
    }

    fun <T : Any> getDebugData(ops: DynamicOps<T>, interaction: IInteraction): DataResult<T> {
        val interactionNbt = interaction.createNbt()
        return ops.mapBuilder()
            .add("interaction", NbtOps.INSTANCE.convertTo(ops, interactionNbt))
            .add("filters", getTextureChangerDebugData(ops, interactionNbt))
            .build(ops.empty())
    }

    private fun <T : Any> getTextureChangerDebugData(ops: DynamicOps<T>, nbt: CompoundTag) = TextureChanger.filters
        .asSequence()
        .map { (target, filters) ->
            filters.asSequence()
                .map { it.getDebugData(ops, nbt) }
                .fold(ops.listBuilder(), ListBuilder<T>::add)
                .build(ops.empty())
                .map { Pair(target.asString(ops), it) }
        }
        .fold(ops.mapBuilder()) { acc, pair ->
            acc.add(pair.map(Pair<T, *>::getFirst), pair.map(Pair<*, T>::getSecond))
        }.build(ops.empty())

    private fun <T : Any> InteractionTarget.asString(ops: DynamicOps<T>) = when (this) {
        is InteractionTarget.Block -> "$type $id"
        is InteractionTarget.Entity -> "$type $id"
        is InteractionTarget.Item -> "$type $id"
        InteractionTarget.Inventory -> type
        InteractionTarget.Unknown -> type
    }.let(ops::createString)

    private fun <T : Any> TextureChangerFilter.getDebugData(ops: DynamicOps<T>, nbt: CompoundTag): DataResult<T> {
        val eval = NbtFilterEvaluation(this, nbt, nbt)
        val pair = Pair(resourceId, eval)
        return DEBUG_DATA_CODEC.encodeStart(ops, pair)
    }
}
