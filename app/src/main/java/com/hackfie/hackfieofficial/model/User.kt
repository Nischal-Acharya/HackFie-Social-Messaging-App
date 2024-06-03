package com.hackfie.hackfieofficial.model

import java.io.Serializable

data class User(
    var bio: String = "",
    var dob: String = "",
    var email: String = "",
    var followers: List<String> = listOf(), // Updated to a list of strings
    var following: List<String> = listOf(), // Updated to a list of strings
    var gender: String = "",
    var name: String = "",
    var password: String = "",
    var posts: Int = 0,
    var profile: String = "",
    var registeredOn: String = "",
    var status: String = "",
    var uid: String = ""
) : Serializable
