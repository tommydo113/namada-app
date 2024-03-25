/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.namada.app.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Since we only have one service, this can all go in one file.
// If you add more services, split this to multiple files and make sure to share the retrofit
// object between services.


interface AppService {
    @GET("api/blocks?count=40")
    suspend fun getBlocklist(): List<NetworkBlock>

    @GET("api/validators")
    suspend fun getValidatorList(): NetworkValidatorContainer

    @GET("api/transactions")
    suspend fun getTransaction(@Query("count") count: Int = 10): List<NetworkTransaction>

    @GET("api/blocks/{height}/txs")
    suspend fun getTransactionsOfBlock(@Path("height")height: Int): List<NetworkTransaction>

    @GET("api/blocks")
    suspend fun getNextBlockList(@Query("height") height: Int, @Query("count") count: Int): List<NetworkBlock>
    @GET("api/transactions")
    suspend fun getNextTxList(@Query("height")height: Int,  @Query("count")count: Int = 10): List<NetworkTransaction>

    @GET("_next/data/DZE3ZfbWa5njyFz9xYSPa/block/{he}.json")
    suspend fun searchByBlockHeight(@Path("he")he: String, @Query("height")height: String): BlockSearch
}

object AppNetwork {

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
            .baseUrl("https://namada.api.explorers.guru/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    val appService = retrofit.create(AppService::class.java)

}

object AppNetwork3 {

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://namada.explorers.guru/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val appService = retrofit.create(AppService::class.java)

}

interface AppService2 {
    @GET("api/v1/chain/governance/proposals")
    suspend fun getProposalList(): NetworkProposalContainer
}
object AppNetwork2 {
    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://it.api.namada.red/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val appService = retrofit.create(AppService2::class.java)
}


