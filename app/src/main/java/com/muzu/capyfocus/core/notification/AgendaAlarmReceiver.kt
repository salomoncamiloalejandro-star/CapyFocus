package com.muzu.capyfocus.core.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.muzu.capyfocus.MainActivity
import com.muzu.capyfocus.R
import com.muzu.capyfocus.domain.models.RecurrenceType
import com.muzu.capyfocus.domain.repository.AgendaRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AgendaAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var agendaRepository: AgendaRepository

    @Inject
    lateinit var notificationScheduler: NotificationScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val itemId = intent.getStringExtra(NotificationScheduler.EXTRA_ITEM_ID) ?: return
        val title = intent.getStringExtra(NotificationScheduler.EXTRA_ITEM_TITLE) ?: "Recordatorio"
        val desc = intent.getStringExtra(NotificationScheduler.EXTRA_ITEM_DESC) ?: ""

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            itemId.hashCode(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, NotificationScheduler.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.notify(itemId.hashCode(), notification)

        // For recurring items, re-schedule notification for the next occurrence
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val item = agendaRepository.getItemById(itemId)
                if (item != null && item.recurrenceType != RecurrenceType.NONE && !item.isCompleted) {
                    notificationScheduler.scheduleNotification(item)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
