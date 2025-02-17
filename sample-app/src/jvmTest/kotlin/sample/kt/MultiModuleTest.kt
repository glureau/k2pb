package sample.kt

import com.glureau.k2pb_sample.dataClassFromLib
import com.glureau.k2pb_sample.multiModule
import com.glureau.sample.MultiModule
import com.glureau.sample.lib.DataClassFromLib
import com.glureau.sample.lib.ValueClassFromLib
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class MultiModuleTest : BaseEncodingTest() {

    @Test
    fun standard() {
        assertCompatibleSerialization(
            ktInstance = MultiModule(DataClassFromLib(51), ValueClassFromLib("42")),
            protocInstance = multiModule {
                dataClassFromLib = dataClassFromLib {
                    myInt = 51
                }
                valueClassFromLib = "42"
            }
        )
    }

    @Test
    fun defaultValues() {
        assertCompatibleSerialization(
            ktInstance = MultiModule(DataClassFromLib(0), ValueClassFromLib("")),
            protocInstance = multiModule {
                dataClassFromLib = dataClassFromLib {
                    myInt = 0
                }
                valueClassFromLib = ""
            }
        )
    }

}