package opekope2.linker

import net.fabricmc.loader.api.FabricLoader
import opekope2.util.catchAll
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

/**
 * A Fabric Loader based dynamic linker class, which automatically remaps the supplied class name to the one used by Minecraft.
 *
 * Based on [Fabric Refection Tutorial](https://fabricmc.net/wiki/tutorial:reflection) and
 * [MethodHandles.Lookup](https://docs.oracle.com/javase/8/docs/api/java/lang/invoke/MethodHandles.Lookup.html)
 *
 * [Mapping resolver documentation](https://maven.fabricmc.net/docs/fabric-loader-0.14.12/net/fabricmc/loader/api/MappingResolver.html)
 *
 * @param className the Minecraft class name defined in [namespace]
 * @param namespace the Fabric namespace [className] defined in (like official, intermediary, named)
 */
class FabricDynamicLinker @JvmOverloads constructor(
    className: String,
    val namespace: String = "intermediary"
) : DynamicLinker(className) {
    private val lookup = MethodHandles.publicLookup()
    private val mappingResolver = FabricLoader.getInstance().mappingResolver

    override val clazz: Class<*> = Class.forName(mappingResolver.mapClassName(namespace, className))

    override fun findConstructor(vararg params: Class<*>): MethodHandle? =
        catchAll {
            lookup.findConstructor(
                clazz,
                MethodType.methodType(JvmPrimitive.VOID, params)
            )
        }

    override fun findGetter(fieldName: String, fieldType: Class<*>): MethodHandle? =
        catchAll {
            lookup.findGetter(
                clazz,
                mappingResolver.mapFieldName(
                    namespace,
                    mappingResolver.unmapClassName(namespace, clazz.name),
                    fieldName,
                    Descriptor.ofClass(mappingResolver.unmapClassName(namespace, fieldType.name))
                ),
                fieldType
            )
        }

    override fun findSetter(fieldName: String, fieldType: Class<*>): MethodHandle? =
        catchAll {
            lookup.findSetter(
                clazz,
                mappingResolver.mapFieldName(
                    namespace,
                    mappingResolver.unmapClassName(namespace, clazz.name),
                    fieldName,
                    Descriptor.ofClass(mappingResolver.unmapClassName(namespace, fieldType.name))
                ),
                fieldType
            )
        }

    override fun findStaticMethod(methodName: String, returnType: Class<*>, vararg params: Class<*>): MethodHandle? =
        catchAll {
            lookup.findStatic(
                clazz,
                mappingResolver.mapMethodName(
                    namespace,
                    mappingResolver.unmapClassName(namespace, clazz.name),
                    methodName,
                    Descriptor.ofMethod(returnType, *params)
                ),
                MethodType.methodType(returnType, params)
            )
        }

    override fun findStaticGetter(fieldName: String, fieldType: Class<*>): MethodHandle? =
        catchAll {
            lookup.findStaticGetter(
                clazz,
                mappingResolver.mapFieldName(
                    namespace,
                    mappingResolver.unmapClassName(namespace, clazz.name),
                    fieldName,
                    Descriptor.ofClass(mappingResolver.unmapClassName(namespace, fieldType.name))
                ),
                fieldType
            )
        }

    override fun findStaticSetter(fieldName: String, fieldType: Class<*>): MethodHandle? =
        catchAll {
            lookup.findStaticSetter(
                clazz,
                mappingResolver.mapFieldName(
                    namespace,
                    mappingResolver.unmapClassName(namespace, clazz.name),
                    fieldName,
                    Descriptor.ofClass(mappingResolver.unmapClassName(namespace, fieldType.name))
                ),
                fieldType
            )
        }

    override fun findVirtualMethod(methodName: String, returnType: Class<*>, vararg params: Class<*>): MethodHandle? =
        catchAll {
            lookup.findVirtual(
                clazz,
                mappingResolver.mapMethodName(
                    namespace,
                    mappingResolver.unmapClassName(namespace, clazz.name),
                    methodName,
                    Descriptor.ofMethod(returnType, *params)
                ),
                MethodType.methodType(returnType, params)
            )
        }
}
