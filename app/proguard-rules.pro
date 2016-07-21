# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class fullName to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# General configuration
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-dontobfuscate

# For testing purposes only!
-dontoptimize

-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!field/removal/writeonly,!field/marking/private,!code/allocation/variable

-keepattributes Exceptions,InnerClasses,Signature,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keep class java.lang.** { *; }
-keepnames enum java.lang.** {*;}
-keepnames interface java.lang.** {*;}

-keep interface org.androidannotations.api.** { *; }
-keepclassmembers interface org.androidannotations.api.** { *; }

-keep class javax.lang.** { *; }
-keep interface javax.lang.** { *; }

-dontwarn org.androidannotations.**
-dontwarn com.sun.codemodel.**

# Keeping activities
-keep public class * extends android.app.Activity { *; }
-keepclassmembernames public class * extends android.app.Activity { *; }

# Keeping fragments
-keep public class * extends android.support.v4.app.Fragment { *; }
-keepclassmembernames public class * extends android.support.v4.app.Fragment { *; }

# Keeping application
-keep public class * extends android.app.Application { *; }

# Keeping services
-keep public class * extends android.app.Service { *; }

# Keeping broadcast receivers
-keep public class * extends android.content.BroadcastReceiver { *; }

# Keeping content providers
-keep public class * extends android.content.ContentProvider { *; }

# Keeping backup utilities
-keep public class * extends android.app.backup.BackupAgentHelper

# Keeping preferences
-keep public class * extends android.preference.Preference

# Keeping licensing service
-keep public class com.android.vending.licensing.ILicensingService

# Keeping everything :)
-keep class com.fess89.swarmautoliker.** { *; }
-keepclassmembers class com.fess89.swarmautoliker.** { *; }
-keepclassmembernames class com.fess89.swarmautoliker.** { *; }

# Keeping model
-keep class com.fess89.swarmautoliker.model.** { *; }
-keepclasseswithmembers class com.fess89.swarmautoliker.model.** { *; }
-keepclassmembernames class com.fess89.swarmautoliker.model.** { *; }
-keepclasseswithmembernames class com.fess89.swarmautoliker.model.** { *; }

# Keeping DAO
-keep class com.fess89.swarmautoliker.database.dao.** { *; }
-keepclassmembernames class com.fess89.swarmautoliker.database.dao.** { *; }

-keep class com.fess89.swarmautoliker.database.** { *; }
-keepclassmembernames class com.fess89.swarmautoliker.database.** { *; }

# Keeping events
-keep class com.fess89.swarmautoliker.event.** { *; }
-keepclassmembernames class com.fess89.swarmautoliker.event.** { *; }

# EventBus
-keepclassmembers class ** {
    public void onEvent(**);
}

-keepclassmembers class ** {
    public void onEventMainThread(**);
}

-keep class de.greenrobot.event.**
-keepclassmembers class de.greenrobot.event.** { *; }
-keepclassmembernames class de.greenrobot.event.** { *; }

-keep enum de.greenrobot.event.**
-keepclassmembers enum de.greenrobot.event.** { *; }
-keepclassmembernames enum de.greenrobot.event.** { *; }

