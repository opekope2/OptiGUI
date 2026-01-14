package opekope2.optigui.util

import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.ParentComponent
import io.wispforest.owo.ui.parsing.UIModel
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

inline fun <reified C : Component> requireChildById(crossinline rootComponent: () -> ParentComponent) =
    ReadOnlyProperty<Any?, C> { _, property -> rootComponent().requireChildById<C>(property.name) }

inline fun <reified C : Component> ParentComponent.requireChildById(id: String): C =
    requireNotNull(childById(C::class.java, id)) { "Child '$id' was not found" }

inline fun <reified C : Component> UIModel.expandTemplate(parameters: Map<String, String>) =
    PropertyDelegateProvider<Any?, Lazy<C>> { _, property ->
        lazy { expandTemplate<C>(C::class.java, property.name, parameters) }
    }
