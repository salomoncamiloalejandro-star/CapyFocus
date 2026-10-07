package com.muzu.capyfocus.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.muzu.capyfocus.domain.repository.AgendaRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var agendaRepository: AgendaRepository

    @Inject
    lateinit var notificationScheduler: NotificationScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val pendingItems = agendaRepository.getAllPendingWithNotifications()
                    pendingItems.forEach { item ->
                        notificationScheduler.scheduleNotification(item)
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
