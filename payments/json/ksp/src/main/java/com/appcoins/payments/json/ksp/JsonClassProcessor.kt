package com.appcoins.payments.json.ksp

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import java.io.OutputStream

class JsonClassProcessor(
  private val codeGenerator: CodeGenerator,
  private val logger: KSPLogger,
) : SymbolProcessor {

  operator fun OutputStream.plusAssign(str: String) {
    this.write(str.toByteArray())
  }

  operator fun StringBuilder.plusAssign(str: String) {
    this.append(str)
  }

  override fun process(resolver: Resolver): List<KSAnnotated> {
    val jsonClasses = resolver.getSymbolsWithAnnotation("com.appcoins.payments.json.Json")
      .filterIsInstance<KSClassDeclaration>()
    jsonClasses.forEach { classD ->
      codeGenerator.createNewFile(
        // Make sure to associate the generated file with sources to keep/maintain it across incremental builds.
        // Learn more about incremental processing in KSP from the official docs:
        // https://kotlinlang.org/docs/ksp-incremental.html
        dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()),
        packageName = classD.packageName.asString(),
        fileName = "${classD.simpleName.getShortName()}_Converter"
      ).use { stream ->
        stream += "package ${classD.packageName.asString()}\n\n"
        val imports = mutableSetOf(
          "org.json.JSONArray",
          "org.json.JSONObject",
          "org.json.JSONStringer"
        )
        val extractors = StringBuilder()
        val converters = StringBuilder()
        val integrators = StringBuilder()
        classD.accept(
          visitor = Visitor(
            resolver = resolver,
            imports = imports,
            converters = converters,
            extractors = extractors,
            integrators = integrators
          ),
          data = Unit
        )
        stream += imports.asSequence()
          .map {
            if (it.contains("get") || it.contains("put")) {
              it.replace("kotlin", "com.appcoins.payments.json")
                .replace("org.json", "com.appcoins.payments.json")
                .replace("java.math", "com.appcoins.payments.json")
            } else {
              it
            }
          }
          .filterNot {
            it.contains("kotlin")
              || it.contains("collections")
              || it.endsWith("getBoolean")
              || it.endsWith("getDouble")
              || it.endsWith("getInt")
              || it.endsWith("getLong")
              || it.endsWith("getString")
              || it.endsWith("getJSONArray")
              || it.endsWith("getJSONObject")
          }
          .map { "import $it\n" }
          .sorted()
          .toSet()
          .joinToString("")
        stream += "\n"
        stream += extractors.toString()
        stream += "\n"
        stream += converters.toString()
        stream += "\n"
        stream += integrators.toString()
      }
    }

    return jsonClasses.filterNot(KSClassDeclaration::validate).toList()
  }

  inner class Visitor(
    private val resolver: Resolver,
    private val imports: MutableSet<String>,
    private val converters: StringBuilder,
    private val extractors: StringBuilder,
    private val integrators: StringBuilder,
  ) : KSVisitorVoid() {

    private var className: String? = null
    private val fromJson = StringBuilder()
    private val toJson = StringBuilder()

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
      fromJson.clear()
      toJson.clear()
      val modifiers = classDeclaration.modifiers
        .filter {
          it in listOf(
            Modifier.PUBLIC,
            Modifier.PRIVATE,
            Modifier.INTERNAL,
            Modifier.PROTECTED
          )
        }
        .joinToString(" ") { it.name.lowercase() }
        .plus(" ")
        .ifBlank { "" }
      className = classDeclaration.qualifiedName?.asString()
      val cSimpleName = classDeclaration.simpleName.getShortName()
      imports += "com.appcoins.payments.json.getOrNull"
      imports += "com.appcoins.payments.json.putNullable"
      return when (classDeclaration.classKind) {
        ClassKind.CLASS -> {
          val primaryConstructor: KSFunctionDeclaration =
            classDeclaration.primaryConstructor ?: run {
              logger.error("Missing primary constructor", classDeclaration)
              return
            }
          imports += className!!
          imports += "com.appcoins.payments.json.jsonToJSONObject"
          val parameters: List<KSValueParameter> = primaryConstructor.parameters
          converters += "${modifiers}fun String.jsonTo$cSimpleName(): $cSimpleName? =\n"
          converters += "  jsonToJSONObject()?.to$cSimpleName()\n"
          converters += "\n"
          converters += "${modifiers}fun $cSimpleName?.toJsonString(): String =\n"
          converters += "  this?.toJsonObject()?.toString() ?: \"null\"\n"
          converters += "\n"
          if (parameters.iterator().hasNext()) {
            fromJson += "${modifiers}fun JSONObject.to$cSimpleName(): $cSimpleName = $cSimpleName(\n"
            toJson += "${modifiers}fun $cSimpleName.toJsonObject(): JSONObject = JSONObject()\n"
            parameters.filter { it.validate() }
              .forEach { parameter -> visitValueParameter(parameter, Unit) }
            fromJson += ")\n"
          } else {
            // Otherwise, generating function with no args.
            fromJson += "${modifiers}fun JSONObject.to$cSimpleName(): $cSimpleName = $cSimpleName()\n"
            toJson += "${modifiers}fun $cSimpleName.toJsonObject(): JSONObject = JSONObject()\n"
          }
          converters += fromJson.toString()
          converters += "\n"
          converters += toJson.toString()
          extractors += "${modifiers}fun JSONObject.get${cSimpleName}(key: String): $cSimpleName =\n"
          extractors += "  getJSONObject(key).to${cSimpleName}()\n"
          extractors += "\n"
          extractors += "${modifiers}fun JSONArray.get${cSimpleName}(index: Int): $cSimpleName =\n"
          extractors += "  getJSONObject(index).to${cSimpleName}()\n"
          extractors += "\n"
          extractors += "${modifiers}fun JSONObject.get${cSimpleName}OrNull(key: String): $cSimpleName? =\n"
          extractors += "  getOrNull(key) { get${cSimpleName}(key) }\n"
          extractors += "\n"
          extractors += "${modifiers}fun JSONArray.get${cSimpleName}OrNull(index: Int): $cSimpleName? =\n"
          extractors += "  getOrNull(index) { get${cSimpleName}(index) }\n"
          integrators += "${modifiers}fun JSONObject.putNullable(key: String, data: $cSimpleName?): JSONObject =\n"
          integrators += "  putNullable(key, data?.toJsonObject())\n"
          integrators += "\n"
          integrators += "${modifiers}fun JSONArray.putNullable(data: $cSimpleName?): JSONArray =\n"
          integrators += "  putNullable(data?.toJsonObject())\n"
        }

        ClassKind.ENUM_CLASS -> {
          imports += className!!
          imports += "com.appcoins.payments.json.jsonToString"
          val constants: Sequence<KSClassDeclaration> =
            classDeclaration.declarations.filterIsInstance<KSClassDeclaration>()
          converters += "${modifiers}fun String.jsonTo$cSimpleName(): $cSimpleName? =\n"
          converters += "  jsonToString()?.to$cSimpleName()\n"
          converters += "\n"
          converters += "${modifiers}fun $cSimpleName?.toJsonString(): String =\n"
          converters += "  this?.toJString()?.toString() ?: \"null\"\n"
          converters += "\n"
          if (constants.iterator().hasNext()) {
            fromJson += "fun String.to$cSimpleName(): $cSimpleName = when(this) {\n"
            toJson += "fun $cSimpleName.toJString(): String = when(this) {\n"
            constants.filter { it.validate() }
              .map { it.simpleName.getShortName() }
              .forEach {
                imports += "$className.$it"
                fromJson += "    \"$it\" -> $it\n"
                toJson += "    $it -> \"$it\"\n"
              }
            fromJson += "    else -> throw NoSuchElementException(\"$cSimpleName doesn't have \\\"\$this\\\" constant defined\")\n"
            fromJson += "}\n"
            toJson += "}\n"
          } else {
            // Otherwise, generating function with no args.
            fromJson += "fun String.to$cSimpleName(): $cSimpleName = $cSimpleName\n"
            toJson += "fun $cSimpleName.toJString(): String = $cSimpleName.toString()\n"
          }
          converters += fromJson.toString()
          converters += "\n"
          converters += toJson.toString()
          extractors += "${modifiers}fun JSONObject.get${cSimpleName}(key: String): $cSimpleName =\n"
          extractors += "  getString(key).to${cSimpleName}()\n"
          extractors += "\n"
          extractors += "${modifiers}fun JSONArray.get${cSimpleName}(index: Int): $cSimpleName =\n"
          extractors += "  getString(index).to${cSimpleName}()\n"
          extractors += "\n"
          extractors += "${modifiers}fun JSONObject.get${cSimpleName}OrNull(key: String): $cSimpleName? =\n"
          extractors += "  getOrNull(key) { get${cSimpleName}(key) }\n"
          extractors += "\n"
          extractors += "${modifiers}fun JSONArray.get${cSimpleName}OrNull(index: Int): $cSimpleName? =\n"
          extractors += "  getOrNull(index) { get${cSimpleName}(index) }\n"
          integrators += "${modifiers}fun JSONObject.putNullable(key: String, data: $cSimpleName?): JSONObject =\n"
          integrators += "  putNullable(key, data?.toJString())\n"
          integrators += "\n"
          integrators += "${modifiers}fun JSONArray.putNullable(data: $cSimpleName?): JSONArray =\n"
          integrators += "  putNullable(data?.toJString())\n"
        }

        else -> {
          logger.error("Only class or enum can be annotated with @Json", classDeclaration)
        }
      }
    }

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
    }

    override fun visitFile(file: KSFile, data: Unit) {
    }

    override fun visitValueParameter(valueParameter: KSValueParameter, data: Unit) {
      val parameterName = valueParameter.name!!.getShortName()

      // Getting the value of the 'name' argument.
      val jsonName: String = valueParameter.annotations
        .find { it.shortName.getShortName() == "Json" }
        ?.arguments
        ?.first { arg -> arg.name?.getShortName() == "name" }
        ?.value as String?
        ?: parameterName

      // Generating argument type.
      val resolvedType: KSType = valueParameter.type.resolve()
      val classDeclaration = resolvedType.declaration
      val (classQualifier, classShortName) = classDeclaration.qualifiedName?.run {
        getQualifier() to getShortName()
      } ?: run {
        logger.error("Invalid type argument", valueParameter)
        return
      }

      // Generating generic parameters if any.
      val genericArguments: List<KSTypeArgument> =
        valueParameter.type.element?.typeArguments ?: emptyList()
      visitTypeArguments(genericArguments)

      // imports += parameterClassName

      val nullable = resolvedType.isMarkedNullable
      val nonNullResolvedType = resolvedType.makeNotNullable()
      val orNull = if (nullable) "OrNull" else ""

      when {
        nonNullResolvedType.isAssignableTo<Map<String, *>>(resolver) -> {
          val argName = genericArguments[1].type?.resolve()?.declaration?.qualifiedName
            ?: run {
              logger.error(
                "Map generic type not found for: $className.$parameterName($classQualifier$classShortName)",
                genericArguments[1]
              )
              return
            }
          imports += "com.appcoins.payments.json.getMap"
          imports += "${argName.getQualifier()}.get${argName.getShortName()}"
          imports += "${argName.getQualifier()}.putNullable"
          fromJson += "  $parameterName = getMap$orNull(\"$jsonName\")  { get${argName.getShortName()}(it) },\n"
          toJson += "  .putNullable(\"$jsonName\", $parameterName) { key, value -> putNullable(key, value) }\n"
        }

        nonNullResolvedType.isAssignableTo<List<*>>(resolver) -> {
          val argName = genericArguments[0].type?.resolve()?.declaration?.qualifiedName
            ?: run {
              logger.error(
                "List generic type not found for: $className.$parameterName($classQualifier$classShortName)",
                genericArguments[0]
              )
              return
            }
          imports += "com.appcoins.payments.json.getList"
          imports += "${argName.getQualifier()}.get${argName.getShortName()}"
          imports += "${argName.getQualifier()}.putNullable"
          fromJson += "  $parameterName = getList$orNull(\"$jsonName\") { get${argName.getShortName()}(it) },\n"
          toJson += "  .putNullable(\"$jsonName\", $parameterName) { putNullable(it) }\n"
        }

        else -> {
          imports += "$classQualifier.get${classShortName}$orNull"
          imports += "$classQualifier.putNullable"
          fromJson += "  $parameterName = get${classShortName}$orNull(\"$jsonName\"),\n"
          toJson += "  .putNullable(\"$jsonName\", $parameterName)\n"
        }
      }
    }

    private fun visitTypeArguments(typeArguments: List<KSTypeArgument>) {
      typeArguments.forEach { visitTypeArgument(it, data = Unit) }
    }

    override fun visitTypeArgument(typeArgument: KSTypeArgument, data: Unit) {
      val resolvedType: KSType? = typeArgument.type?.resolve()
      imports += resolvedType?.declaration?.qualifiedName?.asString()
        ?: run {
          logger.error("Invalid type argument", typeArgument)
          return
        }

      // Generating nested generic parameters if any
      val genericArguments: List<KSTypeArgument> =
        typeArgument.type?.element?.typeArguments ?: emptyList()
      visitTypeArguments(genericArguments)
    }

    private inline fun <reified T> KSType.isAssignableTo(resolver: Resolver): Boolean {
      val classDeclaration = requireNotNull(resolver.getClassDeclarationByName<T>()) {
        "Unable to resolve ${KSClassDeclaration::class.simpleName} for type ${T::class.simpleName}"
      }
      return classDeclaration.asStarProjectedType().isAssignableFrom(this)
    }
  }
}

class JsonClassProcessorProvider : SymbolProcessorProvider {
  override fun create(
    environment: SymbolProcessorEnvironment,
  ): SymbolProcessor =
    JsonClassProcessor(
      codeGenerator = environment.codeGenerator,
      logger = environment.logger,
    )
}
