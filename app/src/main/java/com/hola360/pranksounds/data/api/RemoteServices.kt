package com.hola360.pranksounds.data.api

import com.hola360.pranksounds.data.api.response.call_screen_response.PhoneBookResponse
import com.hola360.pranksounds.data.api.response.detail_category_response.DetailCategoryResponse
import com.hola360.pranksounds.data.api.response.sound_category_response.SoundCategoryResponse
import com.hola360.pranksounds.utils.Constants
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RemoteServices {
    @FormUrlEncoded
    @POST(Constants.SOUND_FUNNY_PATH)
    suspend fun querySoundCategory(
        @Field("type") params: String
    ): SoundCategoryResponse

    @FormUrlEncoded
    @POST(Constants.SOUND_FUNNY_PATH)
    suspend fun getDetailCategory(
        @Field("type") type: String,
        @Field("category_id") catId: String,
        @Field("page") pageNumber: Int,
        @Field("limit") itemPerPage: Int
    ): DetailCategoryResponse?

    @FormUrlEncoded
    @POST("/soundFuny.php")
    suspend fun getPhoneBook(
        @Field("type") params: String
    ): PhoneBookResponse
}