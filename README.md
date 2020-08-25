# Localian

Localian is a library that manages your application locale and language across multiple Android API levels.
 
Set a desired _Locale_ when creating a new _Localian_ instance and it will force your application to use proper ocalized data via Resources class.

The library has been inspired by the following [blogpost](https://proandroiddev.com/change-language-programmatically-at-runtime-on-android-5e6bc15c758).

## Usage
To include _Localian_ in your project, you have to add buildscript dependencies in your project level `build.gradle` or `build.gradle.kts`:

**Groovy**
```gradle
buildscript {
    repositories {
        jcenter()
        // or ...
        maven { url "https://dl.bintray.com/infinum/android" }
    }
}
```
**KotlinDSL**
```kotlin
buildscript {
    repositories {
        jcenter()
        // or ...
        maven(url = "https://dl.bintray.com/infinum/android")
    }
}
```

Then add the following dependencies in your app `build.gradle` or `build.gradle.kts` :

**Groovy**
```gradle
implementation "com.infinum.localian:localian:1.0.0"
```
**KotlinDSL**
```kotlin
implementation("com.infinum.localian:localian:1.0.0")
```

## Setup

Initialize _Localian_ with bare minimum of parameters in your _Application_ class:

``` kotlin
class LocalianApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Localian.run(application = this, locale = Locale("hr", "HR"))
    }
}
```
There is a third parameter in _run_ called _cache_ implemented as a simple Preferences based cache for the selected Locale.

## Features

### Change locale

``` kotlin
        Localian.setLocale(context, Locale.CANADA)
```

_Localian_ is not responsible for updateing all already loaded locale-based data.  
You need to handle it manually, for example, restarting your Activity.

## Follow the system locale

_Localian_ can follow the system Locale and react whenever it changes:

 ``` kotlin
Localian.followSystemLocale(context)
 ```

Any call to `setLocale()` stops following the system Locale and resets `isFollowingSystemLocale()` Boolean. 

## WebView

Starting from *Android N*, there is a [side effect](https://issuetracker.google.com/issues/37113860) when using a [WebView](https://developer.android.com/reference/android/webkit/WebView) in your project.   
The very first creation of WebView, programmatically or via declarative, resets Locale to the system default.  
According to the issue above it will not be fixed any time soon.  

There are several of concepts for a solution of this problem, but the core idea is essentially the same.  
You have to set back your desired Locale after the first usage of a WebView.  
For example, you can programmatically create a fake WebView and immediately set a locale back which prevents this issue.
See the sample app for more details.

## App Bundles

If you are using [app bundle](https://developer.android.com/guide/app-bundle), a user’s device only downloads string resources that match the one or more languages currently selected in the device’s settings.  
When you want to change this behavior and have access to additional language resources, configure as follows:
``` groovy
bundle {
    language {
        enableSplit = false
    }
    density {
        enableSplit = true
    }
    abi {
        enableSplit = true
    }
}
```

## Contributing

Feedback and code contributions are very much welcome. Just make a pull request with a short description of your changes. By making contributions to this project you give permission for your code to be used under the same [license](LICENSE).  
For easier developing a `sample` application with proper implementations is provided.  
It is also recommended to change `build.debug` property in `build.properties` to toggle dependency substitution in project level `build.gradle`.  
If you wish to add a new specific dependency wrapper tool, create a new module and set it up like the ones already provided.  
Then create a pull request.

## License

```
Copyright 2020 Infinum

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Credits

Maintained and sponsored by [Infinum](http://www.infinum.com).

<a href='https://infinum.co'>
  <img src='https://infinum.co/infinum.png' href='https://infinum.com' width='264'>
</a>
