package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoMessage

@ProtoMessage("Vehicle")
sealed class Vehicle {
    @ProtoMessage("Vehicle.Car")
    data class Car(val brand: String) : Vehicle()

    @ProtoMessage("Vehicle.Bike")
    data class Bike(val brand: String) : Vehicle()
}

@ProtoMessage
data class User(val name: String, val vehicle: Vehicle? = null)

@ProtoMessage(
    name = "Vehicle2",
    sealedProtoNumbers = [
        ProtoMessage.SealedChild(Vehicle2.Car2::class, 3),
        ProtoMessage.SealedChild(Vehicle2.Bike2::class, 2),
    ]
)
sealed class Vehicle2 {
    @ProtoMessage("Vehicle2.Car2")
    data class Car2(val brand: String) : Vehicle2()

    @ProtoMessage("Vehicle2.Bike2")
    data class Bike2(val brand: String) : Vehicle2()
}

@ProtoMessage
data class User2(val name: String, val vehicle: Vehicle2? = null)

