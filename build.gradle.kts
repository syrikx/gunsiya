// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.apply {
        set("lifecycle_version", "2.6.1")
        set("room_version", "2.5.2")
    }
}
//plugins {
//    id("com.android.application") version "8.1.0" apply false
//    id("com.android.library") version "8.1.0" apply false
//    id("org.jetbrains.kotlin.android") version "1.8.21" apply false
//}

plugins {
    id ("com.android.application") version "8.0.2" apply false
    id ("com.android.library") version "8.0.2" apply false
    id ("org.jetbrains.kotlin.android") version "1.8.21" apply false
    id ("io.realm.kotlin") version "1.10.0" apply false
    id ("org.jetbrains.dokka") version "1.8.10"
}