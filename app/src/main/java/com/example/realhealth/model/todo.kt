package com.example.realhealth.model

import kotlinx.serialization.Serializable

@Serializable
data class todo(
    val date: String,
    val name: String,
    val content: String
)
