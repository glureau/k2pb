package com.glureau.sample

import kotlinx.serialization.Serializable

@Serializable
sealed class Vehicle {
    @Serializable
    data class Car(val brand: String) : Vehicle()
    @Serializable
    data class Bike(val brand: String) : Vehicle()
}

@Serializable
data class User(val name: String, val vehicle: Vehicle? = null)
