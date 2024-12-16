# Hospital Management System

A comprehensive Hospital Management System Android application designed to streamline core hospital operations such as patient record management, doctor scheduling, appointment booking, inventory tracking and more. Built using Java and Firebase, this app provides an intuitive interface for both patients and admins, ensuring efficiency and enhanced patient care workflows.

## Features

### General Features
- **Secure Authentication**: Patients sign up with E-mail, while admins (Super Admin, Doctors) have pre-configured accounts.
- **Splash Screen**: A welcoming splash screen with a custom theme.
- **Navigation Bar**: Simplified navigation with categorized options.

### Patient-Specific Features
- **Appointment Booking**:
  - Book doctor appointments (auto-assigned slots for morning or evening sessions).
  - Schedule physical tests (daily availability).
- **Profile Management**: Update personal details securely.
- **Dashboard**: View available doctors, specialties and assigned room numbers.
- **Schedule Overview**: Check upcoming appointments and bookings.

### Admin-Specific Features
- **Doctor Management**:
  - Add, update, and delete doctor profiles.
  - Assign or change room numbers.
- **Staff Management**:
  - Add, update and remove staff members.
  - Assign roles (Receptionist, Nurse etc.).
  - View staff details and schedules.
- **Inventory Management**:
  - Hospital inventories and pharmacy inventories.
  - Add and view current items.
- **Patient Management**:
  - Update patient information.
- **Physical Test Management**:
  - Manage Physical Tests
- **Appointments Overview**:
  - Doctors can view appointments for the current day and the next week.

### Technical Constraints
- **Doctor Availability**: Morning (9 AM–12 PM) and Evening (6 PM–9 PM), 20 patients per session.
- **Physical Tests**: Available from 9 AM to 8 PM with a daily cap of 50 patients per test.
- **Booking Rules**:
  - Doctor appointments: Up to 3 days in advance, one active booking per doctor at a time.
  - Physical tests: Bookable one day in advance.

## Tech Stack
- **Languages**: Java, XML
- **Database**: Firebase Authentication, Firebase Realtime Database
- **API Integration**: Custom JSON API for dynamic doctor data management ([API Link](https://api.myjson.online/v1/records/a1e637d2-51da-4f94-b95f-521440530f21)).
- **UI Design**: Material Design components

## Installation

1. Clone this repository:
   git clone https://github.com/your-username/hospital-management-system.git
2. Open the project in Android Studio.
3. Sync the Gradle files.
4. Set up Firebase:
   - Add your `google-services.json` file in the `app/` directory.
   - Configure Firebase Authentication and Realtime Database.
5. Run the project on an emulator or a physical Android device.

## Acknowledgments
- [Firebase](https://firebase.google.com/) for backend services.
- Material Design for UI components.

Feel free to reach out for any queries or suggestions!
