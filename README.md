# BuddAlert
## Demo -> [https://www.youtube.com/watch?v=UYK8agCXa5g](https://youtu.be/qOxR9mVf90A)

## About
This is an android application to set alarms for places. Users can set their desired location and the app will notify them when they reach their destination. Users also have the option of notifying their contacts with their current location after a set interval. This app also serves as a reminder application. Users can set location based reminders and the application will remind them on the day or when they are in the proximity of the location. The app also has a SOS feature, which immediately sends the user's current location along with a 10 second audio clip from the user's end.
<br>


## Technical Specifications
Locatadora uses Google Maps Places API to fetch location attributes. The entire backend is handled with the help of Firebase. Firebase authentication service is used for signing in with phone number and Google. The data for every user is stored in a Firebase Firestore collection with the ID generated during the login process as the "primary?" key.

## System diagram
![alt text](https://github.com/hunkyxstudman/LocataDora/blob/master/assets/sys_diagram.png?raw=true)

