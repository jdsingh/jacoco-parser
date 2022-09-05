package dev.jagdeepsingh.config

interface Transformer<in I, out O> {
    fun invoke(input: I): O
}
