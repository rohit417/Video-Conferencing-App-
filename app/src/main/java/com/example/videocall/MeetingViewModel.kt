package com.example.videocall


import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videocall.JoinActivity
import com.example.videocall.MainActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.*

class MeetingViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun startMeeting(context: Context) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val meetingId = UUID.randomUUID().toString()
        val meeting = hashMapOf(
            "meetingId" to meetingId,
            "hostUid" to currentUser.uid,
            "hostEmail" to currentUser.email,
            "startTime" to Timestamp.now(),
            "status" to "active"
        )

        viewModelScope.launch {
            db.collection("meetings")
                .document(meetingId)
                .set(meeting)
                .addOnSuccessListener {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("MEETING_ID", meetingId)
                    context.startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    fun joinMeeting(context: Context, meetingId: String, userName: String) {
        if (meetingId.isBlank() || userName.isBlank()) {
            Toast.makeText(context, "Meeting code and name required", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(context, JoinActivity::class.java)
        intent.putExtra("MEETING_ID", meetingId)
        intent.putExtra("USER_NAME", userName)
        context.startActivity(intent)
    }
}
