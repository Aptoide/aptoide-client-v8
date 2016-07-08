# Jackson

-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}

-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}

-keepclassmembers class * {
    @com.fasterxml.jackson.annotation.* *;
}

-keepattributes *Annotation*,EnclosingMethod,Signature

-keepnames class com.fasterxml.jackson.** { *; }

-dontwarn com.fasterxml.jackson.databind.**

-keep class org.codehaus.** { *; }

-keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
	public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *;
}

-keep class org.codehaus.jackson.annotate.** { *; }
