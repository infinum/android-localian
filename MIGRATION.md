# Migration guide

Localian is deprecated. Android provides per-app language support natively, and
it covers more than Localian was able to: the selected locale is stored outside
the application and every activity is recreated automatically. The migration
therefore consists largely of removing code.

## Selecting a replacement

The replacement follows from the application's `minSdk`, not from the API level
of the device it is running on:

| `minSdk` | Replacement |
| --- | --- |
| 33+ | `android.app.LocaleManager`, provided by the platform |
| below 33 | `AppCompatDelegate`, provided by `androidx.appcompat` 1.6.0+ |

Applications supporting API 32 or lower use `AppCompatDelegate` on every OS
version, including Android 13 and above, where it forwards to `LocaleManager`
for both reading and writing. There is no reason to branch on
`Build.VERSION.SDK_INT` and maintain two code paths.

## 1. Remove Localian

Remove the dependency, then delete the code and manifest entries it required:

- The `Application` subclass, if it only called `Localian.run(this)`. The
  `android:name` attribute should also be removed from `<application>`.
- Any implementation of `Localian.Cache` and any `Localian.Callback`, along with
  the `recreate()` call made after saving a language and any code that rebuilt a
  screen in `onRestart()` or `onResume()`. Both replacements recreate every
  activity in the task, including those that are stopped.
- The `com.infinum.localian.initial_locale_language_tag` and
  `com.infinum.localian.follow_system_locale` `<meta-data>` entries in
  `<application>`. The first has no equivalent: an application no longer picks a
  starting language, it simply renders in the device language until the user
  chooses otherwise. The second is now the default, expressed by an empty locale
  list.
- The `androidx.startup.InitializationProvider` override that removed
  `com.infinum.localian.LocalianInitializer`, if the WebView initializer had been
  disabled, together with any explicit `LocalianWebViewPatcher` instantiation.
  Nothing replaces these, and nothing regresses; see below.

### The WebView side effect does not come back

