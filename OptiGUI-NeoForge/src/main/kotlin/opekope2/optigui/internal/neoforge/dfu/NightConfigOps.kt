package opekope2.optigui.internal.neoforge.dfu

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.core.NullObject
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JavaOps
import it.unimi.dsi.fastutil.bytes.ByteList
import it.unimi.dsi.fastutil.ints.IntList
import it.unimi.dsi.fastutil.longs.LongList
import net.minecraft.resources.DelegatingOps
import java.time.temporal.Temporal

internal object NightConfigOps : DelegatingOps<Any>(JavaOps.INSTANCE) {
    override fun <U : Any> convertTo(outOps: DynamicOps<U>, input: Any): U = when (input) {
        is Map<*, *> -> convertMap(outOps, input)
        is ByteList, is IntList, is LongList -> super.convertTo(outOps, input)
        is List<*> -> convertList(outOps, input)

        is Config -> convertMap(outOps, input.valueMap())
        is Temporal -> outOps.createString(input.toString())
        is NullObject -> outOps.empty()
        is Collection<*> -> convertList(outOps, input.toList())
        is Enum<*> -> outOps.createString(input.name)

        else -> super.convertTo(outOps, input)
    }
}
