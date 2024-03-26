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

import com.namada.app.domain.Block
import com.namada.app.domain.Proposal
import com.namada.app.domain.Transaction
import com.namada.app.domain.Validator
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 *
 * @see domain package for
 */

/**
 * BlockHolder holds a list of Blocks.
 *
 * This is to parse first level of our network result which looks like
 *
 * {
 *   "videos": []
 * }
 */
@JsonClass(generateAdapter = true)
data class NetworkBlockContainer(val blocks: List<NetworkBlock>)

@JsonClass(generateAdapter = true)
data class NetworkValidatorContainer(val validators: List<NetworkValidator>)

@JsonClass(generateAdapter = true)
data class NetworkProposalContainer(val proposals: List<NetworkProposal>)

/**
 * Block represent a block on the network.
 */

@JsonClass(generateAdapter = true)
data class NetworkBlock (

    @Json(name="hash"     ) var hash     : String?   = null,
    @Json(name="height"   ) var height   : Int?      = null,
    @Json(name="time"     ) var time     : String?   = null,
    @Json(name="proposer" ) var proposer : Proposer? = Proposer(),
    @Json(name="txs"      ) var txs      : Int?      = null

)
//,
//    @Json(name="signatures" ) var signatures : ArrayList<Signatures>? = arrayListOf()
@JsonClass(generateAdapter = true)
data class Proposer (
    @Json(name="address" ) var address : String? = null,
    @Json(name="moniker" ) var moniker : String? = null
)

/**
{
"hash": "B96CDE120FD1D97713F59332BE3703A09C5FD29A3B1261B2BB339B7FAB6A4384",
"height": 101935,
"time": "2024-03-10T05:34:53.378144267Z",
"proposer": {
"address": "0E1CFCF387B1CDBCD642323F3B94E67D6B2F931A",
"moniker": "Feles-GATADAO"
},
"txs": 0
}
 */

/**
 * Convert Network results to database objects
 */
fun List<NetworkBlock>.asDomainModel(): List<Block> {
    return map {
        Block(
                hash = it.hash ?: "",
                time = it.time ?: "",
                proposerAddress = it.proposer?.address ?: "",
                proposerMoniker = it.proposer?.moniker ?: "",
                height = it.height ?: 0,
                txCount = it.txs ?: 0)
    }
}

fun BlockSearch.toBlockModel(): Block {
    return Block(
        hash = pageProps?.block?.hash ?: "",
        time = pageProps?.block?.time ?: "",
        proposerAddress =  pageProps?.block?.proposer?.address ?: "",
        proposerMoniker = pageProps?.block?.proposer?.moniker ?: "",
        height = pageProps?.block?.height ?: 0,
        txCount = pageProps?.block?.txs?.size ?: 0
    )
}

fun List<NetworkValidator>.asValidatorModel(): List<Validator>{
    return map{
        Validator(operatorAddress = it.operatorAddress ?: "",
            hexAddress = it.hexAddress ?: "",
                    moniker = it.moniker ?: "",
            votingPercentage = it.votingPercentage ?: 0.0,
            tokens =  it.tokens ?: 0,
            cumulativeShare = it.cumulativeShare ?: 0.0,
            rank = it.rank ?: 0

        )
    }
}


@JsonClass(generateAdapter = true)
data class NetworkValidator (

    @Json(name="operator_address"     ) var operatorAddress    : String? = null,
    @Json(name="hex_address"          ) var hexAddress         : String? = null,
    @Json(name="moniker"              ) var moniker            : String? = null,
    @Json(name="tokens"               ) var tokens             : Long?    = null,
    @Json(name="cumulative_share"     ) var cumulativeShare    : Double? = null,
    @Json(name="voting_power_percent" ) var votingPercentage : Double? = null,
    @Json(name="rank"                 ) var rank               : Int?    = null

)
fun List<NetworkProposal>.asProposalModel(): List<Proposal>{
    return map {
        Proposal(
            id = it.id ?: 0,
            content = it.content?: "",
            kind = it.kind ?: "",
            authorAddress = it.author?.account ?: "",
            startEpoch = it.startEpoch ?: 0,
            endEpoch =  it.endEpoch ?: 0,
            graceEpoch = it.graceEpoch ?: 0,
            result = it.result?: "",
            yayVotes = (it.yayVotes?: "0").toLong(),
            nayVotes = (it.nayVotes?: "0").toLong(),
            abstainVotes = (it.abstainVotes?: "0").toLong()
        )
    }
}
@JsonClass(generateAdapter = true)
data class NetworkProposal (

    @Json(name="id"            ) var id           : Int?    = null,
    @Json(name="content"       ) var content      : String? = null,
    @Json(name="kind"          ) var kind         : String? = null,
    @Json(name="author"        ) var author       : Author? = Author(),
    @Json(name="start_epoch"   ) var startEpoch   : Int?    = null,
    @Json(name="end_epoch"     ) var endEpoch     : Int?    = null,
    @Json(name="grace_epoch"   ) var graceEpoch   : Int?    = null,
    @Json(name="result"        ) var result       : String? = null,
    @Json(name="yay_votes"     ) var yayVotes     : String? = null,
    @Json(name="nay_votes"     ) var nayVotes     : String? = null,
    @Json(name="abstain_votes" ) var abstainVotes : String? = null

)

@JsonClass(generateAdapter = true)
data class Author (
    @Json(name="Account" ) var account : String? = null
)
@JsonClass(generateAdapter = true)
data class NetworkTransaction (
    val hash: String?,
    val status: Int?,
    val height: Int?,
    val gasWanted: Int?,
    val gasUsed: Int?
)

fun List<NetworkTransaction>.asTransactionModel(): List<Transaction>{
    return map { Transaction(
        hash = it.hash ?: "",
        status =  it.status ?: 0,
        height = it.height ?: 0,
        gasWanted =  it.gasWanted ?: 0,
        gasUsed =  it.gasUsed ?: 0
    ) }
}
@JsonClass(generateAdapter = true)
data class BlockSearch (
    @Json(name="pageProps" ) var pageProps : PageProps? = PageProps()
)
@JsonClass(generateAdapter = true)
data class Signatures (
    @Json(name="address" ) var address : String? = null,
    @Json(name="moniker" ) var moniker : String? = null
)
@JsonClass(generateAdapter = true)
data class PageProps (

    @Json(name="block"  ) var block  : BlockTxResponse? = null,
    @Json(name="height" ) var height : Int?   = null

)
@JsonClass(generateAdapter = true)
data class BlockTxResponse (

    @Json(name="hash"       ) var hash       : String?               = null,
    @Json(name="height"     ) var height     : Int?                  = null,
    @Json(name="time"       ) var time       : String?               = null,
    @Json(name="proposer"   ) var proposer   : Proposer?             = Proposer(),
    @Json(name="txs"        ) var txs        : List<String>?     = emptyList(),
    @Json(name="signatures" ) var signatures : List<Signatures>? = emptyList()

)

