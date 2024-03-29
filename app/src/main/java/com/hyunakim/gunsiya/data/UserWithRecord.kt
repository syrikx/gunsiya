package com.hyunakim.gunsiya.data

data class UserWithRecord(
    val birthday: String,
    val didDropAtropine: Map<String, Boolean>,
    val hospitalCode: String,
    val timeSpentOutdoors: Map<String, Double>,
    val timeSpentOnNearbyTasks: Map<String, Double>,
    val id: String,
    val name: String,
//    val date: Map<String, String>
)

fun convertToUserWithRecord(user: User, records: List<Record>): UserWithRecord {
    val didDropAtropine = mutableMapOf<String, Boolean>()
    val timeSpentOutdoors = mutableMapOf<String, Double>()
    val timeSpentOnNearbyTasks = mutableMapOf<String, Double>()

    for (record in records) {
        didDropAtropine[record.date] = record.isAtropineDrop
        timeSpentOutdoors[record.date] = record.timeOutdoorActivity
        timeSpentOnNearbyTasks[record.date] = record.timeCloseWork
    }

    return UserWithRecord(
        birthday = user.birthDate,
        didDropAtropine = didDropAtropine,
        hospitalCode = user.hospitalCode,
        timeSpentOutdoors = timeSpentOutdoors,
        timeSpentOnNearbyTasks = timeSpentOnNearbyTasks,
        id = user.patientCode,
        name = user.name,
//        date = mapOf("submitted" to user.lastSelectedTime.toString())
    )
}

