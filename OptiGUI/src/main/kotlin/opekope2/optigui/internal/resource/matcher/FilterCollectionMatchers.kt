package opekope2.optigui.internal.resource.matcher

import com.mojang.serialization.Codec
import opekope2.optigui.filter.matchAllOf
import opekope2.optigui.filter.matchAnyOf
import opekope2.optigui.filter.matchNoneOf
import opekope2.optigui.filter.matchSomeOf
import opekope2.optigui.resource.format.json.JsonFilterResource

@JvmField
internal val NONE_OF_DECODER = Codec.of(null, JsonFilterResource.NBT_FILTER_DECODER).listOf().map(::matchNoneOf)

@JvmField
internal val ANY_OF_DECODER = Codec.of(null, JsonFilterResource.NBT_FILTER_DECODER).listOf().map(::matchAnyOf)

@JvmField
internal val SOME_OF_DECODER = Codec.of(null, JsonFilterResource.NBT_FILTER_DECODER).listOf().map(::matchSomeOf)

@JvmField
internal val ALL_OF_DECODER = Codec.of(null, JsonFilterResource.NBT_FILTER_DECODER).listOf().map(::matchAllOf)