-keep interface de.greenrobot.event.**
-keepclassmembers interface de.greenrobot.event.** { *; }
-keepclassmembernames interface de.greenrobot.event.** { *; }

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# DatabaseHelper
-keepclasseswithmembernames class * {
    public <init>(android.content.Context);
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# Keeping native libs
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keeping enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keeping parcelables
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers, allowobfuscation class * {
    @org.codehaus.jackson.annotate.* <fields>;
    @org.codehaus.jackson.annotate.* <init>(...);
}

-keep class org.apache.commons.io.**
-keepclassmembers class org.apache.commons.io.** { *; }
-dontwarn org.apache.commons.io.**

-keep class com.google.inject.** { *; }
-keep class javax.inject.** { *; }
-keep class javax.annotation.** { *; }

-keepclassmembers class com.fess89.swarmautoliker.model.** {
  public void set*(***);
  public *** get*();
  public *** is*();
}

# Warnings for aux networking libraries
-dontwarn android.support.**
-dontwarn com.sun.xml.internal.**
-dontwarn com.sun.istack.internal.**
-dontwarn org.codehaus.jackson.**
-dontwarn org.springframework.**
-dontwarn java.awt.**
-dontwarn javax.security.**
-dontwarn java.beans.**
-dontwarn javax.xml.**
-dontwarn java.util.**
-dontwarn org.w3c.dom.**
-dontwarn com.google.common.**
-dontwarn com.octo.android.robospice.persistence.**
-dontwarn java.lang.invoke**
-dontwarn org.apache.lang.**
-dontwarn org.apache.commons.**
-dontwarn com.nhaarman.**
-dontwarn se.emilsjolander.**
-dontwarn org.acra.**
-dontwarn com.google.zxing.**
-dontwarn com.google.zxing.**

-dontwarn android.support.**

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }

# Support library
-keep class android.support.** { *; }
-keepnames class android.support.** { *; }
-keepclassmembers class android.support.** { *; }
-keepclassmembernames class android.support.** { *; }

# ORMLite
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }

-keep class org.apache.harmony.lang.annotation.AnnotationFactory { *; }
-keepclassmembers class org.apache.harmony.lang.annotation.AnnotationFactory { *; }
-keep class org.apache.harmony.lang.annotation.AnnotationMember { *; }
-keepclassmembers class org.apache.harmony.lang.annotation.AnnotationMember { *; }

-dontwarn com.j256.ormlite.db.**

# Logging for ORMLite
-dontwarn com.j256.ormlite.logger.**
-dontwarn org.apache.commons.logging.**
-dontwarn org.apache.log4j.**
-dontwarn org.apache.logging.log4j.**

-keep class org.apache.commons.logging.**
-keepclassmembers class org.apache.commons.logging.** { *; }
-keep enum org.apache.commons.logging.**
-keepclassmembers enum org.apache.commons.logging.** { *; }
-keep interface org.apache.commons.logging.**
-keepclassmembers interface org.apache.commons.logging.** { *; }

# Retrofit
-keep class retrofit.http.** { *; }
-keepclasseswithmembers class * { @retrofit.http.* <methods>; }

-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }

-dontwarn com.facebook.android.BuildConfig
-dontwarn rx.**
-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn retrofit.appengine.UrlFetchClient
-dontwarn retrofit.converter.**
-dontwarn com.fasterxml.jackson.**

-keepattributes EnclosingMethod, InnerClasses
-keepattributes Annotation
-keepattributes Signature

-keep class * extends android.support.v4.view.** { *; }
-keep class * extends android.widget.* { *; }
-keep class * extends android.preference.* { *; }

# Google Play services
-dontwarn com.google.android.gms.**

-keep class javax.servlet.** { *; }
-keepclassmembers class javax.servlet.** { *; }
-keepclassmembernames class javax.servlet.** { *; }
-keepclasseswithmembernames class javax.servlet.** { *; }

-dontwarn jcifs.**

# Testing
-dontwarn dagger.internal.**
-dontwarn org.hamcrest.**
-dontwarn com.google.android.apps.common.testing.**
-dontwarn com.squareup.javawriter.**

# Apache
-dontwarn org.apache.commons.**
-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**

# JodaTime
-keep class org.joda.** { *; }
-keepclassmembers class org.joda.** { *; }
-keepclassmembernames class org.joda.** { *; }
-keepclasseswithmembernames class org.joda.** { *; }

#-keep class com.google.common.** { *; }
#-keepclassmembers class com.google.common.** { *; }
#-keepclassmembernames class com.google.common.** { *; }

-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**

-dontwarn javax.servlet.**
-dontwarn jcifs.http.NetworkExplorer

# Icepick
-dontwarn icepick.**
-keep class icepick.** { *; }
-keep class **$$Icepick { *; }
-keepclasseswithmembernames class * {
    @icepick.* <fields>;
}
-keepnames class * { @icepick.State *;}