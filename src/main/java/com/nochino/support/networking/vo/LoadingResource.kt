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

package com.nochino.support.networking.vo

import com.nochino.support.networking.vo.Status.ERROR
import com.nochino.support.networking.vo.Status.LOADING
import com.nochino.support.networking.vo.Status.SUCCESS

/**
 * A generic class that holds a value with its loading status.
 *
 * Borrowed from Google Architecture sample project
 * [android-architecture-components](https://github.com/googlesamples/android-architecture-components/blob/88747993139224a4bb6dbe985adf652d557de621/GithubBrowserSample/app/src/main/java/com/android/example/github/vo/Resource.kt)
 *
 * Renamed this class from "Resource" to "LoadingResource" because it's a more descriptive
 * class name than just "Resource"
 *
 * @param <T>
</T> */
data class LoadingResource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): LoadingResource<T> {
            return LoadingResource(SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): LoadingResource<T> {
            return LoadingResource(ERROR, data, msg)
        }

        fun <T> loading(data: T?): LoadingResource<T> {
            return LoadingResource(LOADING, data, null)
        }
    }
}
