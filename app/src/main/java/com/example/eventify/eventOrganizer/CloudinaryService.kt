package com.example.eventify.eventOrganizer


import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

data class CloudinaryUploadResponse(
    val secure_url: String
)

interface CloudinaryService {
    @Multipart
    @POST("v1_1/dmnyoycod/image/upload")
    fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("upload_preset") preset: RequestBody
    ): Call<CloudinaryUploadResponse>
}
