package com.skvoznyak.findart.model

import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface KnnApi {
    @POST("get_similar")
    fun knnGetSimilarPictures(
        @Body jsonObject: JSONObject
    ):
        Call<List<Picture>>
}
