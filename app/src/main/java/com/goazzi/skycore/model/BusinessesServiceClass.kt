package com.goazzi.skycore.model

data class BusinessesServiceClass(
    val businesses: List<Business>,
    val total: Long,
    val region: Region,
    var code: Int,
    var message: String
)
