# GeoShare_Android
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
