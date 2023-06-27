package com.axiel7.anihyou.data.model.media

import com.axiel7.anihyou.MediaRelationsAndRecommendationsQuery

data class MediaRelationsAndRecommendations(
    val relations: List<MediaRelationsAndRecommendationsQuery.Edge>,
    val recommendations: List<MediaRelationsAndRecommendationsQuery.Node>,
)
