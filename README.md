# GeoShare

## Dependency updates
To check for dependency updates run **dependency update.bat**

Currently all updates are done manually and the new versions are shown as below.

```
  The following dependencies are using the latest milestone version:
   - com.android.support:appcompat-v7:28.0.0-rc01
   - com.android.support:cardview-v7:28.0.0-rc01
   - com.android.support:design:28.0.0-rc01
   - com.android.support:recyclerview-v7:28.0.0-rc01
   - com.android.support:support-v4:28.0.0-rc01

  The following dependencies have later milestone versions:
   - com.android.support.constraint:constraint-layout [1.1.2 -> 2.0.0-alpha2]
       http://tools.android.com
   - com.android.tools.lint:lint-gradle [26.1.4 -> 26.3.0-alpha05]
       https://developer.android.com/studio

  Gradle updates:
   - Gradle: [4.9 -> 4.10-rc-1]
```

## Firebase and Maps API setup
To build and successfully run this app you will need to set it up with Firebase and the Google Maps API.

- Firebase:
  - Create a new project on Firebase and download the **google-services.json** file
  - Place this file in **/GeoShare_Android/mobile**
  
- Maps Api
  - Enable the Maps API on Google Cloud Platform and set up an Android key
  - Create a new resouce file in Android Studio, place it in **/res/values** and call it api.xml
  - Paste the API key from Google Cloud into a new entry in this file. The end result should look like this
  
  ```xml
    <resources>
      <string name="api_key">api_key_goes_here</string>
    </resources>
  ```
