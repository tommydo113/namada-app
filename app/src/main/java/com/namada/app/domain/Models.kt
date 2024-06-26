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

package com.namada.app.domain

import com.namada.app.network.ProposalContent
import com.namada.app.util.formatTime
import com.namada.app.util.smartTruncate
import java.io.Serializable

/**
 * Domain objects are plain Kotlin data classes that represent the things in our app. These are the
 * objects that should be displayed on screen, or manipulated by the app.
 *
 * @see database for objects that are mapped to the database
 * @see network for objects that parse or prepare network calls
 */

/**
 * Blocks represent a Block that can be played.
 */
data class Block(val hash: String,
                 val time: String,
                 val proposerAddress: String,
                 val proposerMoniker: String,
                 val height: Int,
                 val txCount: Int
    ) : Serializable{

    val displayTime: String
        get() = formatTime(time)
}
data class Validator(val operatorAddress: String,
                     val hexAddress: String,
                     val moniker: String,
                     val votingPercentage: Double,
                        val tokens: Long,
    val cumulativeShare: Double,
    val rank: Int
                    )


data class Proposal (
    val id           : Int,
    val content      : String,
    val kind         : String,
    val authorAddress       : String,
    val startEpoch   : Int,
    val endEpoch     : Int,
    val graceEpoch   : Int,
    val result       : String,
    val yayVotes     : Double,
    val nayVotes     : Double,
    val abstainVotes : Double,
    val details: String?
): Serializable

data class Transaction (
    val hash: String,
    val status: Int,
    val height: Int,
    val gasWanted: Int,
    val gasUsed: Int
): Serializable
/**
 *  {
 *         "hash": "8C265342AF9589F483B68CE1F34030EC8B1AEF5CBFEDE4A8F20A848132F71D92",
 *         "status": 0,
 *         "height": 103239,
 *         "gasWanted": 0,
 *         "gasUsed": 0
 *     }
 */