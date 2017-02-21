# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/jandrade/Android/Sdk/tools/proguard/proguard-android.txt
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

-optimizationpasses 5
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose

# Retrofit 1.X

-keep class com.squareup.okhttp.** { *; }
-keep class retrofit.** { *; }
-keep interface com.squareup.okhttp.** { *; }

-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
-dontwarn retrofit.**
-dontwarn rx.**

-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

# If in your rest service interface you use methods with Callback argument.
-keepattributes Exceptions

# If your rest service methods throw custom exceptions, because you've defined an ErrorHandler.
-keepattributes Signature

# Also you must note that if you are using GSON for conversion from JSON to POJO representation, you must ignore those POJO classes from being obfuscated.
# Here include the POJO's that have you have created for mapping JSON response to POJO for example.


-dontwarn com.fasterxml.jackson.databind.**
-keepnames class com.fasterxml.jackson.** { *; }


-keep class cm.aptoide.lite.**
-keep class cm.aptoide.pt.**
-keeppackagenames drawable
-keeppackagenames layout
-keeppackagenames menu
-keeppackagenames value
#

-dontshrink
-dontoptimize
-keepclasseswithmembers class * {
    void onClick*(...);
}
-keepclasseswithmembers class * {
    *** *Callback(...);
}


 ##---------------Begin: proguard configuration common for all Android apps ----------
  -optimizationpasses 5
  -dontusemixedcaseclassnames
  -dontskipnonpubliclibraryclasses
  -dontskipnonpubliclibraryclassmembers
  -dontpreverify
  -verbose
  -dump class_files.txt
  -printseeds seeds.txt
  -printusage unused.txt
  -optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

  -allowaccessmodification
  -keepattributes *Annotation*
  -renamesourcefileattribute SourceFile
  -keepattributes SourceFile,LineNumberTable
  -repackageclasses ''

  -keep public class * extends android.app.Activity
  -keep public class * extends android.app.Application
  -keep public class * extends android.app.Service
  -keep public class * extends android.content.BroadcastReceiver
  -keep public class * extends android.content.ContentProvider
  -keep public class * extends android.app.backup.BackupAgentHelper
  -keep public class * extends android.preference.Preference
  -keep public class com.android.vending.licensing.ILicensingService
  -dontnote com.android.vending.licensing.ILicensingService

  -keep public class * {
      public protected *;
  }


  ##---------------End: proguard configuration common for all Android apps ----------

  ##---------------Begin: proguard configuration for Gson  ----------
  # Gson uses generic type information stored in a class file when working with fields. Proguard
  # removes such information by default, so configure it to keep all of it.
  -keepattributes Signature

  # For using GSON @Expose annotation
  -keepattributes *Annotation*

  # Gson specific classes
  -keep class sun.misc.Unsafe { *; }
  #-keep class com.google.gson.stream.** { *; }

  # Application classes that will be serialized/deserialized over Gson
  -keep class com.google.gson.examples.android.model.** { *; }

  ##---------------End: proguard configuration for Gson  ----------


# For RoboSpice
#Results classes that only extend a generic should be preserved as they will be pruned by Proguard
#as they are "empty", others are kept

#RoboSpice requests should be preserved in most cases
-keepclassmembers class cm.aptoide.lite.webservices.** {
  public void set*(***);
  public *** get*();
  public *** is*();
}


## Gson SERIALIZER SETTINGS
# See https://code.google.com/p/google-gson/source/browse/trunk/examples/android-proguard-example/proguard.cfg
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature
# Gson specific classes
 -keep class sun.misc.Unsafe { *; }

 -keep class com.octo.android.robospice.** { *; }

 -keep class com.squareup.okhttp.** { *; }

 -keep interface com.squareup.okhttp.** { *; }

 -keep class com.google.gson

-dontwarn android.net.http.*
-dontwarn com.android.internal.http.multipart.MultipartEntity
-dontwarn com.octo.android.robospice.SpiceService
-dontwarn org.apache.http.impl.auth.NegotiateScheme
#-keep class android.net.http.AndroidHttpClient
#-keep class android.net.http.AndroidHttpClientConnection
#-keep class org.apache.http.* { ; }
