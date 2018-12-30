package com.nochino.retronetworking

/**
 * Simple object used to maintain a cache of a data object
 */
data class DataCache<T>(val t: T) {
    var cache = t
}