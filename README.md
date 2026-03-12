# Pokédex App

A modern Android application built with Jetpack Compose that consumes the [PokeAPI](https://pokeapi.co/) to display, filter, and search for Pokémon. 

## 🏗 Architecture & Tech Stack
This project follows modern Android development best practices:
* **UI Toolkit:** Jetpack Compose
* **Architecture:** MVVM (Model-View-ViewModel) with Unidirectional Data Flow
* **State Management:** Kotlin `StateFlow`
* **Networking:** Retrofit & OkHttp
* **Asynchrony:** Kotlin Coroutines (including Job cancellation for search debouncing)

## ✨ Key Features
* **Dynamic Type Filtering:** Fetches and displays Pokémon by categories (Fire, Water, Grass, etc.).
* **Smart Search System:** * Prioritizes instant local filtering to minimize network requests.
  * Features a **Global API Fallback** that queries the PokeAPI if local results are empty.
  * Uses Coroutine Job cancellation to debounce rapid user input.
* **Robust State Handling:** Seamlessly manages UI states between Loading, Success (Grid), and Error (Not Found) using `Crossfade` animations.
* **Pagination:** Implements an "infinite scroll" mechanism to load batches of 10 Pokémon at a time.

## 🚀 How to Run
1. Clone or download the repository.
2. Open the project in **Android Studio**.
3. Sync Gradle files.
4. Build and run the app on an emulator or physical device.