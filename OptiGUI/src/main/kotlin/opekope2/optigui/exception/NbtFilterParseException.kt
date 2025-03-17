package opekope2.optigui.exception

import opekope2.optigui.resource.format.json.JsonFilterResource

/**
 * An exception thrown when an NBT Filter cannot be parsed.
 *
 * @see JsonFilterResource
 * @see JsonFilterResource.NBT_FILTER_DECODER
 */
class NbtFilterParseException : RuntimeException {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
    constructor(message: String, cause: Throwable, enableSuppression: Boolean, writableStackTrace: Boolean) :
            super(message, cause, enableSuppression, writableStackTrace)
}
