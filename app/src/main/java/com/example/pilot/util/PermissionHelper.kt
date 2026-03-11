package com.example.pilot.util

import android.Manifest
import android.os.Build

object PermissionHelper {
    fun getRequiredPermissions(): List<String> {
        val permissions = mutableListOf(
            Manifest.permission.READ_CALENDAR
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        return permissions
    }

    fun getPermissionRationale(permission: String): String {
        return when (permission) {
            Manifest.permission.READ_CALENDAR -> "Acceder a votre calendrier pour afficher vos evenements"
            Manifest.permission.POST_NOTIFICATIONS -> "Envoyer des notifications pour vos rappels"
            else -> "Permission requise pour le bon fonctionnement de l'application"
        }
    }
}
