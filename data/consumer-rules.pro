-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Database class * { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }

-keep class io.ii.data.remote.dto.** { *; }
-keepclassmembers class io.ii.data.remote.dto.** {
    public static ** Companion;
    public static ** $serializer(...);
}
