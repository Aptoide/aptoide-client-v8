# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/neuro/programming/android-sdk-linux/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep class cm.aptoide.pt.** {*;}
#-keep class cm.aptoide.pt.preferences.** {*;}
#-keep class cm.aptoide.pt.dataprovider.** {*;}
-keep class android.support.v7.appcompat.** { *; }

## Retrolambda specific rules ##

# as per official recommendation: https://github.com/evant/gradle-retrolambda#proguard
-dontwarn java.lang.invoke.*

# Proguard Configuration for Realm (http://realm.io)
# For detailed discussion see: https://groups.google.com/forum/#!topic/realm-java/umqKCc50JGU
# Additionally you need to keep your Realm Model classes as well
# For example:
# -keep class com.yourcompany.realm.** { *; }

#-keep class io.realm.annotations.RealmModule
#-keep @io.realm.annotations.RealmModule class *
#-keep class io.realm.internal.Keep
#-keep @io.realm.internal.Keep class *
#-dontwarn javax.**
#-dontwarn io.realm.**

# RxJava 0.21

-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}