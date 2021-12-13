package com.skvoznyak.findart.model

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PictureRepository {

    private const val knnBaseUrl = "https://a63c-185-165-219-190.ngrok.io"

    private val retrofit = Retrofit.Builder()
        .baseUrl(knnBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val knnApi = retrofit.create(KnnApi::class.java)

    fun getSimilarPictures(vector: FloatArray): Single<List<Picture>> {

        val kNeighbors = 6
        val paramObject = JSONObject()
        paramObject.put("vector", vector)
        paramObject.put("k_neighbors", kNeighbors)
        return Single.fromCallable {
            knnApi.knnGetSimilarPictures(paramObject).execute().body() ?: error("Empty response :(")
        }.subscribeOn(Schedulers.io())
    }
}
