package com.muzu.capyfocus.core.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.muzu.capyfocus.domain.models.AgendaItem
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

    companion object {
        const val CHANNEL_ID = "agenda_notifications_channel"
        const val CHANNEL_NAME = "Recordatorios de Agenda"
        const val EXTRA_ITEM_ID = "extra_agenda_item_id"
        const val EXTRA_ITEM_TITLE = "extra_agenda_item_title"
        const val EXTRA_ITEM_DESC = "extra_agenda_item_desc"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Notificaciones de eventos y tareas de la Agenda"
                enableVibration(true)
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun scheduleNotification(item: AgendaItem) {
        val offset = item.notificationOffset ?: return
        if (item.isCompleted) return

        val localDate = LocalDate.ofEpochDay(item.dateEpochDay)
        val hour = item.startMinuteOfDay / 60
        val minute = item.startMinuteOfDay % 60
        val eventTime = LocalDateTime.of(localDate, LocalTime.of(hour, minute))
        val triggerTime = eventTime.minusMinutes(offset.minutesBefore)

        val triggerMillis = triggerTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        if (triggerMillis <= System.currentTimeMillis()) return

        val intent = Intent(context, AgendaAlarmReceiver::class.java).apply {
            putExtra(EXTRA_ITEM_ID, item.id)
            putExtra(EXTRA_ITEM_TITLE, item.title)
            putExtra(EXTRA_ITEM_DESC, item.description.ifBlank { "Tenés una actividad programada." })
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            item.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
            } else {
                alarmManager?.setExact(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
            }
        } catch (_: SecurityException) {
            alarmManager?.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
        }
    }

    fun cancelNotification(itemId: String) {
        val intent = Intent(context, AgendaAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            itemId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
        )
        if (pendingIntent != null) {
            alarmManager?.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
