# Weather Application

## Overview

This is a Weather application built in Kotlin for Android, following **Clean Architecture** principles and modularization for better maintainability and scalability. The app utilizes modern libraries and tools such as **Ktor** for networking, **Dagger Hilt** for Dependency Injection, **Android Room** for local database management, and **Jetpack Compose** for building the UI.

API keys are securely managed using **Google Secrets Manager** to avoid hardcoding sensitive data in the source code.

Unit tests are implemented for the Repository and UseCase layers using **MockK** to ensure correctness of the business logic.

---

## Technologies Used

- **Kotlin**: Primary programming language.
- **Jetpack Compose**: For declarative UI development.
- **Dagger Hilt**: For Dependency Injection.
- **Ktor**: For making asynchronous HTTP requests to fetch weather and currency exchange data.
- **Kotlinx Serialization**: For JSON parsing and serialization of network responses in a type-safe and efficient manner.
- **Android Room**: For local database storage of user data and cached weather info.
- **Google Secrets Manager**: To securely store and retrieve API keys.
- **MockK**: For mocking dependencies during unit testing.
- **Clean Architecture**: For clear separation of concerns across the app layers.
- **Unit Testing**: To verify business logic correctness in the UseCase layer.

---

## Project Structure

The project is modularized into multiple Gradle modules, each with a specific responsibility:

- **:app**  
  The main Android application module that ties together all components and modules.

- **:data**  
  Implements data sources including remote API calls via Ktor and local persistence using Room.

- **:domain**  
  Contains business logic including UseCases for currency conversion, balance management, and commission calculation.

- **:presentation:ui**  
  Implements the UI components including theming, styling, and all common reusable UI elements using Jetpack Compose. This module handles UI presentation and visual consistency across the app.

- **:presentation:ui-model**  
  Serves as a common UI module shared across all app modules, providing reusable UI models, data classes, and UI-related utilities to ensure consistency and reduce duplication.

- **:presentation:weather**  
  Implements the Weather app screens and related functionality following the MVVM architecture pattern using ViewModels. It also incorporates elements of the MVI (Model-View-Intent) pattern to manage Compose UI state and handle user events in a unidirectional data flow, ensuring a clear separation between state, events, and side effects for better maintainability and testability.

---

## API Key Management

API keys are stored securely using **Google Secrets Manager** and accessed during build time or runtime. This approach prevents exposing sensitive keys in the codebase or repository.

To configure the API key locally, add the following to your `local.properties` (ignored by git):

```properties
API_KEY=your_open_weather_api_key

   ```

---

## Unit Testing

Unit tests have been implemented for the **UseCase** layer, which handles the core business logic of currency conversion and balance management. The tests use **MockK** to mock dependencies and ensure the UseCase works as expected.

To run the unit tests, use the following command in Android Studio:

- Go to **Run > Run Tests**.
- Select the test configuration for your UseCase tests.

## Architecture

### Clean Architecture

The app follows **Clean Architecture** with the following layers:

- **Presentation Layer**: Contains the UI components using **Compose** that interact with the ViewModel.
- **Domain Layer**: Contains the UseCase, which holds the business logic for currency conversion, balance management, and commission calculation.
- **Data Layer**: Handles API interactions (via Ktor) to fetch currency exchange rates and persists the data in a local database (using Android Rome).
- **Dependency Injection**: **Dagger Hilt** is used to manage dependencies across the app, ensuring the app's modules remain loosely coupled and maintainable.

---

### Modularization

The project is split into multiple modules for better maintainability and scalability. Each module is designed to have a single responsibility and can be easily extended in the future.

---

### What I'd add if I had more time

- Getting the user's current location using GPS to show weather based on real-time position.
- Adding more failure handling for edge cases like no internet, GPS disabled, or API errors.
- Implementing caching for offline support.
- Improving the UI/UX for different screen sizes and orientations.
- Supporting multiple languages and temperature units (°C/°F).
- Inject Dispatcher and Scope using Hilt, make more Testing friendly.
