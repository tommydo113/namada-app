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
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi

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
@JsonClass(generateAdapter = true)
data class NetworkNProposalContainer(val proposals: List<NProposal>)

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
            yayVotes = (it.yayVotes?: "0").toDouble(),
            nayVotes = (it.nayVotes?: "0").toDouble(),
            abstainVotes = (it.abstainVotes?: "0").toDouble(),
            details = "" //no need
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
data class NProposal (

     @Json(name="proposal_id" ) var proposalId : String,
     @Json(name="type"        ) var type       : String,
     @Json(name="author"      ) var author     : String,
     @Json(name="content"     ) var content    : String? = null,
     @Json(name="start_epoch" ) var startEpoch : String,
     @Json(name="end_epoch"   ) var endEpoch   : String,
     @Json(name="grace_epoch" ) var graceEpoch : String,
     @Json(name="result"      ) var result     : String? = null

)
/*
    val id           : Int,
    val content      : String,
    val kind         : String,
    val authorAddress       : String,
    val startEpoch   : Int,
    val endEpoch     : Int,
    val graceEpoch   : Int,
    val result       : String,
    val yayVotes     : Long,
    val nayVotes     : Long,
    val abstainVotes : Long
 */
fun NProposal.asProposalModel(): Proposal {
    val moshi: Moshi = Moshi.Builder().build()
    val jsonAdapter: JsonAdapter<ProposalContent> = moshi.adapter(ProposalContent::class.java)
    val proposalContent = this.content?.replace("\\\"", "\"")?.let { jsonAdapter.fromJson(it) }
    val resultAdapter: JsonAdapter<ProposalResult> = moshi.adapter(ProposalResult::class.java)
    val proposalResult = this.result?.replace("\\\"", "\"")?.let { resultAdapter.fromJson(it) }
    return Proposal(
        id = this.proposalId.toInt(),
        content = proposalContent?.title ?: "",
        kind = this.type,
        authorAddress =  this.author,
        startEpoch = this.startEpoch.toInt(),
        endEpoch = this.endEpoch.toInt(),
        graceEpoch = this.graceEpoch.toInt(),
        result = proposalResult?.outcome ?: "",
        yayVotes = proposalResult?.yay ?: 0.0,
        nayVotes = proposalResult?.nay ?: 0.0,
        abstainVotes = proposalResult?.abstain ?: 0.0,
        details = this.content?.replace("\\\"", "\"")
    )
}

fun List<NProposal>.asProposalModelList(): List<Proposal>{
    return map { it.asProposalModel() }
}
@JsonClass(generateAdapter = true)
data class ProposalContent (

     @Json(name="abstract"       ) var abstract       : String? = null,
     @Json(name="authors"        ) var authors        : String? = null,
     @Json(name="created"        ) var created        : String? = null,
     @Json(name="details"        ) var details        : String? = null,
     @Json(name="discussions-to" ) var discussionsTo : String? = null,
     @Json(name="license"        ) var license        : String? = null,
     @Json(name="motivation"     ) var motivation     : String? = null,
     @Json(name="requires"       ) var requires       : String? = null,
     @Json(name="title"          ) var title          : String? = null

)

@JsonClass(generateAdapter = true)
data class ProposalResult (
     @Json(name="outcome"            ) var outcome          : String? = null,
     @Json(name="yay"                ) var yay              : Double? = null,
     @Json(name="nay"                ) var nay              : Double? = null,
     @Json(name="abstain"            ) var abstain          : Double? = null,
     @Json(name="total_voting_power" ) var totalVotingPower : Double? = null,
     @Json(name="threshold"          ) var threshold        : Double? = null

)
/*
    {
      "proposal_id": "666",
      "type": "Default",
      "author": "tnam1qrnk6tymtq8p2enttnewcg9efph7pk6atvaw8p6r",
      "content": "{\"abstract\":\"\", \"authors\":\"Devil, Satan, etc\", \"created\":\"2024-04-10T03:55:01Z\", \"details\":\"no\", \"discussions-to\":\"\", \"license\":\"HELL\", \"motivation\":\"evil only\", \"requires\":\"666\", \"title\":\"Devil's proposal😈\"}",
      "start_epoch": "88",
      "end_epoch": "90",
      "grace_epoch": "92",
      "result": "{\"outcome\":\"rejected\",\"yay\":49909594.698851,\"nay\":25414694.805497,\"abstain\":2776156.188282,\"total_voting_power\":268897900.697092,\"threshold\":179265267.131216}"
    }
 */

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
@JsonClass(generateAdapter = true)
data class TxSearch (

    @Json(name="pageProps" ) var pageProps : PagePropsTx? = PagePropsTx(),
    @Json(name="__N_SSP"   ) var _NSSP     : Boolean?   = null

)
@JsonClass(generateAdapter = true)
data class PagePropsTx (

    @Json(name="hash"        ) var hash        : String?      = null,
    @Json(name="transaction" ) var transaction : TransactionSearch? = TransactionSearch()

)
@JsonClass(generateAdapter = true)
data class TxResult (

    @Json(name="code"       ) var code      : Int?              = null,
    @Json(name="data"       ) var data      : String?           = null,
    @Json(name="log"        ) var log       : String?           = null,
    @Json(name="info"       ) var info      : String?           = null,
    @Json(name="gas_wanted" ) var gasWanted : String?           = null,
    @Json(name="gas_used"   ) var gasUsed   : String?           = null,
    @Json(name="events"     ) var events    : List<String> = emptyList(),
    @Json(name="codespace"  ) var codespace : String?           = null

)
@JsonClass(generateAdapter = true)
data class TransactionSearch (

    @Json(name="hash"      ) var hash     : String?   = null,
    @Json(name="height"    ) var height   : String?   = null,
    @Json(name="index"     ) var index    : Int?      = null,
    @Json(name="tx_result" ) var txResult : TxResult? = TxResult(),
    @Json(name="tx"        ) var tx       : String?   = null

)

fun TxSearch.toTransactionModel(): Transaction {
    return Transaction(
        hash = this.pageProps?.transaction?.hash ?: "",
        status =  this.pageProps?.transaction?.txResult?.code ?: 0,
        height = this.pageProps?.transaction?.height?.toInt() ?: 0,
        gasUsed = this.pageProps?.transaction?.txResult?.gasUsed?.toInt() ?: 0 ,
        gasWanted = this.pageProps?.transaction?.txResult?.gasWanted?.toInt() ?: 0
    )

}