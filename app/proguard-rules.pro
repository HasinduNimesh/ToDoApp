# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep MainActivity and other Activity classes
-keep class com.example.moderntodo.ui.MainActivity { *; }
-keep class com.example.moderntodo.TodoApplication { *; }

# Keep all classes that extend Activity, Service, BroadcastReceiver, etc.
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Keep Hilt components
-keep class dagger.hilt.android.** { *; }
-keep class * extends dagger.hilt.android.AndroidEntryPoint
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }

# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep Compose classes
-keep class androidx.compose.** { *; }
-keep class androidx.activity.ComponentActivity { *; }

# Keep Room entities and DAOs
-keep class com.example.moderntodo.data.local.entity.** { *; }
-keep class com.example.moderntodo.data.local.dao.** { *; }

# Keep data classes and models
-keep class com.example.moderntodo.data.model.** { *; }

# Keep Firebase backup data classes and their constructors
-keep class com.example.moderntodo.data.backup.BackupData { *; }
-keep class com.example.moderntodo.data.local.ToDoList { *; }
-keep class com.example.moderntodo.data.local.ToDoItem { *; }
-keep class com.example.moderntodo.data.local.Priority { *; }

# Keep Firebase Firestore serialization classes
-keepclassmembers class com.example.moderntodo.data.backup.BackupData {
    public <init>();
    public <init>(...);
    public *;
}
-keepclassmembers class com.example.moderntodo.data.local.ToDoList {
    public <init>();
    public <init>(...);
    public *;
}
-keepclassmembers class com.example.moderntodo.data.local.ToDoItem {
    public <init>();
    public <init>(...);
    public *;
}
-keepclassmembers class com.example.moderntodo.data.model.User {
    public <init>();
    public <init>(...);
    public *;
}

# Keep enums used in Firebase
-keepclassmembers enum com.example.moderntodo.data.local.Priority {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Preserve the line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep serialization classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep R8 from removing classes with @Keep annotation
-keep class androidx.annotation.Keep
-keep @androidx.annotation.Keep class * {*;}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}