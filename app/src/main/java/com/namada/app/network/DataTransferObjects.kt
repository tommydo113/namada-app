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
data class NetworkValidatorContainer(val currentValidatorsList: List<NetworkValidator>)

/**
 * Block represent a block on the network.
 */
@JsonClass(generateAdapter = true)
data class NetworkBlock(
    val block_id: String? = null,
    val header_version_app: Int = 0,
    val header_version_block: Int = 0,
    val header_chain_id: String? = null,
    val header_height: Int = 0,
    val header_time: String? = null,
    val header_last_block_id_hash: String? = null,
    val header_last_block_id_parts_header_total: Int = 0,
    val header_last_block_id_parts_header_hash: String? = null,
    val header_last_commit_hash: String? = null,
    val header_data_hash: String? = null,
    val header_validators_hash: String? = null,
    val header_next_validators_hash: String? = null,
    val header_consensus_hash: String? = null,
    val header_app_hash: String? = null,
    val header_last_results_hash: String? = null,
    val header_evidence_hash: String? = null,
    val header_proposer_address: String? = null,
    val commit_height: Int = 0,
    val commit_round: Int = 0,
    val commit_block_id_hash: String? = null,
    val commit_block_id_parts_header_total: Int = 0,
    val commit_block_id_parts_header_hash: String? = null,
    val transactions_count: String? = null)

/**
 *  "block_id": "4340c605cfc3128383e8ca550b55a9b09868d1e1bcf772ddb7ae9bfc0383c82b",
 *         "header_version_app": 0,
 *         "header_version_block": 11,
 *         "header_chain_id": "shielded-expedition.88f17d1d14",
 *         "header_height": 90044,
 *         "header_time": "2024-02-28T20:17:58.704534371Z",
 *         "header_last_block_id_hash": "eb5fdbb6544cd20dd2798a916b66cad4decdf9331988feecf794952fac4961d0",
 *         "header_last_block_id_parts_header_total": 1,
 *         "header_last_block_id_parts_header_hash": "06d0558348e5553d17243307fe804458e37a038452e5eeee623fff461727a7e5",
 *         "header_last_commit_hash": "25556346a03a32d94fdc5980def4b27b802bb2457b275a3b6712648a3fef7cd3",
 *         "header_data_hash": "21f6517b8f5895c15730e2b9262003bcdc764fdcc7c9e27c0101daa2c8974af9",
 *         "header_validators_hash": "5e85f15672808a4b854281dc1f6a08bb94361a5b408ebcc993a81ecb1bd4e2a1",
 *         "header_next_validators_hash": "5e85f15672808a4b854281dc1f6a08bb94361a5b408ebcc993a81ecb1bd4e2a1",
 *         "header_consensus_hash": "4b53c13521d2126d4e199e783963d0e8b2729f143eb01a7f3817a1d1e079193f",
 *         "header_app_hash": "94FB71CA990FCA243FD9E43BBBF7AA04A4F69065BB74CEEA6510C146788829A5",
 *         "header_last_results_hash": "6e340b9cffb37a989ca544e6bb780a2c78901d3fb33738768511a30617afa01d",
 *         "header_evidence_hash": "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
 *         "header_proposer_address": "ECCB99DE2BB6889DE0F6F57F4A1FB6F1656A4B8A",
 *         "commit_height": 90043,
 *         "commit_round": 0,
 *         "commit_block_id_hash": "eb5fdbb6544cd20dd2798a916b66cad4decdf9331988feecf794952fac4961d0",
 *         "commit_block_id_parts_header_total": 1,
 *         "commit_block_id_parts_header_hash": "06d0558348e5553d17243307fe804458e37a038452e5eeee623fff461727a7e5",
 *         "transactions_count": "2"
 *     },
 */

/**
 * Convert Network results to database objects
 */
fun List<NetworkBlock>.asDomainModel(): List<Block> {
    return map {
        Block(
                title = it.block_id ?: "",
                description = it.header_app_hash ?: "",
                url = it.header_chain_id ?: "",
                updated = "it.",
                thumbnail = "it.thumbnail")
    }
}

fun List<NetworkValidator>.asValidatorModel(): List<Validator>{
    return map{
        Validator(address = it.address ?: "",
                    moniker = it.moniker ?: "",
                operatorAddress = it.operatorAddress ?: "",
            votingPower = it.votingPower ?: 0,
            proposerPriority = it.proposerPriority ?: "",
            votingPercentage = it.votingPercentage ?: 0.0,
            pubKeyType = it.pubKey?.type ?: "",
            pubKeyValue = it.pubKey?.value ?: ""
        )
    }
}


@JsonClass(generateAdapter = true)
data class PubKey (
    @Json(name="type"  ) var type  : String? = null,
    @Json(name="value" ) var value : String? = null
)

@JsonClass(generateAdapter = true)
data class NetworkValidator (
    @Json(name="address"           ) var address          : String? = null,
    @Json(name="pub_key"           ) var pubKey           : PubKey? = PubKey(),
    @Json(name="voting_power"      ) var votingPower      : Long?    = null,
    @Json(name="proposer_priority" ) var proposerPriority : String? = null,
    @Json(name="voting_percentage" ) var votingPercentage : Double? = null,
    @Json(name="moniker"           ) var moniker          : String? = null,
    @Json(name="operator_address"  ) var operatorAddress  : String? = null

)
/**
 *     {
 *             "address": "2C8300C6D4EE7F9641963DF7B7F53391CC172CB0",
 *             "pub_key": {
 *                 "type": "tendermint/PubKeyEd25519",
 *                 "value": "M5cVI72wX67qbBWmEVbOZqXmUFs8WelMMKb7dpEn7TM="
 *             },
 *             "voting_power": 2692877300000,
 *             "proposer_priority": "47342885438620",
 *             "voting_percentage": 1.7204354248369806,
 *             "moniker": "EmberStake",
 *             "operator_address": "tnam1q9k0jkssxmwdd0g3vpulvl7yp3rv7rnnuyezutcp"
 *         }
 */