Localian applied a locale by calling `Locale.setDefault()` and
`Resources.updateConfiguration()` on the contexts that were already alive. That
override existed only inside the process and was not part of the configuration
the system handed to the application, so anything re-applying that configuration
discarded it — most notably the first creation of a `WebView`
([issue 37113860](https://issuetracker.google.com/issues/37113860)), which is
what `LocalianWebViewPatcher` was repairing.

Both replacements keep the selection outside the process instead: on API 33 and
above the platform stores it and includes it in the configuration it gives the
application, and below that AppCompat re-applies it through the base context of
every `AppCompatActivity`. A `WebView` resetting the locale to the application's
configuration therefore lands on the selected language, and there is nothing
left to patch. The sample's WebView screen is the place to confirm this after
migrating.

## 2. Declare the supported languages

Localian needed no list of the languages an application ships; the system does.
`android:localeConfig` is what puts the application into
**Settings → System → Languages → App languages** on Android 13 and above, which
is where users expect to find a per-app language once the platform owns it.
Declare it regardless of the replacement chosen, as an application with a lower
`minSdk` still runs on Android 13 devices. This is the one step of the migration
that adds something rather than removing it.

Either write the list by hand, in `res/xml/locales_config.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<locale-config xmlns:android="http://schemas.android.com/apk/res/android">
    <locale android:name="hr-HR" />
    <locale android:name="en-US" />
    <locale android:name="de-DE" />
</locale-config>
```

and reference it from `<application>`:

```xml
<application
    android:localeConfig="@xml/locales_config">
```

Or have AGP 8.1 and above generate it from the `values-*` resource folders:

```groovy
android {
    androidResources {
        generateLocaleConfig true
    }
}
```

Generation additionally requires `compileSdk` 33 or higher and a
`res/resources.properties` file naming the locale of the unqualified `values`
folder:

```properties
unqualifiedResLocale=en-US
```

The two are mutually exclusive: with generation enabled, a hand-written
`locales_config.xml` fails the build.

Official documentation:
[per-app language preferences](https://developer.android.com/guide/topics/resources/app-languages),
[`android:localeConfig`](https://developer.android.com/guide/topics/resources/app-languages#use-localeconfig),
[automatic generation](https://developer.android.com/guide/topics/resources/app-languages#auto-localeconfig).

## 3. Read and write the language

### API 33 and above

```kotlin
private val localeManager: LocaleManager
    get() = getSystemService(LocaleManager::class.java)

private fun appLanguageTag(): String =
    localeManager.applicationLocales.takeUnless { it.isEmpty }?.get(0)?.toLanguageTag()
        ?: resources.configuration.locales[0].toLanguageTag()

localeManager.applicationLocales = LocaleList.forLanguageTags(languageTag)
```

An empty `applicationLocales` indicates that the user has never selected a
language, in which case the language resolved by the application is used instead.

### Below API 33

`minSdk` remains unchanged, but all activities must extend `AppCompatActivity`.

```kotlin
private fun appLanguageTag(): String? =
    (AppCompatDelegate.getApplicationLocales()[0]
        ?: ConfigurationCompat.getLocales(resources.configuration)[0])
        ?.toLanguageTag()

AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
```

Read the selection back as a full BCP 47 tag, through `toLanguageTag()`, and not
as `Locale.language`: the setter takes tags, and `language` alone collapses
`en-US` and `en-GB` into the same `en`. Only an application whose picker offers
a single locale per language can afford to compare language subtags.

The following service is also required in the manifest:

```xml
<service
    android:name="androidx.appcompat.app.AppLocalesMetadataHolderService"
    android:enabled="false"
    android:exported="false">
    <meta-data
        android:name="autoStoreLocales"
        android:value="true" />
</service>
```

The service is never started. It exists solely to carry the `autoStoreLocales`
flag, which instructs AppCompat to persist the locale itself below API 33.
Without it, devices running earlier versions lose the selected language on the
next cold start. Verify this on an emulator running API 30 or similar, as devices
on API 33 and above never execute that code path.

## 4. Determine how previously saved languages are handled

Localian's preferences remain on the device after the update, but they are no
longer read. Users who had selected a language other than their device language
will therefore see the application in their device language once, and will have
to select their language again.

### Preserving the previously selected language

Localian stored the selection in the `localian_preference` preferences file,
under the `key_current_locale` key, in JSON format:

```json
{"language":"en","country":"","variant":""}
```

Read this value once from the launcher activity's `onCreate`, before the layout
is inflated. Convert `language`, together with `country` if country-specific
resources are used, into a language tag such as `en` or `en-GB`, and pass it to
the new API:

```kotlin
// API 33 and above
localeManager.applicationLocales = LocaleList.forLanguageTags(tag)

// Below API 33
AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
```

Clear the preference afterwards so that the conversion runs only once, and remove
the code entirely once updates from a Localian build are no longer expected.

Three details of this conversion are easy to get wrong:

- **A saved value does not imply an explicit user selection.** `Localian.run()`
  saved the locale on every application start, so users who never opened the
  language picker also have a saved value, namely their device language. Applying
  it pins the application to that language and prevents subsequent device
  language changes from taking effect. Values matching the current device
  language should therefore be ignored. Compare whole language tags rather than
  language subtags if the picker offers several regions of one language;
  otherwise a saved `en-GB` looks like the device's `en-US` and is dropped.
- **The system language choice is stored separately.** `key_follow_system_locale`
  is `true` when the user had selected the device language. The locale saved next
  to it is then the device language of that moment rather than a selection, and
  applying it has the same effect as applying the device language above. An empty
  locale list, which is where both replacements start, already means that choice.
- **Where the conversion may run differs between the two replacements.**
  `LocaleManager` is an application-scoped system service that can be obtained
  from any context, `Application` included — this is exactly what AppCompat does
  internally on API 33 and above — so an application with `minSdk` 33 is free to
  convert anywhere. `AppCompatDelegate`, however, works with the
  `AppCompatActivity` context on API 32 and lower, so below 33 the conversion has
  to run from an activity. The launcher activity's `onCreate` satisfies both,
  which is what the samples use; applying a locale from `Application.onCreate`,
  while the process is still starting, was observed to take effect only partially
  in the sample, updating the activity title but not its content.

## Worked examples

The sample application in this repository was migrated on two branches, one per
replacement. Both start from the Localian version on `master`, so the diff
against it is the migration itself.

| Branch | Replacement | `minSdk` |
| --- | --- | --- |
| [`sample/locale-manager`](https://github.com/infinum/android-localian/tree/sample/locale-manager) | `LocaleManager` | raised to 33 |
| [`sample/appcompat-delegate`](https://github.com/infinum/android-localian/tree/sample/appcompat-delegate) | `AppCompatDelegate` | unchanged, 23 |

Both branches carry the conversion of previously saved Localian preferences in
`LocalianMigration`, called from the launcher activity, in the form described
above. The sample offers one locale per language, so its check that a saved
language is not merely the device language compares language subtags; an
application offering regional variants of the same language has to compare
complete, normalized tags there instead.
