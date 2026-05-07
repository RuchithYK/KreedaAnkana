package com.example.kreedaankana.repository

import com.example.kreedaankana.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class FirebaseRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun getCurrentUser() = auth.currentUser
    fun signOut() = auth.signOut()

    // ─────────────────────────────────────────
    // USER PROFILE
    // ─────────────────────────────────────────

    fun getUserProfile(userId: String): Flow<UserProfile?> = callbackFlow {
        val listener = db.collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val profile = snapshot?.toObject(UserProfile::class.java)
                trySend(profile)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        db.collection("users")
            .document(profile.userId)
            .set(profile)
            .await()
    }

    suspend fun updateTeamName(userId: String, teamName: String) {
        db.collection("users")
            .document(userId)
            .update("teamName", teamName)
            .await()
    }

    suspend fun deleteAllUserData(userId: String) {
        val bookings = db.collection("bookings")
            .whereEqualTo("userId", userId).get().await()
        bookings.documents.forEach { it.reference.delete().await() }

        val challenges = db.collection("challenges")
            .whereEqualTo("userId", userId).get().await()
        challenges.documents.forEach { it.reference.delete().await() }

        val scores = db.collection("scores")
            .whereEqualTo("userId", userId).get().await()
        scores.documents.forEach { it.reference.delete().await() }

        db.collection("users").document(userId).delete().await()
        auth.currentUser?.delete()?.await()
    }

    // ─────────────────────────────────────────
    // TEAMS
    // ─────────────────────────────────────────

    // get all teams — for join team screen
    fun getAllTeams(): Flow<List<Team>> = callbackFlow {
        val listener = db.collection("teams")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val teams = snapshot?.documents?.map { doc ->
                    doc.toObject(Team::class.java)!!.copy(id = doc.id)
                } ?: emptyList()
                trySend(teams)
            }
        awaitClose { listener.remove() }
    }

    // get ONE specific team by id
    fun getTeam(teamId: String): Flow<Team?> = callbackFlow {
        val listener = db.collection("teams")
            .document(teamId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val team = snapshot?.toObject(Team::class.java)?.copy(id = snapshot.id)
                trySend(team)
            }
        awaitClose { listener.remove() }
    }

    // create new team
    suspend fun createTeam(
        teamName: String,
        sport: String,
        captainId: String,
        captainName: String
    ): String {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val team = Team(
            teamName = teamName,
            sport = sport,
            captainId = captainId,
            captainName = captainName,
            members = listOf(
                TeamMember(
                    userId = captainId,
                    displayName = captainName,
                    joinedAt = today
                )
            ),
            createdAt = today
        )

        // add team to firestore → get the auto generated team id
        val docRef = db.collection("teams").add(team).await()
        val teamId = docRef.id

        // update user profile with teamId and teamName
        db.collection("users").document(captainId).update(
            mapOf(
                "teamId" to teamId,
                "teamName" to teamName
            )
        ).await()

        return teamId
    }

    // join existing team
    suspend fun joinTeam(
        teamId: String,
        userId: String,
        displayName: String,
        teamName: String
    ) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val newMember = mapOf(
            "userId" to userId,
            "displayName" to displayName,
            "joinedAt" to today
        )

        // FieldValue.arrayUnion = add to array without removing existing items
        // like [...existingMembers, newMember] in JS
        db.collection("teams").document(teamId)
            .update("members", FieldValue.arrayUnion(newMember))
            .await()

        // update user profile
        db.collection("users").document(userId).update(
            mapOf(
                "teamId" to teamId,
                "teamName" to teamName
            )
        ).await()
    }

    // leave team
    suspend fun leaveTeam(
        teamId: String,
        userId: String,
        displayName: String
    ) {
        val memberToRemove = mapOf(
            "userId" to userId,
            "displayName" to displayName
        )

        // FieldValue.arrayRemove = remove from array
        db.collection("teams").document(teamId)
            .update("members", FieldValue.arrayRemove(memberToRemove))
            .await()

        // clear teamId and teamName from user profile
        db.collection("users").document(userId).update(
            mapOf(
                "teamId" to "",
                "teamName" to ""
            )
        ).await()
    }

    // get scores for a specific team
    fun getTeamScores(teamName: String): Flow<List<Score>> = callbackFlow {
        val listener = db.collection("scores")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                // filter scores where this team played
                val scores = snapshot?.documents?.mapNotNull { doc ->
                    val score = doc.toObject(Score::class.java)?.copy(id = doc.id)
                    // include score if team1 OR team2 matches
                    if (score?.team1 == teamName || score?.team2 == teamName) score
                    else null
                } ?: emptyList()
                trySend(scores)
            }
        awaitClose { listener.remove() }
    }

    // ─────────────────────────────────────────
    // BOOKINGS
    // ─────────────────────────────────────────

    fun getBookings(): Flow<List<Booking>> = callbackFlow {
        val listener = db.collection("bookings")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val bookings = snapshot?.documents?.map { doc ->
                    doc.toObject(Booking::class.java)!!.copy(id = doc.id)
                } ?: emptyList()
                trySend(bookings)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addBooking(booking: Booking) {
        db.collection("bookings").add(booking).await()
    }

    suspend fun deleteBooking(bookingId: String) {
        db.collection("bookings").document(bookingId).delete().await()
    }

    suspend fun updateBooking(booking: Booking) {
        db.collection("bookings").document(booking.id).set(booking).await()
    }

    // ─────────────────────────────────────────
    // CHALLENGES
    // ─────────────────────────────────────────

    fun getChallenges(): Flow<List<Challenge>> = callbackFlow {
        val listener = db.collection("challenges")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val challenges = snapshot?.documents?.map { doc ->
                    doc.toObject(Challenge::class.java)!!.copy(id = doc.id)
                } ?: emptyList()
                trySend(challenges)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addChallenge(challenge: Challenge) {
        db.collection("challenges").add(challenge).await()
    }

    suspend fun updateChallenge(challenge: Challenge) {
        db.collection("challenges").document(challenge.id).set(challenge).await()
    }

    suspend fun deleteChallenge(challengeId: String) {
        db.collection("challenges").document(challengeId).delete().await()
    }

    // ─────────────────────────────────────────
    // SCORES
    // ─────────────────────────────────────────

    fun getScores(): Flow<List<Score>> = callbackFlow {
        val listener = db.collection("scores")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val scores = snapshot?.documents?.map { doc ->
                    doc.toObject(Score::class.java)!!.copy(id = doc.id)
                } ?: emptyList()
                trySend(scores)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addScore(score: Score) {
        db.collection("scores").add(score).await()
    }

    suspend fun updateScore(score: Score) {
        db.collection("scores").document(score.id).set(score).await()
    }

    suspend fun deleteScore(scoreId: String) {
        db.collection("scores").document(scoreId).delete().await()
    }

    // ─────────────────────────────────────────
    // HOME
    // ─────────────────────────────────────────

    fun getRecentBookings(): Flow<List<Booking>> = callbackFlow {
        val listener = db.collection("bookings")
            .limit(3)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val bookings = snapshot?.documents?.map { doc ->
                    doc.toObject(Booking::class.java)!!.copy(id = doc.id)
                } ?: emptyList()
                trySend(bookings)
            }
        awaitClose { listener.remove() }
    }

    fun getRecentChallenges(): Flow<List<Challenge>> = callbackFlow {
        val listener = db.collection("challenges")
            .limit(2)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val challenges = snapshot?.documents?.map { doc ->
                    doc.toObject(Challenge::class.java)!!.copy(id = doc.id)
                } ?: emptyList()
                trySend(challenges)
            }
        awaitClose { listener.remove() }
    }
}