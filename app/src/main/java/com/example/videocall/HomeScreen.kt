package com.example.videocall

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }

    var showJoinDialog by remember { mutableStateOf(false) }
    var meetingCode by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val vibrationManager = remember { VibrationManager(context) } //initializing vibration

    if (showJoinDialog) {
        AlertDialog(
            onDismissRequest = { showJoinDialog = false },
            title = { Text("Join Meeting") },
            text = {
                Column {
                    OutlinedTextField(
                        value = meetingCode,
                        onValueChange = {
                            meetingCode = it
                            errorMessage = ""
                        },
                        label = { Text("Meeting Code") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = userName,
                        onValueChange = {
                            userName = it
                            errorMessage = ""
                        },
                        label = { Text("Your Name") },
                        singleLine = true
                    )
                    if (errorMessage.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    vibrationManager.triggerVibration()
                    if (meetingCode.isBlank() || userName.isBlank()) {
                        errorMessage = "Please fill in all fields."
                    } else {
                        showJoinDialog = false
                        errorMessage = ""
                        joinMeeting(context, meetingCode, userName)
                    }
                }) {
                    Text("Join")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    vibrationManager.triggerVibration()
                    showJoinDialog = false
                    errorMessage = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                vibrationManager.triggerVibration()
                startMeeting(auth, db, context) { error ->
                    errorMessage = error
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Instant Meeting")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                vibrationManager.triggerVibration(150)
                showJoinDialog = true
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Join Meeting")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                vibrationManager.triggerVibration(150)
                errorMessage = "Schedule Meeting feature coming soon."
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Schedule Meeting")
        }

        if (errorMessage.isNotBlank()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

private fun startMeeting(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    context: Context,
    onError: (String) -> Unit
) {
    val currentUser = auth.currentUser
    if (currentUser == null) {
        onError("You must be signed in to start a meeting.")
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

    db.collection("meetings")
        .document(meetingId)
        .set(meeting)
        .addOnSuccessListener {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("MEETING_ID", meetingId)
            context.startActivity(intent)
        }
        .addOnFailureListener { e ->
            onError("Failed to start meeting: ${e.message}")
        }
}

private fun joinMeeting(
    context: Context,
    meetingId: String,
    userName: String
) {
    val intent = Intent(context, JoinActivity::class.java)
    intent.putExtra("MEETING_ID", meetingId)
    intent.putExtra("USER_NAME", userName)
    context.startActivity(intent)
}
