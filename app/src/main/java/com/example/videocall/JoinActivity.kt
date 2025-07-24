package com.example.videocall

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.getstream.video.android.compose.permission.LaunchCallPermissions
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.call.activecall.CallContent
import io.getstream.video.android.compose.ui.components.call.controls.ControlActions
import io.getstream.video.android.compose.ui.components.call.controls.actions.*
import io.getstream.video.android.core.GEO
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.model.User
import com.example.videocall.VibrationManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.material3.Text
import io.getstream.video.android.core.StreamVideo


class JoinActivity : ComponentActivity() {
    private lateinit var lightSensorManager: LightSensorManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get meeting ID from intent
        val callId = intent.getStringExtra("MEETING_ID") ?: ""
        val userName = intent.getStringExtra("USER_NAME") ?: "Guest"

        lightSensorManager = LightSensorManager(this)
        lightSensorManager.startListening()


        // Only allow joining if callId matches the hardcoded ID
        val allowedCallId = "fBQSrBnGRdZF"
        if (callId != allowedCallId) {
            Toast.makeText(this, "Invalid meeting code.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val apiKey = "mmhfdzb5evj2"
        val userToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL3Byb250by5nZXRzdHJlYW0uaW8iLCJzdWIiOiJ1c2VyL1NhdGVsZV9TaGFuIiwidXNlcl9pZCI6IlNhdGVsZV9TaGFuIiwidmFsaWRpdHlfaW5fc2Vjb25kcyI6NjA0ODAwLCJpYXQiOjE3NTIwNDM3MzQsImV4cCI6MTc1MjY0ODUzNH0.j16czB0Bpr41kzZrQt5WOouG5iny_u6-QnwSOOCLRRU"
        val userId = "Chewbacca"

        val user = User(
            id = userId,
            name = userName,
            image = "https://bit.ly/2TIt8NR",
        )


        val client = StreamVideoBuilder(
            context = applicationContext,
            apiKey = apiKey,
            geo = GEO.GlobalEdgeNetwork,
            user = user,
            token = userToken,
        ).build()

        val call = client.call(type = "default", id = callId)

        setContent {
            val context = this
            val vibrationManager = remember { VibrationManager(context) }
            LaunchCallPermissions(
                call = call,
                onAllPermissionsGranted = {
                    val result = call.join(create = true)
                    result.onError {
                        Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            )

            val lightLevel = remember { mutableStateOf(1000f) }
            Text(
                text = "Light Level: ${lightLevel.value}",
                color = Color.Black,
                modifier = Modifier.padding(8.dp)
            )


            VideoTheme {
                val lightLevel = remember { mutableStateOf(1000f) }

                LaunchedEffect(Unit) {
                    while (true) {
                        lightLevel.value = lightSensorManager.currentLightLevel
                        delay(1000)  // check every second
                    }
                }

                val isCameraEnabled by call.camera.isEnabled.collectAsState()
                val isMicrophoneEnabled by call.microphone.isEnabled.collectAsState()

                val isDark = lightLevel.value < 1000f
                val backgroundColor = if (isDark) Color.Red else Color.White


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .padding(top = 16.dp)
                )

                if (isDark) {
                    Text(
                        text = "⚠️ Low Light Detected!",
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }


                CallContent(
                    modifier = Modifier.background(color = backgroundColor),
                    call = call,
                    onBackPressed = { onBackPressed() },
                    controlsContent = {
                        ControlActions(
                            call = call,
                            actions = listOf(
                                {
                                    ToggleCameraAction(
                                        modifier = Modifier.size(52.dp).semantics {
                                            contentDescription = if (isCameraEnabled) "Turn off camera" else "Turn on camera"
                                        },
                                        isCameraEnabled = isCameraEnabled,
                                        onCallAction = {
                                            vibrationManager.triggerVibration()
                                            call.camera.setEnabled(it.isEnabled) }
                                    )
                                },
                                {
                                    ToggleMicrophoneAction(
                                        modifier = Modifier.size(52.dp).semantics {
                                            contentDescription = if (isMicrophoneEnabled) "Mute microphone" else "Unmute microphone"
                                        },
                                        isMicrophoneEnabled = isMicrophoneEnabled,
                                        onCallAction = {
                                            vibrationManager.triggerVibration()
                                            call.microphone.setEnabled(it.isEnabled) }
                                    )
                                },
                                {
                                    FlipCameraAction(
                                        modifier = Modifier.size(52.dp).semantics { contentDescription = "Flip camera" },
                                        onCallAction = { vibrationManager.triggerVibration()
                                            call.camera.flip() }
                                    )
                                },
                                {
                                    LeaveCallAction(
                                        modifier = Modifier.size(52.dp).semantics { contentDescription = "Leave the call" },
                                        onCallAction = {
                                            vibrationManager.triggerVibration()
                                            call.leave()
                                            finish()
                                        }
                                    )
                                },
                                {
                                    IconButton(
                                        onClick = {
                                            vibrationManager.triggerVibration()
                                            val shareIntent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    "Join my video call: myapp://join?meeting_id=$callId"
                                                )
                                                type = "text/plain"
                                            }
                                            startActivity(Intent.createChooser(shareIntent, "Share call link"))
                                        },
                                        modifier = Modifier.size(52.dp).semantics { contentDescription = "Share call link" }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                }
                            )
                        )
                    }
                )
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        lightSensorManager.stopListening()
    }

}
