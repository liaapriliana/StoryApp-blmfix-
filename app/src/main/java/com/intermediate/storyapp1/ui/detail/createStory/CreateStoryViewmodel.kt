package com.intermediate.storyapp1.ui.detail.createStory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.intermediate.storyapp1.data.api.ApiConfig
import com.intermediate.storyapp1.data.model.CreateStoryResponse
import com.intermediate.storyapp1.data.model.LoginPreference
import com.intermediate.storyapp1.data.utils.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CreateStoryViewmodel (application: Application): AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    fun postCreateStory(imageFile: File, desc: String) {
        _isLoading.value = true

        val file = reduceFileImage(imageFile as File)

        val description = desc.toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )

        val token = LoginPreference(context).getUser().token

        val client = token?.let {
            ApiConfig.getApiService().createStory(
                token = "Bearer $it",
                file =  imageMultipart,
                description = description,
                lat = 0F,
                lon = 0F
            )
        }

        client?.enqueue(object : Callback<CreateStoryResponse> {
            override fun onResponse(
                call: Call<CreateStoryResponse>,
                response: Response<CreateStoryResponse>
            ) {
                if (response.body()?.error == false) {
                    _isLoading.value = false
                    _isError.value = false
                } else {
                    _isError.value = true
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<CreateStoryResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
            }
        })
    }
}