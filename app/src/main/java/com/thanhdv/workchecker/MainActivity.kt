package com.thanhdv.workchecker

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.font.FontWeight
import com.thanhdv.workchecker.data.UserConfig
import com.thanhdv.workchecker.ui.theme.ConfigViewModel
import com.thanhdv.workchecker.ui.theme.WorkCheckerTheme
import android.os.Bundle
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorkCheckerTheme {
                ConfigScreen()
            }
        }
    }
}

@Composable
fun ConfigScreen(vm: ConfigViewModel = viewModel()) {
    val saved by vm.config.collectAsStateWithLifecycle()
    ConfigScreenContent(
        saved = saved,
        onSave = { config -> vm.saveConfig(config) }
    )
}

@Composable
fun ConfigScreenContent(
    saved: UserConfig,
    onSave: (UserConfig) -> Unit
) {
    val context = LocalContext.current

    var name by remember(saved) { mutableStateOf(saved.name) }
    var email by remember(saved) { mutableStateOf(saved.email) }
    var empId by remember(saved) { mutableStateOf(saved.employeeId) }
    var webhook by remember(saved) { mutableStateOf(saved.webhookURL) }
    var payload by remember(saved) { mutableStateOf(saved.payload) }
    var message by remember(saved) { mutableStateOf(saved.message) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 56.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Work Checker",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Configuration", style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = empId, onValueChange = { empId = it },
                label = { Text("Employee ID") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = webhook, onValueChange = { webhook = it },
                label = { Text("Webhook URL") },
                // Hide URL like a password because it contains sensitive tokens
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
//            OutlinedTextField(
//                value = message, onValueChange = { message = it },
//                label = { Text("Message") },
//                supportingText = { Text("Variables: {name} {empId} {time}") },
//                minLines = 2,
//                modifier = Modifier.fillMaxWidth()
//            )
            OutlinedTextField(
                value = payload, onValueChange = { payload = it },
                label = { Text("Payload") },
                supportingText = { Text("Use {message} as placeholder") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    onSave(UserConfig(name, email, empId, webhook, payload, message))
                    Toast.makeText(context, "Saved ✓", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save Configuration") }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ConfigScreenPreview() {
    WorkCheckerTheme(dynamicColor = false) {
        ConfigScreenContent(saved = UserConfig(), onSave = {})
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ConfigScreenDarkPreview() {
    WorkCheckerTheme(dynamicColor = false) {
        ConfigScreenContent(saved = UserConfig(), onSave = {})
    }
}

@Preview(showBackground = true, device = "spec:width=360dp,height=640dp")
@Composable
fun ConfigScreenSmallPreview() {
    WorkCheckerTheme(dynamicColor = false) {
        ConfigScreenContent(saved = UserConfig(), onSave = {})
    }
}