package com.example.videocall

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import io.getstream.video.android.compose.permission.LaunchCallPermissions
import io.getstream.video.android.compose.theme.StreamColors
import io.getstream.video.android.compose.theme.StreamDimens
import io.getstream.video.android.compose.theme.StreamShapes
import io.getstream.video.android.compose.theme.StreamTypography
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.theme.VideoTheme.colors
import io.getstream.video.android.compose.theme.VideoTheme.dimens
import io.getstream.video.android.compose.ui.components.call.activecall.CallContent
import io.getstream.video.android.compose.ui.components.call.controls.ControlActions
import io.getstream.video.android.compose.ui.components.call.controls.actions.FlipCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.LeaveCallAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleMicrophoneAction
import io.getstream.video.android.compose.ui.components.call.renderer.FloatingParticipantVideo
import io.getstream.video.android.compose.ui.components.call.renderer.ParticipantVideo
import io.getstream.video.android.compose.ui.components.video.VideoRenderer
import io.getstream.video.android.core.GEO
import io.getstream.video.android.core.RealtimeConnection
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.model.User
import kotlinx.coroutines.launch




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiKey = "mmhfdzb5evj2"
        val userToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL3Byb250by5nZXRzdHJlYW0uaW8iLCJzdWIiOiJ1c2VyL01hY2VfV2luZHUiLCJ1c2VyX2lkIjoiTWFjZV9XaW5kdSIsInZhbGlkaXR5X2luX3NlY29uZHMiOjYwNDgwMCwiaWF0IjoxNzUzMjgzOTU5LCJleHAiOjE3NTM4ODg3NTl9.ktghTrv-pkoCYtDiag-zcFDNiW1ePatEYy3ugWMdZUo"
        val userId = "Mace_Windu"
        val callId = "fBQSrBnGRdZF"



        // Create a user
        val user = User(
            id = userId, // any string
            name = "Tutorial", // name and image are used in the UI
            image = "https://bit.ly/2TIt8NR",
        )

        StreamVideo.removeClient()

        // Initialize StreamVideo. For a production app, we recommend adding the client to your Application class or di module.
        val client = StreamVideoBuilder(
            context = applicationContext,
            apiKey = apiKey,
            geo = GEO.GlobalEdgeNetwork,
            user = user,
            token = userToken,
        ).build()

        setContent {
            // Request permissions and join a call, which type is default and id is 123.
            val call = client.call(type = "default", id = callId)
            LaunchCallPermissions(
                call = call,
                onAllPermissionsGranted = {
                    // All permissions are granted so that we can join the call.
                    val result = call.join(create = true)
                    result.onError {
                        Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            )

            // Apply VideoTheme
            VideoTheme {
                val isCameraEnabled by call.camera.isEnabled.collectAsState()
                val isMicrophoneEnabled by call.microphone.isEnabled.collectAsState()


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(top = 16.dp) // optional spacing
                )

                CallContent(
                    modifier = Modifier.background(color = Color.White),
                    call = call,
                    onBackPressed = { onBackPressed() },

                    controlsContent = {
                        ControlActions(
                            call = call,
                            actions = listOf(
                                {
                                    ToggleCameraAction(
                                        modifier = Modifier.size(52.dp),
                                        isCameraEnabled = isCameraEnabled,
                                        onCallAction = { call.camera.setEnabled(it.isEnabled) }
                                    )
                                },
                                {
                                    ToggleMicrophoneAction(
                                        modifier = Modifier.size(52.dp),
                                        isMicrophoneEnabled = isMicrophoneEnabled,
                                        onCallAction = { call.microphone.setEnabled(it.isEnabled) }
                                    )
                                },
                                {
                                    FlipCameraAction(
                                        modifier = Modifier.size(52.dp),
                                        onCallAction = { call.camera.flip() }
                                    )
                                },

                                {
                                    LeaveCallAction(
                                        modifier = Modifier.size(52.dp),
                                        onCallAction = {
                                            call.leave()
                                            finish()
                                        }
                                    )
                                },
                                {
                                    IconButton(
                                        onClick = {
                                            val shareIntent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    "Join my video call: myapp://join?meeting_id=$callId"
                                                )
                                                type = "text/plain"
                                            }
                                            val chooser = Intent.createChooser(
                                                shareIntent,
                                                "Share call link"
                                            )
                                            startActivity(chooser)
                                        },
                                        modifier = Modifier.size(52.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share",
                                            tint = Color.White
                                        )
                                    }

                                })
                        )
                    }
                )
            }
        }
    }
}