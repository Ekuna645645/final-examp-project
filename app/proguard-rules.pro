# Keep Firebase model classes intact so Realtime Database reflection works.
-keep class ge.btu.habittracker.data.model.** { *; }
-keepclassmembers class ge.btu.habittracker.data.model.** {
    <init>();
    <fields>;
}
