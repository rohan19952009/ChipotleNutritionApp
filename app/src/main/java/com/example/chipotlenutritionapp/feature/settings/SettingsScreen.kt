package com.example.chipotlenutritionapp.feature.settings

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.chipotlenutritionapp.workers.ReminderWorker
import java.util.concurrent.TimeUnit

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    var notificationsEnabled by remember { mutableStateOf(checkNotificationPermission(context)) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        notificationsEnabled = isGranted
        if (isGranted) {
            scheduleReminders(context)
        } else {
            cancelReminders(context)
        }
    }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("App Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Column {
                Text("Daily Reminders", style = MaterialTheme.typography.titleMedium)
                Text("Remind me to track meals", style = MaterialTheme.typography.bodySmall)
            }
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { checked ->
                    if (checked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else if (checked) {
                        notificationsEnabled = true
                        scheduleReminders(context)
                    } else {
                        notificationsEnabled = false
                        cancelReminders(context)
                    }
                }
            )
        }
        
        Divider(Modifier.padding(vertical = 16.dp))
        
        Text("About & Web", style = MaterialTheme.typography.titleMedium)
        
        Spacer(Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth().clickable {
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://chipotlenutritioncalculator.ai/"))
                context.startActivity(intent)
            },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Visit Official Website", style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("chipotlenutritioncalculator.ai", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        
        Spacer(Modifier.height(16.dp))
        Text("Version 2.0 - Premium Edition", style = MaterialTheme.typography.bodyMedium)
        Text("Not affiliated with Chipotle. Local persistent state architecture.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun checkNotificationPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }
    return true
}

private fun scheduleReminders(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
        .build()
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "daily_reminder",
        ExistingPeriodicWorkPolicy.KEEP,
        workRequest
    )
}

private fun cancelReminders(context: Context) {
    WorkManager.getInstance(context).cancelUniqueWork("daily_reminder")
}
