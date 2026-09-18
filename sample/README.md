# Sample — `AppCompatDelegate`

The sample application after the migration off Localian, using
[`AppCompatDelegate.setApplicationLocales`](https://developer.android.com/reference/androidx/appcompat/app/AppCompatDelegate#setApplicationLocales%28androidx.core.os.LocaleListCompat%29).
The sample `minSdk` stays at 23. On API 33 and above AppCompat forwards to the
platform; below it, the `AppLocalesMetadataHolderService` declared in the
manifest is what persists the selection, so test the migration on an API 30
emulator or similar. The
[`sample/locale-manager`](https://github.com/infinum/android-localian/tree/sample/locale-manager)
branch uses the platform API directly instead.

`LocalianMigration` converts the language Localian had saved, once, so the
selection survives the update.

Diff it against `master` to see the migration itself:

```
git diff master...sample/appcompat-delegate -- sample
```

The guide it follows is [MIGRATION.md](../MIGRATION.md).
