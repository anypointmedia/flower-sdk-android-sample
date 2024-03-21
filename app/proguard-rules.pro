# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#-keep class org.apache.xerces.** { *; }
#-keep class org.w3c.** { *; }
#-keep class org.xml.sax.** { *; }
#-keep class javax.xml.** { *; }
#-keep class com.fasterxml.jackson.** { *; }
#-keep class kotlinx.coroutines.** { *; }
#
#-dontwarn com.google.ads.**
#-keep class com.google.** { *; }
#-keep interface com.google.** { *; }
#-keep class com.google.ads.interactivemedia.** { *; }
#-keep interface com.google.ads.interactivemedia.** { *; }
#
#-keepclassmembers class ch.qos.logback.classic.pattern.* { <init>(); }
#-keepclassmembers class ch.qos.logback.** { *; }
#-keepclassmembers class org.slf4j.impl.** { *; }
#
-keep class tv.anypoint.flower.android.sdk.api.** { *; }
-keep class tv.anypoint.flower.sdk.core.ads.domain.** { *; }
-keep class tv.anypoint.flower.sdk.core.manifest.hls.model.** { *; }
-keeppackagenames tv.anypoint.**
-dontwarn org.slf4j.impl.StaticLoggerBinder
