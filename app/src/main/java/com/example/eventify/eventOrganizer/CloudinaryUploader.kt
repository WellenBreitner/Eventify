package com.example.eventify.eventOrganizer


import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


fun uploadImageToCloudinary(imageFile: File, onResult: (String?) -> Unit) {
    val cloudinaryService = Retrofit.Builder()
        .baseUrl("https://api.cloudinary.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CloudinaryService::class.java)

    val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
    val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
    val preset = "YOUR_UPLOAD_PRESET".toRequestBody("text/plain".toMediaTypeOrNull())

    cloudinaryService.uploadImage(body, preset).enqueue(object : Callback<CloudinaryUploadResponse> {
        override fun onResponse(
            call: Call<CloudinaryUploadResponse>,
            response: Response<CloudinaryUploadResponse>
        ) {
            if (response.isSuccessful) {
                onResult(response.body()?.secure_url)
            } else {
                onResult(null)
            }
        }

        override fun onFailure(call: Call<CloudinaryUploadResponse>, t: Throwable) {
            onResult(null)
        }
    })
}
