package com.example.shows_lovre_nincevic_pestilence01.api

import com.example.shows_lovre_nincevic_pestilence01.api.requests.LoginRequest
import com.example.shows_lovre_nincevic_pestilence01.api.requests.PostReviewRequest
import com.example.shows_lovre_nincevic_pestilence01.api.requests.RegisterRequest
import com.example.shows_lovre_nincevic_pestilence01.api.responses.*
import retrofit2.Call
import retrofit2.http.*

interface ShowsApiService {

    @POST("/users")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("/users/sign_in")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @Headers("token-type: Bearer")
    @GET("/shows")
    fun getShows(@Header("access-token") accessToken: String, @Header("client") client: String, @Header("uid") uid: String): Call<ShowsResponse>

    @Headers("token-type: Bearer")
    @GET("/users/me")
    fun getCurrentUser(@Header("access-token") accessToken: String, @Header("client") client: String, @Header("uid") uid: String): Call<CurrentUserResponse>

    @Headers("token-type: Bearer")
    @GET("/shows/{id}")
    fun getCurrentShow(@Path("id") id: String, @Header("access-token") accessToken: String, @Header("client") client: String, @Header("uid") uid: String): Call<CurrentShowResponse>

    @Headers("token-type: Bearer")
    @GET("/shows/{show_id}/reviews")
    fun getReviews(@Path("show_id") id: String, @Header("access-token") accessToken: String, @Header("client") client: String, @Header("uid") uid: String): Call<ReviewsResponse>

    @Headers("token-type: Bearer")
    @POST("/reviews")
    fun postReviews(@Header("access-token") accessToken: String, @Header("client") client: String, @Header("uid") uid: String, @Body request: PostReviewRequest): Call<PostReviewResponse>

    @POST("/users")
    fun updateProfilePhoto(@Header("access-token") accessToken: String, @Header("client") client: String, @Header("uid") uid: String, @Field("image") path: String ): Call<UpdateProfilePhotoResponse>
}