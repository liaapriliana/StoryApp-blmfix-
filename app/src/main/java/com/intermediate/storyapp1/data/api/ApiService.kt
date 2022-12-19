package com.intermediate.storyapp1.data.api

import com.intermediate.storyapp1.data.model.CreateStoryResponse
import com.intermediate.storyapp1.data.model.LoginResponse
import com.intermediate.storyapp1.data.model.RegisterResponse
import com.intermediate.storyapp1.data.model.StoryResponse
import com.intermediate.storyapp1.data.presenter.ResultStories
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    fun login(@Field("email") email : String?,
              @Field("password") password: String?
    ) : Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(@Field("name") name : String?,
                 @Field("email") email : String?,
                 @Field("password") password : String?
    ) : Call<RegisterResponse>

    @GET("stories")
    fun stories(@Query("location") location : Int?
    ) : Call<ResultStories>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int
    ) : Call<StoryResponse>

    @Multipart
    @POST("stories")
    fun createStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float,
        @Part("lon") lon: Float,
    ) : Call<CreateStoryResponse>
}