package com.thanhdv.workchecker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thanhdv.workchecker.data.ConfigRepository
import com.thanhdv.workchecker.network.WebhookClient
import com.thanhdv.workchecker.ui.theme.WorkCheckerTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class NFCUiState {
    object Loading : NFCUiState()
    data class Success(val preview: String) : NFCUiState()
    data class Error(val reason: String) : NFCUiState()
}

class NFCResultActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorkCheckerTheme {
                NfcResultScreen(
                    onSendRequest = ::sendWebhook,
                    onDismiss = ::finish
                )
            }
        }
    }

    private suspend fun sendWebhook(): NFCUiState {
        val config = ConfigRepository(this).configFlow.first()

        if (config.webhookURL.isBlank()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return NFCUiState.Loading
        }

        val result = WebhookClient.send(config)
        return if (result.isSuccess) {
            NFCUiState.Success(WebhookClient.resolveMessage(config))
        } else {
            NFCUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
        }
    }
}

@Composable
fun NfcResultScreen(
    onSendRequest: suspend () -> NFCUiState,
    onDismiss: () -> Unit
) {
    var state: NFCUiState by remember { mutableStateOf(NFCUiState.Loading) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        state = onSendRequest()
        if (state is NFCUiState.Success) {
            delay(2_000)
            onDismiss()
        }
    }

    if (state !is NFCUiState.Loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    enabled = state is NFCUiState.Success,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {},
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                AnimatedContent(
                    targetState = state,
                    label = "nfc_result_state"
                ) { currentState ->
                    when (currentState) {
                        is NFCUiState.Loading -> {}
                        is NFCUiState.Success -> SuccessContent(currentState.preview)
                        is NFCUiState.Error -> ErrorContent(
                            reason = currentState.reason,
                            onRetry = {
                                state = NFCUiState.Loading
                                scope.launch {
                                    state = onSendRequest()
                                    if (state is NFCUiState.Success) {
                                        delay(3_000)
                                        onDismiss()
                                    }
                                }
                            },
                            onDismiss = onDismiss
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuccessContent(preview: String) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = "Success",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }

        Text(
            text = "Sent!",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                text = preview,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }

        Text(
            text = "Auto-closing in 2s",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun ErrorContent(
    reason: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(36.dp)
            )
        }

        Text(
            text = "Failed to send",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = reason,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }

        Column(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Retry") }

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Dismiss") }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSuccess() {
    WorkCheckerTheme {
        SuccessContent(preview = "John Doe (EMP123) checked at 08:30 15/10/2023")
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewError() {
    WorkCheckerTheme {
        ErrorContent(reason = "Timeout: No internet connection", onRetry = {}, onDismiss = {})
    }
}