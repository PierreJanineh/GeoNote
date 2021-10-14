# GeoNote
GeoNote is a Location based Notes App that saves every note with a Location to be displayed later on a Map.

## Features
The Android App lets you:
- **Login/Signup** to your own account on GeoNote.
- Create new notes and edit them **online**.
- View existing notes on a **map** as **pins**.
- **Edit/Delete** existing notes.
- **Logout** of your account on a device.

## Technologies
GeoNote uses multiple technologies to get you to the final app you are able to use on your phone, some of the feature technologies are:

- **Firebase** to authorize your Login, and **Firestore Database** to store your GeoNotes.
- Device's **Location**.
- **Google Maps SDK**.

## Permissions
- Location.

## Database Structure

GeoNote depends on Firestore Database to provide and store its data.

|Collection (Users)           |Collection (Notes)             |
|-----------------------------|-------------------------------|
|Document (`User1`)           |Document (`Note1`)             |
|Document (`User2`)           |Document (`Note1`)             |
|Document (`User3`)           |Document (`Note1`)             |

The Base Collection has all **Users** as **Documents** signed-up on GeoNote.
Each **User** has a **Notes Collection**, which has all **Note Documents**.
