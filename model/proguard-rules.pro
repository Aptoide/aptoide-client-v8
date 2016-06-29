
##
## Jackson specific rules
##

#-keepattributes *Annotation*,EnclosingMethod,Signature
#-keepnames class com.fasterxml.jackson.** { *; }
# -dontwarn com.fasterxml.jackson.databind.**
# -keep class org.codehaus.** { *; }
# -keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
# public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *; }

##
## Model (module) specific rules
##

#-keep class android.support.v7.appcompat.** { *; }
#-keep class cm.aptoide.pt.** {*;}


