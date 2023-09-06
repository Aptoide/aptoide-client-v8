package com.aptoide.aptoide_ab_testing.model

import com.google.gson.JsonObject

// getFlag
data class Flag(
    val id: Long,
    val key: String,
    val description: String,
    val enabled: Boolean,
    val tags: List<Tag>,
    val segments: List<Segment>,
    val variants: List<Variant>,
    val dataRecordsEnabled: Boolean,
    val entityType: String,
    val notes: String,
    val createdBy: String,
    val updatedBy: String,
    val updatedAt: String
)

data class Tag (
    val id: Long,
    val value: String
)

data class Segment (
    val id: Long,
    val description: String,
    val constraints: List<Constraint>,
    val distributions: List<Distribution>,
    val rank: Long,
    val rolloutPercent: Long
)

data class Constraint (
    val id: Long,
    val property: String,
    val operator: String,
    val value: String
)

data class Distribution (
    val id: Long,
    val percent: Long,
    val variantKey: String,
    val variantID: Long
)

data class Variant (
    val id: Long,
    val key: String,
    val attachment: Attachment
)

typealias Attachment = JsonObject

// postEvaluation
data class PostEvaluationResponseJson (
    val flagID: Long,
    val flagKey: String,
    val flagSnapshotID: Long,
    val segmentID: Long,
    val variantID: Long,
    val variantKey: String,
    val variantAttachment: VariantAttachment,
    val evalContext: EvalContext,
    val timestamp: String,
    val evalDebugLog: EvalDebugLog
)

typealias VariantAttachment = JsonObject

// Same as the request body to postEvaluation (= PostEvaluationRequestJson)
data class EvalContext (
    val entityID: String,
    val entityType: String,
    val entityContext: VariantAttachment,
    val enableDebug: Boolean,
    val flagID: Long,
    val flagKey: String,
    val flagTags: List<String>,
    val flagTagsOperator: String
)

data class EvalDebugLog (
    val segmentDebugLogs: List<SegmentDebugLog>,
    val msg: String
)

data class SegmentDebugLog (
    val segmentID: Long,
    val msg: String
)
