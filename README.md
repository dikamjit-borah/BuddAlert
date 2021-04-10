# LocataDora
Trigger alarm on reaching destination

##About
Locatadora is an android application to set alarms for destinations. Users can set their desired location and Locatadora will notify them when they reach their destination. Users also have the option of notifying some contacts about their current location after some interval. Locatadora also serves as a reminder application, in the sense that users can set location based reminders and the application will remind them either on the day or when they are in the proximity of the location.
<br>


##Technical Specifications
Locatadora uses Google Maps Places API to fetch location attributes. The entire backend is handled with the help of Firebase. Firebase authentication service is used for signing in with phone number and Google. The data for every user is stored in a Firebase Firestore collection with the ID generated during the login process as the "primary?" key.

##System diagram
![alt text](https://github.com/hunkyxstudman/LocataDora/blob/master/assets/sys_diagram.png?raw=true)

