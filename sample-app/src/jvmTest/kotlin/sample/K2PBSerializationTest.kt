package sample

import com.glureau.k2pb.runtime.K2PB
import com.glureau.k2pb.runtime.decodeFromByteArray
import com.glureau.k2pb.runtime.encodeToByteArray
import com.glureau.sample.BarEvent
import com.glureau.sample.CollectionType
import com.glureau.sample.CommonClass
import com.glureau.sample.FooEvent
import com.glureau.sample.NullableNativeTypeEvent
import com.glureau.sample.TransientField
import com.glureau.sample.User
import com.glureau.sample.UuidBytesValueClass
import com.glureau.sample.UuidStringValueClass
import com.glureau.sample.UuidsHolder
import com.glureau.sample.Vehicle
import com.glureau.sample.WithNestClassA
import com.glureau.sample.WithNestClassB
import com.glureau.sample.lib.AnEnum
import com.glureau.sample.lib.EnumHolder
import com.glureau.sample.lib.registerSampleLibCodecs
import com.glureau.sample.registerSampleAppCodecs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

@OptIn(ExperimentalStdlibApi::class)
class K2PBSerializationTest {
    private val serializer = K2PB {
        registerSampleLibCodecs()
        registerSampleAppCodecs()
    }

    @Test
    fun `test FooEvent and BarEvent serialization`() {
        val fooEvent = FooEvent(CommonClass("test-id"))
        val serialized = serializer.encodeToByteArray<FooEvent>(fooEvent)
        val deserialized = serializer.decodeFromByteArray<FooEvent>(serialized)
        assertEquals(fooEvent, deserialized)

        val barEvent = BarEvent(CommonClass("test-id"))
        val serializedBar = serializer.encodeToByteArray<BarEvent>(barEvent)
        val deserializedBar = serializer.decodeFromByteArray<BarEvent>(serializedBar)
        assertEquals(barEvent, deserializedBar)
    }

    @Test
    fun `test collection types serialization`() {
        val collectionType = CollectionType(
            integerList = listOf(1, 2, 3),
            stringList = listOf("a", "b", "c"),
            maybeIntegerList = listOf(4, 5, 6),
            mapStringInt = mapOf("one" to 1, "two" to 2),
            dataClassList = listOf(com.glureau.sample.lib.DataClassFromLib(7)),
            mapStringObject = mapOf("height" to com.glureau.sample.lib.DataClassFromLib(8)),
        )
        val serialized = serializer.encodeToByteArray<CollectionType>(collectionType)
        println("serialized: ${serialized.joinToString(" ") { it.toHexString() }}")
        val deserialized = serializer.decodeFromByteArray<CollectionType>(serialized)
        assertEquals(collectionType, deserialized)
    }

    @Test
    fun `test enum serialization - default`() {
        val enumClass = EnumHolder(AnEnum.AnEnum_A)
        val serialized = serializer.encodeToByteArray<EnumHolder>(enumClass)
        println("serialized: ${serialized.joinToString { it.toHexString() }}")
        val deserialized = serializer.decodeFromByteArray<EnumHolder>(serialized)
        assertEquals(enumClass, deserialized)
    }

    @Test
    fun `test enum serialization - not default`() {
        val enumClass = EnumHolder(AnEnum.AnEnum_C)
        val serialized = serializer.encodeToByteArray<EnumHolder>(enumClass)
        println("serialized: ${serialized.joinToString { it.toHexString() }}")
        val deserialized = serializer.decodeFromByteArray<EnumHolder>(serialized)
        assertEquals(enumClass, deserialized)
    }

    @Test
    fun `test nested classes serialization`() {
        val withNestClassA = WithNestClassA(
            a = WithNestClassA.NestedClass("test nested")
        )
        val serialized = serializer.encodeToByteArray<WithNestClassA>(withNestClassA)
        val deserialized = serializer.decodeFromByteArray<WithNestClassA>(serialized)
        assertEquals(withNestClassA, deserialized)

        val withNestClassB = WithNestClassB(
            b = WithNestClassB.NestedClass(WithNestClassB.NestedClass.NestedEnum.B)
        )
        val serializedB = serializer.encodeToByteArray<WithNestClassB>(withNestClassB)
        val deserializedB = serializer.decodeFromByteArray<WithNestClassB>(serializedB)
        assertEquals(withNestClassB, deserializedB)
    }

    @Test
    fun `test nullable native type serialization`() {
        val nullableNative = NullableNativeTypeEvent(
            integer = 42,
            long = 123456789L,
            float = 3.14f,
            double = 3.14159,
            string = "test",
            short = 123,
            char = 'A',
            boolean = true,
            byte = 127,
            byteArray = byteArrayOf(1, 2, 3)
        )
        val serialized = serializer.encodeToByteArray<NullableNativeTypeEvent>(nullableNative)
        val deserialized = serializer.decodeFromByteArray<NullableNativeTypeEvent>(serialized)
        assertEquals(nullableNative, deserialized)
    }

    @Test
    fun `test transient field serialization`() {
        val transientField = TransientField(
            fieldSerialized = "test",
            fieldTransient = "should not be serialized"
        )
        val serialized = serializer.encodeToByteArray<TransientField>(transientField)
        val deserialized = serializer.decodeFromByteArray<TransientField>(serialized)
        assertEquals(transientField.fieldSerialized, deserialized?.fieldSerialized)
        assertEquals("default value", deserialized?.fieldTransient)
    }

    @Test
    fun `test UUID holders serialization`() {
        val uuid = Uuid.random()
        val uuidsHolder = UuidsHolder(
            uuidAsString = uuid,
            uuidAsBytes = uuid,
            stringValueClass = UuidStringValueClass(uuid),
            bytesValueClass = UuidBytesValueClass(uuid)
        )
        val serialized = serializer.encodeToByteArray<UuidsHolder>(uuidsHolder)
        val deserialized = serializer.decodeFromByteArray<UuidsHolder>(serialized)
        assertEquals(uuidsHolder, deserialized)
    }

    @Test
    fun `test sealed polymorphic serialization`() {
        val user = User(
            name = "John",
            vehicle = Vehicle.Car("Tesla")
        )
        val serialized = serializer.encodeToByteArray<User>(user)
        val deserialized = serializer.decodeFromByteArray<User>(serialized)
        assertEquals(user, deserialized)

        val userWithBike = User(
            name = "Jane",
            vehicle = Vehicle.Bike("BMW")
        )
        val serializedBike = serializer.encodeToByteArray<User>(userWithBike)
        val deserializedBike = serializer.decodeFromByteArray<User>(serializedBike)
        assertEquals(userWithBike, deserializedBike)
    }
}