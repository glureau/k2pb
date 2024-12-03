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
