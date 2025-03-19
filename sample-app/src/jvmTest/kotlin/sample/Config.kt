package sample

import com.glureau.k2pb.runtime.K2PB
import com.glureau.sample.lib.registerSampleLibSerializers
import com.glureau.sample.registerSampleAppSerializers


val serializer = K2PB {
    registerSampleLibSerializers()
    registerSampleAppSerializers()
    // registerBuilder(MigrationAddFieldAfterDynamicSC("runtime value"))
}