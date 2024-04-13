package opekope2.optigui.buildscript.data

import org.gradle.api.tasks.Input

data class Position(@Input val x: Int, @Input val y: Int, @Input val z: Int) {
    override fun toString() = "$x $y $z"
}
