package com.muzu.capyfocus.core.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.muzu.capyfocus.domain.models.AgendaItem
import com.muzu.capyfocus.domain.models.RecurrenceType
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.DayOfWeek
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

    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager?.canScheduleExactAlarms() ?: false
        } else {
            true
        }
    }

    fun requestExactAlarmPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${context.packageName}")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun scheduleNotification(item: AgendaItem) {
        // Cancel old notification/alarm for this item first so edited items don't keep old reminders active
        cancelNotification(item.id)

        if (item.notificationOffset == null || item.isCompleted) return

        val triggerMillis = calculateNextTriggerMillis(item) ?: return

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
            if (canScheduleExactAlarms()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                } else {
                    alarmManager?.setExact(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager?.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                } else {
                    alarmManager?.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                }
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

    fun calculateNextTriggerMillis(item: AgendaItem): Long? {
        val offset = item.notificationOffset ?: return null
        if (item.isCompleted) return null

        val nowMillis = System.currentTimeMillis()
        val zone = ZoneId.systemDefault()
        val baseDate = LocalDate.ofEpochDay(item.dateEpochDay)
        val today = LocalDate.now(zone)
        val hour = item.startMinuteOfDay / 60
        val minute = item.startMinuteOfDay % 60
        val eventTime = LocalTime.of(hour, minute)

        val startDate = if (baseDate.isAfter(today)) baseDate else today

        var checkDate = startDate
        var attempts = 0
        val maxAttempts = 366

        while (attempts < maxAttempts) {
            val eventDateTime = LocalDateTime.of(checkDate, eventTime)
            val triggerDateTime = eventDateTime.minusMinutes(offset.minutesBefore)
            val triggerMillis = triggerDateTime.atZone(zone).toInstant().toEpochMilli()

            if (isDateMatchingRecurrence(checkDate, baseDate, item.recurrenceType)) {
                if (triggerMillis > nowMillis) {
                    return triggerMillis
                }
            }

            if (item.recurrenceType == RecurrenceType.NONE) {
                return null
            }

            checkDate = checkDate.plusDays(1)
            attempts++
        }

        return null
    }

    private fun isDateMatchingRecurrence(
        date: LocalDate,
        baseDate: LocalDate,
        recurrenceType: RecurrenceType,
    ): Boolean {
        if (date.isBefore(baseDate)) return false

        return when (recurrenceType) {
            RecurrenceType.NONE -> date == baseDate
            RecurrenceType.DAILY -> true
            RecurrenceType.WEEKDAYS -> date.dayOfWeek != DayOfWeek.SATURDAY && date.dayOfWeek != DayOfWeek.SUNDAY
            RecurrenceType.WEEKLY -> date.dayOfWeek == baseDate.dayOfWeek
            RecurrenceType.MONTHLY -> date.dayOfMonth == baseDate.dayOfMonth ||
                (date.dayOfMonth == date.lengthOfMonth() && baseDate.dayOfMonth > date.lengthOfMonth())
            RecurrenceType.CUSTOM -> date.dayOfWeek == baseDate.dayOfWeek
        }
    }
}
