# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class abkabk.azbarkon.** {
    *** Companion;
}
-keepclasseswithmembers class abkabk.azbarkon.** {
    kotlinx.serialization.KSerializer serializer(...);
}
