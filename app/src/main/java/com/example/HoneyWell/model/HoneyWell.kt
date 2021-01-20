package com.example.HoneyWell.model

data class HoneyWellNewsData(
    val exhaustiveNbHits: Boolean,
    val hits: List<Hit>,
    val hitsPerPage: Int,
    val nbHits: Int,
    val nbPages: Int,
    val page: Int,
    val params: String,
    val processingTimeMS: Int,
    val query: String
)

data class Hit(
    val _highlightResult: HighlightResult,
    val _tags: List<String>,
    val author: String,
    val comment_text: Any,
    val created_at: String,
    val created_at_i: Int,
    val num_comments: Int,
    val objectID: String,
    val parent_id: Any,
    val points: Int,
    val story_id: Any,
    val story_text: Any,
    val story_title: Any,
    val story_url: Any,
    val title: String,
    val url: String
)

data class HighlightResult(
    val author: Author,
    val story_text: StoryText,
    val title: Title,
    val url: Url
)

data class Author(
    val matchLevel: String,
    val matchedWords: List<Any>,
    val value: String
)

data class StoryText(
    val matchLevel: String,
    val matchedWords: List<Any>,
    val value: String
)

data class Title(
    val matchLevel: String,
    val matchedWords: List<Any>,
    val value: String
)

data class Url(
    val matchLevel: String,
    val matchedWords: List<Any>,
    val value: String
)