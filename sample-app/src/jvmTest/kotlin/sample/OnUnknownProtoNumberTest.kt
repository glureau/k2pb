package sample

import com.glureau.k2pb.runtime.K2PB
import com.glureau.k2pb.runtime.decodeFromByteArray
import com.glureau.k2pb.runtime.encodeToByteArray
import com.glureau.sample.AbstractClass
import com.glureau.sample.AbstractSubClass
import com.glureau.sample.AfterAddingField
import com.glureau.sample.BeforeAddingField
import com.glureau.sample.Vehicle
import com.glureau.sample.lib.registerSampleLibCodecs
import com.glureau.sample.registerSampleAppCodecs
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class OnUnknownProtoNumberTest {

    val unknownCalls = mutableListOf<Pair<KClass<*>, Int>>()

    @Test
    fun `basic backward compatibility`() {
        val serializer = K2PB {
            registerSampleLibCodecs()
            registerSampleAppCodecs()
            onUnknownProtoNumber = { instanceClass, protoNumber ->
                println("Unknown proto number $protoNumber for class ${instanceClass.simpleName}")
                unknownCalls += instanceClass to protoNumber
            }
        }

        val serialized = serializer.encodeToByteArray<AfterAddingField>(AfterAddingField("a", 42))
        val deserialized = serializer.decodeFromByteArray<BeforeAddingField>(serialized)
        // old format is still deserializable, but 42 is lost
        assertEquals(BeforeAddingField("a"), deserialized)
        assertEquals(listOf<Pair<KClass<*>, Int>>(BeforeAddingField::class to 2), unknownCalls)
    }


    @Test
    fun `default backward compatibility`() {
        val serializer = K2PB {
            registerSampleLibCodecs()
            registerSampleAppCodecs()
            // No onUnknownProtoNumber defined here, a std log will be printed and that's it.
        }

        val serialized = serializer.encodeToByteArray<AfterAddingField>(AfterAddingField("a", 42))
        val deserialized = serializer.decodeFromByteArray<BeforeAddingField>(serialized)
        // old format is still deserializable, but 42 is lost
        assertEquals(BeforeAddingField("a"), deserialized)
    }



    @Test
    fun `backward compatibility can be blocked if needed`() {
        val serializer = K2PB {
            registerSampleLibCodecs()
            registerSampleAppCodecs()
            onUnknownProtoNumber = { instanceClass, protoNumber ->
                error("I want to block backward compatibility ${instanceClass.simpleName} // $protoNumber")
            }
        }

        val serialized = serializer.encodeToByteArray<AfterAddingField>(AfterAddingField("a", 42))
        val deserialized = try {
            serializer.decodeFromByteArray<BeforeAddingField>(serialized)
        } catch (e: IllegalStateException) {
            assertEquals("I want to block backward compatibility BeforeAddingField // 2", e.message)
            return // skip the fail below
        }
        fail("You should not be able to deserialize if throwing in onUnknownProtoNumber, got $deserialized")
    }
}