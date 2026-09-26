package com.example.agent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "",
    val preferredName: String = "",
    val pronouns: String = "",
    val dob: String = "",
    val phone: String = "",
    val email: String = "",
    val homeAddress: String = "",
    val workAddress: String = "",
    val jobTitle: String = "",
    val employer: String = "",
    val socialHandles: String = "",
    val emergencyContact: String = "",
    val shippingAddress: String = "",
    val billingAddress: String = "",
    val dietaryPreferences: String = "",
    val emailSignature: String = "",
    val preferredTone: String = "formal",
    val timeZone: String = "UTC",
    val language: String = "en",
    val currency: String = "USD",
    val monthlySpendLimit: Double = 50.0,
    val termsAccepted: Boolean = false,
    val onboardingCompleted: Boolean = false
)
