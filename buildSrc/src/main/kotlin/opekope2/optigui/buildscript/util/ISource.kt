package opekope2.optigui.buildscript.util

import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.assign

interface ISource<TProperty> {
    val source: Property<TProperty>

    fun from(path: TProperty) {
        source = path
    }
}
