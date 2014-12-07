Purpose
=======

Purpose of the app is to allow you to be in touch with your family members by constantly being able to see where they are currently and getting in touch with them.

The main features of the app are:
* Map view which shows markers with pictures of your family members showing their current location.
* Allows you to talk to them through phone calls or pre-canned or custom messages when you click on their information marker.
* Add a family member by selecting their contact from your contacts.
* Updates your location marker as and when your location changes. 


High Level Architecture
=======================

The app has a backend and a frontend component. 

**Backend**

The backend component is present on heroku which fetches the member data from the server. The member data is of the format:

```    {
    name: "Niti Verma",
    latitude: 37.3894,
    longitude: -122.0819,
    timestamp: 1412664869279,
    id: "0",
    phoneno: "1202321233",
    image: "http://qph.is.quoracdn.net/main-thumb-12400824-200-kuczsoyvyravqapcqfiknbxwiueuubjc.jpeg"
    }
```

It has fields for name, latitude, longitude, timestamp for the location, id, phone no and picture. 

**Frontend**

The frontend is the android app which allows the user to consume the information about their friends and also update their own information and add family members. 
In terms of the features used on the android app: 
 
  * It is a single activity app. The activity shows the GoogleMaps fragment from the google play services. 
  * It uses GoogleMaps api and shows the information about the person as a custom marker. The click events on the markers are intercepted and an implicit intent to text or call the family member is done based on the user's input on the dialog box.
  * The action bar is used to show the menu item for adding a new family member. The family member is entered by allowing the user to pick a contact from their contacts app and once the information is received; we send a pre-canned text message to invite the user to downlaod the app. This is achieved through the sms text intent.

**The app makes use of the following android techniques a lot:**
* Implicit intents to open up messages, dialer, contacts app.
* Uses googleplay services and Google maps API to show the location of the various markers.
* Uses locationProvider from the googleplay services for getting the up-to-date location of the device as and when the location changes.
* Uses actionbar menu items

** Things to improve **
* Provide a signup page in order to allow the user to signup for the app.
* Allow user to accept and decline family member requests using notifications
* Update the backend to use a good database to store the information and history.
* Allow users to see family members location history
* Upload the app on play store and get real user feedback to improve and build upon.