package com.example.ry0000tarodojo2026.data.model

data class ChannelResponse(
    val items: List<ChannelItem>?
)

data class ChannelItem(
    val id: String,
    val snippet: ChannelSnippet
)

data class ChannelSnippet(
    val title: String,
    val thumbnails: Thumbnails
)