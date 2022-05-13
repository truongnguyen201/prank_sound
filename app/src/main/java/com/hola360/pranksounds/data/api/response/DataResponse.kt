package com.hola360.pranksounds.data.api.response

sealed class DataResponse<T> constructor(val loadingStatus: LoadingStatus) {
    class DataLoading<T>(private val loadingType : LoadingStatus) : DataResponse<T>(loadingType)
    class DataIdle<T> : DataResponse<T>(LoadingStatus.Idle)
    class DataError<T> : DataResponse<T>(LoadingStatus.Error)
    data class DataSuccess<T>(val body: T) : DataResponse<T>(LoadingStatus.Success)
}