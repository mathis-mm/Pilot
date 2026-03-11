package com.example.pilot.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pilot.ui.theme.Primary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

data class GitHubInfo(
    val latestVersion: String,
    val lastCommitMessage: String,
    val lastCommitDate: String
)

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var githubInfo by remember { mutableStateOf<GitHubInfo?>(null) }
    var loading by remember { mutableStateOf(true) }

    val currentVersion = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
        } catch (e: Exception) { "1.0" }
    }

    LaunchedEffect(Unit) {
        githubInfo = fetchGitHubInfo()
        loading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "A propos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Version card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Info,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text("Version actuelle", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.5f))
                    Text("v$currentVersion", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Latest release
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                }
            } else if (githubInfo != null) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.NewReleases, contentDescription = null, tint = Primary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text("Derniere mise a jour", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.5f))
                            Text("v${githubInfo!!.latestVersion}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Primary)
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Outlined.Code, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(githubInfo!!.lastCommitMessage, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(githubInfo!!.lastCommitDate, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.35f))
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Impossible de recuperer les infos", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.5f))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // GitHub link
        Card(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mathis-mm/Pilot"))
                context.startActivity(intent)
            },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Code, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Code source", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.5f))
                    Text("github.com/mathis-mm/Pilot", style = MaterialTheme.typography.bodyMedium, color = Primary)
                }
                Icon(Icons.Filled.OpenInNew, contentDescription = null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

private suspend fun fetchGitHubInfo(): GitHubInfo? = withContext(Dispatchers.IO) {
    try {
        // Get latest release
        val releaseConn = URL("https://api.github.com/repos/mathis-mm/Pilot/releases/latest").openConnection() as HttpURLConnection
        releaseConn.setRequestProperty("Accept", "application/vnd.github.v3+json")
        releaseConn.connectTimeout = 8000
        releaseConn.readTimeout = 8000
        val latestVersion = if (releaseConn.responseCode == 200) {
            val json = JSONObject(releaseConn.inputStream.bufferedReader().readText())
            json.getString("tag_name").removePrefix("v")
        } else "?"
        releaseConn.disconnect()

        // Get latest commit
        val commitConn = URL("https://api.github.com/repos/mathis-mm/Pilot/commits?per_page=1").openConnection() as HttpURLConnection
        commitConn.setRequestProperty("Accept", "application/vnd.github.v3+json")
        commitConn.connectTimeout = 8000
        commitConn.readTimeout = 8000
        val (commitMsg, commitDate) = if (commitConn.responseCode == 200) {
            val arr = org.json.JSONArray(commitConn.inputStream.bufferedReader().readText())
            val commit = arr.getJSONObject(0).getJSONObject("commit")
            val msg = commit.getString("message").lines().first()
            val dateStr = commit.getJSONObject("committer").getString("date")
            val sdfIn = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }
            val sdfOut = SimpleDateFormat("d MMMM yyyy 'a' HH:mm", Locale.FRANCE)
            val formatted = try { sdfOut.format(sdfIn.parse(dateStr)!!) } catch (e: Exception) { dateStr }
            Pair(msg, formatted)
        } else Pair("?", "?")
        commitConn.disconnect()

        GitHubInfo(latestVersion, commitMsg, commitDate)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
