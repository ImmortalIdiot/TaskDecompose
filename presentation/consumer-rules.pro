-keep class io.ii.presentation.navigation.Route { *; }
-keep class io.ii.presentation.navigation.Route$* { *; }
-keepclassmembers class io.ii.presentation.navigation.Route$* {
    public static ** Companion;
    public static ** $serializer(...);
}
