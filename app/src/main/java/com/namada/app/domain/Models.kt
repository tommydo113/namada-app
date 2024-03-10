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

import com.namada.app.util.smartTruncate

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
                 val height: Int,
                 val txCount: String
    ) {

    val displayTime: String
        get() = time
}
data class Validator(val address: String,
                     val moniker: String,
                     val operatorAddress: String,
                     val votingPower: Long,
                     val votingPercentage: Double,
                     val proposerPriority: String,
                    val pubKeyType: String,
                    val pubKeyValue : String
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
    val yayVotes     : String,
    val nayVotes     : String,
    val abstainVotes : String
)