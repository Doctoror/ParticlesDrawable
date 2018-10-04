# @ConfigurableKeep annotation

-keep class com.doctoror.particlesdrawable.ConfigurableKeep

-keep @com.doctoror.particlesdrawable.ConfigurableKeep class * {*;}

-keepclasseswithmembers class * {
    @com.doctoror.particlesdrawable.ConfigurableKeep <methods>;
}

-keepclasseswithmembers class * {
    @com.doctoror.particlesdrawable.ConfigurableKeep <fields>;
}

-keepclasseswithmembers class * {
    @com.doctoror.particlesdrawable.ConfigurableKeep <init>(...);
}
