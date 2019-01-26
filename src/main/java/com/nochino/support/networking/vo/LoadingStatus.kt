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

/**
 * LoadingStatus of a resource that is provided to the UI.
 *
 *
 * These are usually created by the Repository classes where they return
 * `LiveData<LoadingResource<T>>` to pass back the latest data to the UI with its fetch loadingStatus.
 *
 * Renamed this class from "Status" to "LoadingStatus" because it's a more descriptive
 * class name than just "Status"
 *
 * Borrowed from Google Architecture sample project
 * [android-architecture-components](https://github.com/googlesamples/android-architecture-components/blob/88747993139224a4bb6dbe985adf652d557de621/GithubBrowserSample/app/src/main/java/com/android/example/github/vo/Status.kt)
 */
enum class LoadingStatus {
    SUCCESS,
    ERROR,
    LOADING
}
