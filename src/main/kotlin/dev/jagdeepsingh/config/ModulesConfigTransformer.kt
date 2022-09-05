package dev.jagdeepsingh.config

@JvmInline
value class ModuleName(val name: String) {

    override fun toString(): String {
        return name
    }
}

class ModulesConfigTransformer : Transformer<List<String>, List<ModuleName>> {

    override fun invoke(input: List<String>): List<ModuleName> {
        return input.map { ModuleName(name = it.trim('"')) }
    }
}
