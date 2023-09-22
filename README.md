# GeoNote 🌍

GeoNote is a sophisticated location-based notes application that seamlessly integrates with your device's geolocation capabilities. Every note you create is saved with a specific location, allowing you to visualize and recall memories or tasks based on where you were.

## Core Features 🌟

- **User Authentication**: Securely login or signup to your personalized GeoNote account.
- **Online Note Management**: Create, edit, and manage your notes in real-time.
- **Map Visualization**: View your notes on a map, represented as pins, giving you a spatial perspective of your memories or tasks.
- **Comprehensive Note Operations**: Beyond creation, you can edit or delete existing notes as per your needs.
- **User Session Management**: Logout from your account on any device for security and privacy.

## Under the Hood 🛠

GeoNote leverages cutting-edge technologies to deliver a smooth user experience:
- **Firebase**: Powers user authentication and ensures secure access.
- **Firestore Database**: A robust NoSQL database that stores all your GeoNotes.
- **Device Geolocation**: Harnesses your device's location capabilities to pin notes.
- **Google Maps SDK**: Provides the map interface for visualizing notes.

## Permissions 🔒

For GeoNote to function optimally, it requires access to:
- Device's Location.

## Getting Started
1. Clone the repository: `git clone https://github.com/PierreJanineh/GeoNote.git`
2. Open the project in Android Studio.
3. Set up Firebase and connect it to the app.
4. Build and run the app on an emulator or a real device.

## Firestore Database Structure 📦

GeoNote's backbone is the Firestore Database, structured as follows:

The base collection encompasses all **Users** as individual **Documents** registered on GeoNote. Each **User** has an associated **Notes Collection**, containing all their **Note Documents**.

| Collection (Users) | Collection (Notes) |
|--------------------|--------------------|
| Document (`User1`) | Document (`Note1`)|
| Document (`User2`) | Document (`Note1`)|
| Document (`User3`) | Document (`Note1`)|
