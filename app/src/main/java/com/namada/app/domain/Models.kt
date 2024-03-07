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

import com.namada.app.network.PubKey
import com.namada.app.util.smartTruncate
import com.squareup.moshi.Json

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
data class Block(val title: String,
                 val description: String,
                 val url: String,
                 val updated: String,
                 val thumbnail: String) {

    /**
     * Short description is used for displaying truncated descriptions in the UI
     */
    val shortDescription: String
        get() = description.smartTruncate(200)
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

