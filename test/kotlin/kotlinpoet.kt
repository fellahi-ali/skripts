import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName

val STRING = String::class.asClassName()

fun Val(name: String, type: TypeName, vararg modifers: KModifier) =
    ParameterSpec.builder(name, type, *modifers)

fun ParameterSpec.Builder.withDefault(): ParameterSpec.Builder {
    val code = when (this.build().type) {
        STRING -> "\"\""
        INT -> "0"
        DOUBLE -> "0.0"
        else -> null
    }
    if (code != null) this.defaultValue(code)
    return this
}

fun DataClass(name: String, vararg params: ParameterSpec.Builder, mutable: Boolean = false): TypeSpec {
    val builtParams = params.map { it.build() }
    val properties = builtParams.map { param ->
        PropertySpec.builder(param.name, param.type)
            .initializer(param.name)
            .mutable(mutable)
            .build()
    }

    return TypeSpec.classBuilder(name)
        .primaryConstructor(
            FunSpec.constructorBuilder().addParameters(builtParams).build()
        ).addProperties(properties)
        .build()
}
