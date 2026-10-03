package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

internal fun stripPartialTrailingMarkdown(input: String): String {
  var s = input

  run {
    val lastOpen = s.lastIndexOf('[')
    val lastClose = s.lastIndexOf(']')
    if (lastOpen > lastClose) s = s.substring(0, lastOpen)
  }

  run {
    val openParenAfterClose = Regex("""]\(""").findAll(s).lastOrNull()?.range?.first
    if (openParenAfterClose != null) {
      val tail = s.substring(openParenAfterClose + 2)
      if (!tail.contains(')')) {
        val matchingBracket = s.lastIndexOf('[', openParenAfterClose)
        s = s.substring(0, if (matchingBracket >= 0) matchingBracket else openParenAfterClose)
      }
    }
  }

  if (s.endsWith("*") && !s.endsWith("**")) {
    s = s.dropLast(1)
  }

  val boldCount = Regex("\\*\\*").findAll(s).count()
  if (boldCount % 2 == 1) {
    val lastOpener = s.lastIndexOf("**")
    if (lastOpener >= 0) s = s.substring(0, lastOpener)
  }

  return s
}

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
