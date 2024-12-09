package sample.kt

import CollectionTypeEventOuterClass
import com.glureau.sample.CollectionTypeEvent
import com.glureau.sample.lib.DataClassFromLib
import dataClassFromLib
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class CollectionType : BaseEncodingTest() {

    @Test
    fun checkCollectionTypeEvent() {
        assertCompatibleSerialization(
            ktInstance = CollectionTypeEvent(
                integerList = listOf(1, 3, 5),
                stringList = listOf("aaa", "bbb", "ccc"),
                maybeIntegerList = listOf(42, 51),
                mapStringInt = mapOf(
                    "a" to 2,
                    "b" to 4,
                ),
                dataClassList = listOf(DataClassFromLib(33), DataClassFromLib(34)),
            ),
            protocInstance = CollectionTypeEventOuterClass.CollectionTypeEvent.newBuilder()
                // Randomized order => preserve proto number sorting in serialization
                .addIntegerList(1)
                .addDataClassList(
                    dataClassFromLib {
                        myInt = 33
                    }
                )
                .addIntegerList(3)
                .addDataClassList(
                    dataClassFromLib {
                        myInt = 34
                    }
                )
                .putMapStringInt("a", 2)
                .addIntegerList(5)
                .putMapStringInt("b", 4)
                .addMaybeIntegerList(42)
                .addMaybeIntegerList(51)
                .addStringList("aaa")
                .addStringList("bbb")
                .addStringList("ccc")
                .build()
        )
    }


    @Test
    fun defaultScalarAreStillEncoded() {
        assertCompatibleSerialization(
            ktInstance = CollectionTypeEvent(
                integerList = listOf(0, 0, 0),
                stringList = listOf("", "", ""),
                maybeIntegerList = listOf(0, 0),
                mapStringInt = mapOf(
                    "" to 0,
                ),
                dataClassList = listOf(DataClassFromLib(0)),
            ),
            protocInstance = CollectionTypeEventOuterClass.CollectionTypeEvent.newBuilder()
                .addIntegerList(0)
                .addIntegerList(0)
                .addIntegerList(0)
                .addStringList("")
                .addStringList("")
                .addStringList("")
                .addMaybeIntegerList(0)
                .addMaybeIntegerList(0)
                .putMapStringInt("", 0)
                .addDataClassList(
                    dataClassFromLib {
                        myInt = 0
                    }
                )
                .build()
        )
    }

}