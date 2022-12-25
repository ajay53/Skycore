package com.goazzi.skycore.misc

object Enum{
    enum class SortByEnum(val type: String) {
        BEST_MATCH("best_match"),
        RATING("rating"),
        REVIEW_COUNT("review_count"),
        DISTANCE("distance")
    }

    enum class PermissionEnum {
        GPS, LOCATION
    }
}