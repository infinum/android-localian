# Migration guide

Localian is deprecated. Android provides per-app language support natively, and
it covers more than Localian was able to: the selected locale is stored outside
the application and every activity is recreated automatically. The migration
therefore consists largely of removing code.

## Selecting a replacement

| `minSdk` | Replacement |
| --- | --- |
| 33+ | `android.app.LocaleManager`, provided by the platform |
| below 33 | `AppCompatDelegate`, provided by `androidx.appcompat` 1.6.0+ |

## 1. Remove Localian

Remove the dependency, then delete the code it required:

- The `Application` subclass, if it only called `Localian.run(this)`. The
  `android:name` attribute should also be removed from `<application>`.
- The `recreate()` call made after saving a language, together with any code that
  rebuilt a screen in `onRestart()` or `onResume()`. Both replacements recreate
  every activity in the task, including those that are stopped.

## 2. Read and write the language

### API 33 and above

```kotlin
private val localeManager: LocaleManager
    get() = getSystemService(LocaleManager::class.java)

private fun appLanguage(): String =
    localeManager.applicationLocales.takeUnless { it.isEmpty }?.get(0)?.language
        ?: resources.configuration.locales[0].language

localeManager.applicationLocales = LocaleList.forLanguageTags(languageTag)
```

An empty `applicationLocales` indicates that the user has never selected a
language, in which case the language resolved by the application is used instead.

### Below API 33

`minSdk` remains unchanged, but all activities must extend `AppCompatActivity`.

```kotlin
private fun appLanguage(): String =
    AppCompatDelegate.getApplicationLocales()[0]?.language
        ?: resources.configuration.locales[0].language

AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
```

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

## 3. Determine how previously saved languages are handled

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
  language should therefore be ignored.
- **The conversion cannot run in `Application`.** Both APIs apply the locale
  through the activities. When called from `Application`, `LocaleManager` applies
  it only partially, affecting the title but not the content, and
  `AppCompatDelegate` ignores it entirely.

## Worked examples

The sample application in this repository was migrated on two branches, one per
replacement. Both start from the Localian version on `master`, so the diff
against it is the migration itself.

| Branch | Replacement | `minSdk` |
| --- | --- | --- |
| [`sample/locale-manager`](https://github.com/infinum/android-localian/tree/sample/locale-manager) | `LocaleManager` | raised to 33 |
| [`sample/appcompat-delegate`](https://github.com/infinum/android-localian/tree/sample/appcompat-delegate) | `AppCompatDelegate` | unchanged, 23 |

```
git diff master...sample/locale-manager -- sample
git diff master...sample/appcompat-delegate -- sample
```

Neither branch carries the conversion of previously saved Localian preferences
described above, as the sample has no users whose selection has to be preserved.
