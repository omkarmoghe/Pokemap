# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Omkar Moghe\android-sdks/tools/proguard/proguard-android.txt
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

-printseeds build/seed.txt
-printusage build/usuage.txt

-dontobfuscate
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-optimizationpasses 5

-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*

#####
# Something to do with Okio.Http
#####
-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-keep, includedescriptorclasses class sun.misc.Unsafe { *; }


######
# Support Stream
######
#-dontwarn java.lang.invoke.**
-dontwarn sun.misc.Unsafe
-dontwarn java.lang.**

######
# App Compat, Design Support Libraries
######
-keep, includedescriptorclasses public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-dontwarn android.support.design.**
-keep, includedescriptorclasses class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep, includedescriptorclasses public class android.support.design.R$* { *; }


#####
# Protocol Buffers
#####
-keep class com.google.protobuf.** { *; }
-dontwarn com.google.**
-keep class * extends com.google.protobuf.GeneratedMessage { *; }
-keepclassmembernames class * extends com.google.protobuf.GeneratedMessage { *; }


-keep class java8.**
-dontwarn java8.**

-keep, includedescriptorclasses public class POGOSProtos.**{ *; }
-keep, includedescriptorclasses public class com.omkarmoghe.**{ *; }

######
# Google maps animations
######
-keep, includedescriptorclasses public class com.google.android.gms.maps.model.** {*;}


#-ignorewarnings