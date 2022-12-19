package com.intermediate.storyapp1.data.presenter

import com.intermediate.storyapp1.data.model.Story

interface StoriesView {
    fun onSuccessStories (msg : String?, data : ArrayList<Story>?)
    fun onFailedStories (msg : String?)
}