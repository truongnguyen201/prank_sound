package com.hola360.pranksounds.data.api

import com.hola360.pranksounds.data.api.response.call_screen_response.PhoneBookResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface RemoteServices {
    @FormUrlEncoded
    @POST("/soundFuny.php")
    suspend fun getPhoneBook(
        @Field ("type") params: String
    ): PhoneBookResponse
}