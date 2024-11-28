package com.glureau.sample

import com.glureau.k2pb.ProtoPolymorphism
import com.glureau.k2pb.ProtoPolymorphism.Pair
import com.glureau.k2pb.annotation.ProtoMessage

// Declaration of the polymorphic hierarchy is still required to generate the serializer with expected numbers.
// Evolution: allow an annotation/param to better support sealed class/interface.
@ProtoPolymorphism(
    Vehicle::class,
    [
        Pair(Vehicle.Car::class, 1),
        Pair(Vehicle.Bike::class, 2),
    ]
)
// @ProtoMessage("Vehicle")
sealed class Vehicle {
    @ProtoMessage("Vehicle.Car")
    data class Car(val brand: String) : Vehicle()

    @ProtoMessage("Vehicle.Bike")
    data class Bike(val brand: String) : Vehicle()
}

@ProtoMessage
data class User(val name: String, val vehicle: Vehicle? = null)
