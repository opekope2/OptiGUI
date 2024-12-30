package opekope2.optigui.internal.resource.matcher

import com.mojang.serialization.Codec
import opekope2.optigui.filter.matchAll
import opekope2.optigui.filter.matchAny
import opekope2.optigui.filter.matchNone
import opekope2.optigui.filter.matchSome
import opekope2.optigui.resource.format.json.JsonFilterResource

@JvmField
internal val NONE_OF_DECODER = Codec.of(null, JsonFilterResource.NBT_FILTER_DECODER).listOf().map(::matchNone)

@JvmField
internal val ANY_OF_DECODER = Codec.of(null, JsonFilterResource.NBT_FILTER_DECODER).listOf().map(::matchAny)

@JvmField
internal val SOME_OF_DECODER = Codec.of(null, JsonFilterResource.NBT_FILTER_DECODER).listOf().map(::matchSome)

@JvmField
internal val ALL_OF_DECODER = Codec.of(null, JsonFilterResource.NBT_FILTER_DECODER).listOf().map(::matchAll)
