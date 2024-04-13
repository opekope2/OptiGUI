package opekope2.optigui.buildscript.util

import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.assign

interface IDestination<TProperty> {
    val destination: Property<TProperty>

    fun into(path: TProperty) {
        destination = path
    }
}
