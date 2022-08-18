# Localian

Localian is a library that manipulates your application locale and language across multiple Android API levels with possibility not to restart application process.

Create a new _Localian_ instance with desired _Locale_ and make your application use proper localized data via Resources class from according Context.

## Usage
To include _Localian_ in your project, you have to add buildscript dependencies in your project level `build.gradle` or `build.gradle.kts`:

**Groovy**
```groovy
buildscript {
    repositories {
        mavenCentral()
    }
}
```
**KotlinDSL**
```kotlin
buildscript {
    repositories {
        mavenCentral()
    }
}
```

Then add the following dependencies in your app `build.gradle` or `build.gradle.kts` :

**Groovy**
```groovy
implementation "com.infinum.localian:localian:1.0.7"
```
**KotlinDSL**
```kotlin
implementation("com.infinum.localian:localian:1.0.7")
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
An interface _Localian.Cache_ has been exposed for any specific implementations a project might need.  

## Features

### Change locale

``` kotlin
Localian.setLocale(context, Locale.CANADA)
```

_Localian_ is not responsible for updating all already loaded locale-based data.
You need to handle it manually, for example, restarting your Activity.  
However, _Localian_ does provide a _Localian.Callback_ when _Locale_ has changed which you can use to react and restart currently visible Activity or take any other suitable actions.  

## Follow the system locale

_Localian_ can follow the system Locale and react whenever it changes:

 ``` kotlin
Localian.followSystemLocale(context)
 ```

Any call to `setLocale()` stops following the system Locale and resets `isFollowingSystemLocale()` Boolean to _false_. 

## WebView

Starting from *Android N*, there is a [side effect](https://issuetracker.google.com/issues/37113860) when using a [WebView](https://developer.android.com/reference/android/webkit/WebView) in your project.   
The very first creation of WebView of any kind, programmatically or declarative, resets Locale to the system default.  
According to the issue above it will not be fixed any time soon.  

There are several of concepts for a solution of this problem, but the core idea is essentially the same.  
You have to set back your desired Locale after the first usage of a WebView.  
For example, you can programmatically create a fake WebView and immediately set a Locale back which prevents this issue.  

_Localian_ attempts to fix this by preemptive first usage of a WebView inside an AndroidX Startup initializer. For most cases this will be enough to bypass the issue.  
If this doesn't help or presents a performance issue, startup initializer can be disabled but _LocalianWebViewPatcher_ class can be explicitly instantiated instead when appropriate.  

### Disable automatic initialization for WebView patching

In you module Android manifest file, add or modify with following code:
 ``` xml
<provider
    android:name="androidx.startup.InitializationProvider"
    android:authorities="${applicationId}.androidx-startup"
    android:exported="false"
    tools:node="merge">
    <meta-data android:name="com.infinum.localian.LocalianInitializer"
        tools:node="remove" />
</provider>
 ```

### Passing language tag to initializer

If you can keep the initializer but you still observe unexpected Locale changes, you can pass a language tag from Android manifest via meta data to AndroidX Startup initializer.  
In you module Android manifest file, add following meta data code in your _application_ XML node:
 ``` xml
<application
    
    ...
    
    <meta-data
        android:name="com.infinum.localian.initial_locale_language_tag"
        android:value="hr-HR" />
            
</application>
 ```
Please pay attention to correct and exact value of meta data _name_ attribute and the fact that _value_ attribute must be a language tag string that conforms to the IETF BCP 47 standard, so no underscores, but hyphens. 

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

<p align="center">
  <a href='https://infinum.com'>
    <picture>
        <source srcset="https://assets.infinum.com/brand/logo/static/white.svg" media="(prefers-color-scheme: dark)">
        <img src="https://assets.infinum.com/brand/logo/static/default.svg">
    </picture>
  </a>
</p>
