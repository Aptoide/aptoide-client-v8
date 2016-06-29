
##
## Aptoide Account Manager (module) specific rules
##

#-keep public class cm.aptoide.accountmanager.** {
#  public void set*(***);
#  public *** get*();
#}
#-keep class cm.aptoide.accountmanager.** { *; }
#-keep class android.support.v7.appcompat.** { *; }
#-keep class cm.aptoide.pt.** {*;}
