package com.nochino.support.networking.api

/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import retrofit2.Call
import retrofit2.Response
import timber.log.Timber
import java.util.regex.Pattern

/**
 * Common class used by API responses.
 *
 * Borrowed from Google Architecture sample project
 * [android-architecture-components](https://github.com/googlesamples/android-architecture-components/blob/88747993139224a4bb6dbe985adf652d557de621/GithubBrowserSample/app/src/main/java/com/android/example/github/api/ApiResponse.kt)
 *
 * @param <T> the type of the response object</T>
 *
 */
@Suppress("unused", "UNUSED_PARAMETER") // T is used in extending classes
sealed class ApiResponse<T>(call: Call<T>, response: Response<T>?) {

    companion object {

        fun <T> create(call: Call<T>, error: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(call, null, error.message ?: "unknown error")
        }

        fun <T> create(call: Call<T>, response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                if (body == null || response.code() == 204) {
                    ApiEmptyResponse(call, response)
                } else {
                    ApiSuccessResponse(
                        call = call,
                        response = response,
                        body = body,
                        linkHeader = response.headers()?.get("link")
                    )
                }
            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }
                ApiErrorResponse(call, response, errorMsg ?: "unknown error")
            }
        }
    }
}

/**
 * separate class for HTTP 204 responses so that we can make ApiSuccessResponse's body non-null.
 */
class ApiEmptyResponse<T>(
    call: Call<T>,
    response: Response<T>
) : ApiResponse<T>(call, response)

@Suppress("unused")
data class ApiSuccessResponse<T>(
    val call: Call<T>,
    val response: Response<T>,
    val body: T,
    val links: Map<String, String>
) : ApiResponse<T>(call, response) {
    constructor(call: Call<T>, response: Response<T>, body: T, linkHeader: String?) : this(
        call = call,
        response = response,
        body = body,
        links = linkHeader?.extractLinks() ?: emptyMap()
    )

    val nextPage: Int? by lazy(LazyThreadSafetyMode.NONE) {
        links[NEXT_LINK]?.let { next ->
            val matcher = PAGE_PATTERN.matcher(next)
            if (!matcher.find() || matcher.groupCount() != 1) {
                null
            } else {
                try {
                    Integer.parseInt(matcher.group(1))
                } catch (ex: NumberFormatException) {
                    Timber.w("cannot parse next page from %s", next)
                    null
                }
            }
        }
    }

    companion object {
        private val LINK_PATTERN = Pattern.compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"")
        private val PAGE_PATTERN = Pattern.compile("\\bpage=(\\d+)")
        private const val NEXT_LINK = "next"

        private fun String.extractLinks(): Map<String, String> {
            val links = mutableMapOf<String, String>()
            val matcher = LINK_PATTERN.matcher(this)

            while (matcher.find()) {
                val count = matcher.groupCount()
                if (count == 2) {
                    links[matcher.group(2)] = matcher.group(1)
                }
            }
            return links
        }

    }
}

data class ApiErrorResponse<T>(
    val call: Call<T>,
    val response: Response<T>?,
    val errorMessage: String
) : ApiResponse<T>(call, response)
