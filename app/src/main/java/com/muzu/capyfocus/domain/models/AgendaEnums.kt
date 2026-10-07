package com.muzu.capyfocus.domain.models

enum class AgendaItemType {
    TASK,
    EVENT,
    WORK
}

enum class AgendaCategory(val colorArgb: Int, val label: String) {
    STUDY(0xFF2196F3.toInt(), "Estudio"),
    WORK(0xFFFF9800.toInt(), "Trabajo"),
    PERSONAL(0xFF9C27B0.toInt(), "Personal"),
    EXERCISE(0xFF4CAF50.toInt(), "Ejercicio"),
    OTHER(0xFF607D8B.toInt(), "Otros")
}

enum class AgendaPriority {
    LOW,
    MEDIUM,
    HIGH
}

enum class RecurrenceType(val label: String) {
    NONE("Sin repetición"),
    DAILY("Todos los días"),
    WEEKDAYS("Lunes a viernes"),
    WEEKLY("Semanalmente"),
    MONTHLY("Mensualmente"),
    CUSTOM("Personalizada")
}

enum class NotificationOffset(val minutesBefore: Long, val label: String) {
    EXACT(0, "En el momento"),
    FIVE_MINUTES_BEFORE(5, "5 minutos antes"),
    FIFTEEN_MINUTES_BEFORE(15, "15 minutos antes"),
    THIRTY_MINUTES_BEFORE(30, "30 minutos antes"),
    ONE_HOUR_BEFORE(60, "1 hora antes"),
    ONE_DAY_BEFORE(1440, "1 día antes")
}
