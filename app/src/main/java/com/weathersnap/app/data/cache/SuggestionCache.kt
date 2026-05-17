package com.weathersnap.app.data.cache

import com.weathersnap.app.domain.model.CitySuggestion
import javax.inject.Inject
import javax.inject.Singleton

// Simple in-memory cache so we don't repeat the same API call
@Singleton
class SuggestionCache @Inject constructor() {

    private val store = mutableMapOf<String, List<CitySuggestion>>()

    fun get(query: String): List<CitySuggestion>? = store[query.trim().lowercase()]

    fun put(query: String, value: List<CitySuggestion>) {
        store[query.trim().lowercase()] = value
    }

    fun clear() = store.clear()
}
