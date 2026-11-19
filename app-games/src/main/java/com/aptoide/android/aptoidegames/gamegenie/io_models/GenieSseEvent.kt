package com.aptoide.android.aptoidegames.gamegenie.io_models

import androidx.annotation.Keep

/**
 * Server-Sent Events emitted by the streaming `/api/genie` endpoint.
 *
 * Wire format (per event, terminated by a blank line):
 *
 *   data: {"type": "meta",        "id": "..."}
 *   data: {"type": "delta",       "text": "..."}
 *   data: {"type": "apps",        "apps": [...]}
 *   data: {"type": "video",       "video": "..." | null}
 *   data: {"type": "follow_ups",  "follow_ups": ["...", "...", "..."]}
 *   data: {"type": "done",        "id": "...", "title": "...", "apps": [...], "video": "...",
 *                                 "follow_ups": [...]}
 *   data: {"type": "error",       "message": "..."}
 *
 * Ordering: `meta` -> `delta` x N -> ([Apps] | [Video] | [FollowUps]) in arbitrary order, each
 * at most once -> ([Done] | [Error]).
 *
 * The typed structured events ([Apps], [Video], [FollowUps]) are additive — their payloads are
 * also duplicated inside [Done] for backwards compatibility.
 */
sealed class GenieSseEvent {
  @Keep
  data class Meta(val id: String) : GenieSseEvent()

  @Keep
  data class Delta(val text: String) : GenieSseEvent()

  /**
   * Entries may be fully enriched (icon/rating/downloads via the Aptoide bulk API) or minimal
   * (name + package only). Callers must tolerate both by resolving via the apps repository.
   */
  @Keep
  data class Apps(val apps: List<GenieAppRef>) : GenieSseEvent()

  @Keep
  data class Video(val videoId: String?) : GenieSseEvent()

  @Keep
  data class FollowUps(val followUps: List<String>) : GenieSseEvent()

  /** [title] is only ever delivered here — there is no `title` typed event. */
  @Keep
  data class Done(
    val id: String,
    val title: String?,
    val apps: List<GenieAppRef>,
    val video: String?,
    val followUps: List<String> = emptyList(),
  ) : GenieSseEvent()

  @Keep
  data class Error(val message: String?) : GenieSseEvent()
}

@Keep
data class GenieAppRef(
  val name: String?,
  val packageName: String,
)
