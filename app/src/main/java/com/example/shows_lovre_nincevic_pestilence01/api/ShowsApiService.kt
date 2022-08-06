package com.example.shows_lovre_nincevic_pestilence01.api

import com.example.shows_lovre_nincevic_pestilence01.api.requests.LoginRequest
import com.example.shows_lovre_nincevic_pestilence01.api.requests.PostReviewRequest
import com.example.shows_lovre_nincevic_pestilence01.api.requests.RegisterRequest
import com.example.shows_lovre_nincevic_pestilence01.api.responses.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ShowsApiService {

    @POST("/users")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("/users/sign_in")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @GET("/shows")
    fun getShows(): Call<ShowsResponse>

    @GET("/shows/top_rated")
    fun getTopRatedShows(): Call<ShowsResponse>

    @GET("/users/me")
    fun getCurrentUser(): Call<CurrentUserResponse>

    @GET("/shows/{id}")
    fun getCurrentShow(
        @Path("id") id: String,
    ): Call<CurrentShowResponse>

    @GET("/shows/{show_id}/reviews")
    fun getReviews(
        @Path("show_id") id: String
    ): Call<ReviewsResponse>

    @POST("/reviews")
    fun postReviews(
        @Body request: PostReviewRequest
    ): Call<PostReviewResponse>

    @POST("/users")
    @Multipart
    fun updateProfilePhoto(
        @Part formData: MultipartBody.Part
    ): Call<UpdateProfilePhotoResponse>
}