package com.example.kreedaankana.data

// Booking — when a team books a ground slot
data class Booking(
    val id: String = "",
    val userId: String = "",
    val teamName: String = "",
    val sport: String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
)

// Challenge — when a team posts a match challenge
data class Challenge(
    val id: String = "",
    val userId: String = "",
    val teamName: String = "",
    val sport: String = "",
    val message: String = "",
    val date: String = "",
)

// Score — match result posted after game
data class Score(
    val id: String = "",
    val userId: String = "",
    val team1: String = "",
    val team2: String = "",
    val score1: Int = 0,
    val score2: Int = 0,
    val sport: String = "",
    val date: String = "",
)