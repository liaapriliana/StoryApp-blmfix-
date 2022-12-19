package com.intermediate.storyapp1.data.presenter

import com.google.gson.annotations.SerializedName
import com.intermediate.storyapp1.data.model.Story

data class ResultStories(
    @field:SerializedName("listStory")
    val listStory: ArrayList<Story>? = null,
    @field:SerializedName("message")
    val message: String? = null,
    @field:SerializedName("error")
    val error: Boolean? = null
)
