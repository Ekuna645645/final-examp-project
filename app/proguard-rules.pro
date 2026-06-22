# Keep Firebase/Firestore model classes intact so reflection-based (de)serialization works.
-keep class ge.btu.flowershop.data.model.** { *; }
-keepclassmembers class ge.btu.flowershop.data.model.** {
    <init>();
    <fields>;
}
