# Sample — `LocaleManager`

The sample application after the migration off Localian, using the platform
[`LocaleManager`](https://developer.android.com/reference/android/app/LocaleManager).
It requires `minSdk` 33; for lower versions see the
[`sample/appcompat-delegate`](https://github.com/infinum/android-localian/tree/sample/appcompat-delegate)
branch.

Diff it against `master` to see the migration itself:

```
git diff master...sample/locale-manager -- sample
```

The guide it follows is [MIGRATION.md](../MIGRATION.md).
