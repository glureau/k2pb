# K2PB (Kotlin to ProtoBuf)

(WIP)

Using KSP to generate protobuf from @Serializable kotlin classes.

### Comparison with KotlinX Serialization ProtoBufSchemaGenerator

This tool is another approach to KotlinX solution, some notable differences:

- Serialization works at run time, K2PB works at compile time.
- At run time, Serialization COULD know the serializerModule and @Contextual custom serializer, K2PB don't know them (so
  you'll need to add an annotation to help the generation)
- Serialization has some limitations right now:
    - sealed class are not supported, expecting a oneOf
      translation ([KotlinX Serialization doesn't support it](https://github.com/Kotlin/kotlinx.serialization/issues/67),
      so schema is coherent with the serialization)
    - custom serializer and @Contextual are not supported (the generator should ask for a ProtoBuf instance to know
      those)
    - value class are not supported (creating useless message, not matching the encoded format)
    - nested classes are not supported
- At compile time, K2PB has access to documentation and more code information to be able to copy those in generated
  protobuf files

(Eventually I hope to be able to handle upgrades from existing protobuf files, that's also another reason to get this
out of KotlinXSerialization/run-time limitations.)

### Status

This is a WORK-IN-PROGRESS, consider it an experimental tool, and don't hesitate to create issues.

Current known limitations / things I'd like to achieve:

- all protobuf annotations are not supported yet (@ProtoNumber, @ProtoXxx)
- @SerialName is not supported yet, the simpleName is used most of the time
- nested classes support is limited, for example a serializable class inside a non-serializable class is not supported
- oneof that contain directly another oneof is not supported yet (ex: sealed class that inherits from a sealed
  interface)
- List of oneOf and other combination may also be missing
- error messages are very limited (don't hesitate to send a private message on kotlinlang slack)
- object serialization is not supported yet
- TESTING is very limited (in this repository I plan to build with protoc the generated files, and be able to
  encode/decode from Kotlin and from generated Kotlin classes, to compare results)
- proto2 is not supported
- Support reserved fields
- Support upgrading proto files (checking that the newly generated files are compatible with the current ones)
- multi-module support
