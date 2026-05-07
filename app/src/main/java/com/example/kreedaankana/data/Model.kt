package com.example.kreedaankana.data

data class Booking(
    val id: String = "",
    val userId: String = "",
    val teamName: String = "",
    val sport: String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
)

data class Challenge(
    val id: String = "",
    val userId: String = "",
    val teamName: String = "",
    val sport: String = "",
    val message: String = "",
    val date: String = "",
    val status: String = "OPEN",        // "OPEN" or "ACCEPTED"
    val acceptedByTeam: String = "",    // team name who accepted
    val acceptedByUserId: String = "",  // userId who accepted
    val reply: String = ""              // reply message
)

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

// NEW — user profile
data class UserProfile(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val teamName: String = "",
    val teamId: String = "",   // which team user belongs to
    val photoUrl: String = ""
)

// ONE member inside a team
data class TeamMember(
    val userId: String = "",
    val displayName: String = "",
    val joinedAt: String = ""
)

// the team itself
data class Team(
    val id: String = "",
    val teamName: String = "",
    val sport: String = "",
    val captainId: String = "",
    val captainName: String = "",
    val members: List<TeamMember> = emptyList(),
    val createdAt: String = ""
)