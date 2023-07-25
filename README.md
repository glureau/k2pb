# K2PB (Kotlin to ProtoBuf)

(WIP)

Using KSP to generate protobuf from @Serializable kotlin classes.

### Comparison with KotlinX Serialization ProtoBufSchemaGenerator

This tool is another approach to KotlinX solution, some notable differences:
- Serialization works at run time, K2PB works at compile time.
- At run time, Serialization COULD know the serializerModule and @Contextual custom serializer, K2PB don't know them (so you'll need to add an annotation to help the generation)
- Serialization has some limitations right now:
  - sealed class are not supported, expecting a oneOf translation (also it's a bit tricky to support them properly)
  - custom serializer and @Contextual are not supported (the generator should ask for a ProtoBuf instance to know those)
  - value class are not supported (creating useless message, not matching the encoded format)
  - nested classes are not supported
- At compile time, K2PB has access to documentation and more code information to be able to copy those in generated protobuf files

(Eventually I hope to be able to handle upgrades from existing protobuf files, that's also another reason to get this out of KotlinXSerialization/run-time limitations.)
