package com.example.realhealth.model

import kotlinx.serialization.Serializable

@Serializable
data class todo(
    val data: String,
    val classify: String,
    val index: Int,
    val content: String
)
