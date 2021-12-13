package com.skvoznyak.findart.model

import com.google.gson.annotations.SerializedName

data class Picture(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("painter")
    val painter: String,
    @SerializedName("year")
    val year: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("text")
    val text: String,
)
