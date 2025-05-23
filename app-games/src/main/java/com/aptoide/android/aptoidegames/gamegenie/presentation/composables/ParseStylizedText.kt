package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

fun parseStylizedText(input: String): List<TextSegment> {
  val segments = mutableListOf<TextSegment>()
  val boldRegex = Regex("\\*\\*(.*?)\\*\\*")
  val linkRegex = Regex("""\(?\[([^\]]+)]\((https?://[^)]+)\)\)?""")

  val matches = (boldRegex.findAll(input).map { it to "bold" } +
    linkRegex.findAll(input).map { it to "link" })
    .sortedBy { it.first.range.first }

  var lastIndex = 0

  for ((match, type) in matches) {
    val start = match.range.first
    val end = match.range.last + 1

    if (start > lastIndex) {
      segments.add(TextSegment.Plain(input.substring(lastIndex, start)))
    }

    when (type) {
      "bold" -> segments.add(TextSegment.Bold(match.groupValues[1]))
      "link" -> segments.add(TextSegment.Link(match.groupValues[1], match.groupValues[2]))
    }

    lastIndex = end
  }

  if (lastIndex < input.length) {
    segments.add(TextSegment.Plain(input.substring(lastIndex)))
  }

  return segments
}
