-keeppackagenames com.doctoror.particlesdrawable.**

# @KeepAsApi annotation

-keep class com.doctoror.particlesdrawable.KeepAsApi

-keep @com.doctoror.particlesdrawable.KeepAsApi class * {*;}

-keepclasseswithmembers class * {
    @com.doctoror.particlesdrawable.KeepAsApi <methods>;
}

-keepclasseswithmembers class * {
    @com.doctoror.particlesdrawable.KeepAsApi <fields>;
}

-keepclasseswithmembers class * {
    @com.doctoror.particlesdrawable.KeepAsApi <init>(...);
}
