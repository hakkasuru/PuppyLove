package com.example.androiddevchallenge.models

data class Puppy(
    val name: String,
    val age: Int,
    val breed: String,
    val bio: String,
    val likes: List<String>,
    val image: Int,
    val verified: Boolean
)
