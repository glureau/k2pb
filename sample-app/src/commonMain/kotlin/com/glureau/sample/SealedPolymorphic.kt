package com.glureau.sample

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("Vehicle")
@Serializable
sealed class Vehicle {
    @SerialName("Vehicle.Car")
    @Serializable
    data class Car(val brand: String) : Vehicle()
    @SerialName("Vehicle.Bike")
    @Serializable
    data class Bike(val brand: String) : Vehicle()
}

@Serializable
data class User(val name: String, val vehicle: Vehicle? = null)
