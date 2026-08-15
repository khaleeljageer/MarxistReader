# Add project specific ProGuard rules here.
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep line numbers for readable crash stack traces (and hide the original file name).
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ---------------------------------------------------------------------------
# kotlinx.serialization  (WordPress REST DTOs in :network)
# ---------------------------------------------------------------------------
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

# Keep every @Serializable DTO and its generated $$serializer intact. The model
# package is tiny, so keeping it fully is the safest way to avoid serialization
# breakage under R8 full mode.
-keep class org.cpimtn.marxist.network.model.** { *; }

# Standard kotlinx.serialization keep rules (R8 full-mode safe).
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$$serializer {
    *;
}

# ---------------------------------------------------------------------------
# Enums persisted by name via DataStore + Enum.valueOf() — constant names must
# survive obfuscation or valueOf() throws at runtime (settings would crash).
# ---------------------------------------------------------------------------
-keepclassmembers enum org.cpimtn.marxist.android.domain.model.Theme { *; }
-keepclassmembers enum org.cpimtn.marxist.android.domain.model.FontSize { *; }
-keepclassmembers enum org.cpimtn.marxist.android.domain.model.AppLanguage { *; }
-keepclassmembers enum org.cpimtn.marxist.android.domain.model.HelpTopic { *; }

# ---------------------------------------------------------------------------
# Retrofit — keep generic signatures used to build service interfaces.
# (Modern Retrofit/OkHttp ship their own consumer rules; these are belt-and-braces.)
# ---------------------------------------------------------------------------
-keepattributes Signature, Exceptions, EnclosingMethod

# ---------------------------------------------------------------------------
# joda-time (pulled in transitively via the Readium reader) references the
# optional compile-only joda-convert annotations, which aren't on the runtime
# classpath. Suppress the R8 missing-class warnings for them.
# ---------------------------------------------------------------------------
-dontwarn org.joda.convert.FromString
-dontwarn org.joda.convert.ToString
