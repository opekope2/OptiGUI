package opekope2.optigui.internal.config.annotation

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class RequiresMod(val modId: String, val inverse: Boolean)
