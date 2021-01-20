package com.example.HoneyWell.news_api


import com.example.HoneyWell.model.HoneyWellNewsData
import io.reactivex.Observable
import retrofit2.http.GET

interface TopHeadlinesEndpoint {

    @GET("search?tags=front_page")
    fun getHoneyWellNews(
    ): Observable<HoneyWellNewsData>

//    @GET("top-headlines")
//    fun getUserSearchInput(
//        @Query("apiKey") apiKey: String,
//        @Query("q") q: String
//    ): Observable<TopHeadlines>
}