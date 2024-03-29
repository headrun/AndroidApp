# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/headrun/Android/Sdk/tools/proguard/proguard-android.txt
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

-keep class butterknife.** { *; }
 -dontwarn butterknife.internal.**
 -keep class **$$ViewBinder { *; }

 -keepclasseswithmembernames class * {
     @butterknife.* <fields>;
 }

 -keepclasseswithmembernames class * {
     @butterknife.* <methods>;
 }

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


 -keep public class * implements com.bumptech.glide.module.GlideModule
 -keep public class * extends com.bumptech.glide.module.AppGlideModule
 -keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
   **[] $VALUES;
   public *;
 }

 -ignorewarnings
 -keep class * {
     public private *;
 }

 # for DexGuard only
 #-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

 ### OKHTTP

 # Platform calls Class.forName on types which do not exist on Android to determine platform.
 -dontnote okhttp3.internal.Platform


 ### OKIO

 # java.nio.file.* usage which cannot be used at runtime. Animal sniffer annotation.
 -dontwarn okio.Okio
 # JDK 7-only method which is @hide on Android. Animal sniffer annotation.
 -dontwarn okio.DeflaterSink

