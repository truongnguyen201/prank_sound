package com.hola360.pranksounds.data.api

import com.hola360.pranksounds.data.api.response.sound_category_response.SoundCategoryResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RemoteServices {
    @FormUrlEncoded
    @POST("/soundFuny.php")
    suspend fun querySoundCategory(
        @Field("type") params: String
    ): SoundCategoryResponse
}