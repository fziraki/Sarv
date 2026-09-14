# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class abkabk.azbarkon.** {
    *** Companion;
}
-keepclasseswithmembers class abkabk.azbarkon.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ponytail: package stays abkabk.azbarkon for backwards compat; only display/DB names changed to Sarv
