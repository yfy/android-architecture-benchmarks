# BaseArchitecture: Android Architecture Patterns Benchmark Study

A comprehensive research project comparing 5 Android architecture patterns implemented with Jetpack Compose using **Clean Architecture** principles and the **API/Impl separation technique**. The project includes performance benchmarks, static code analysis, and architectural evaluations. Data is primarily served from mock JSON files, with only images being fetched from API endpoints.

## 📋 Overview

This project implements the same three features (Cart, Chat, Product List) using **5 different Android architecture patterns**:
- **Classic MVVM** (ViewModel + StateFlow)
- **MVC** (Model-View-Controller)
- **MVP** (Model-View-Presenter)
- **Single-State MVVM** (Custom State Pattern)
- **MVI** (Model-View-Intent)

Each implementation features nearly identical UI and business logic, allowing for fair performance and code quality comparisons. The project follows **Clean Architecture** principles with clear separation between domain, data, and presentation layers. The **API/Impl separation technique** is used throughout, allowing for easy swapping between implementations (e.g., mock vs. real implementations).

### Data Sources

- **Mock Data**: All business data (products, cart items, chat messages, user profiles) are served from local JSON files located in the `src/mock/resources/` directories of each feature module.
- **API Data**: Only images are fetched from remote API endpoints. All other data is mock-based to ensure consistent benchmarking conditions.

## 🏗️ Project Structure

### Core Architecture

The project uses a modular **Clean Architecture** with clear separation of concerns, implementing the **API/Impl pattern** for all core and feature modules:

```
BaseArchitecture/
├── core/                          # Shared core modules
│   ├── designsystem/              # UI components & theme
│   ├── model/                     # Data models
│   ├── navigation/                # Navigation setup
│   ├── ui-api/                    # UI interfaces
│   ├── ui-impl/                   # UI implementations
│   ├── network-api/               # Network interfaces
│   ├── network-impl/              # Network implementations
│   ├── analytics-api/             # Analytics interfaces
│   ├── analytics-impl/            # Analytics implementations
│   ├── database-api/              # Database interfaces
│   ├── database-impl/             # Database implementations
│   ├── datastore-api/             # DataStore interfaces
│   ├── datastore-impl/            # DataStore implementations
│   ├── notification-api/          # Notification interfaces
│   └── notification-impl/         # Notification implementations
│
├── feature/                       # Feature modules
│   ├── cart-impl/                 # Default Cart (Single-State MVVM)
│   ├── cart-impl-classicmvvm/     # Classic MVVM Cart
│   ├── cart-impl-mvc/             # MVC Cart
│   ├── cart-impl-mvp/             # MVP Cart
│   ├── cart-impl-mvi/             # MVI Cart
│   │
│   ├── product-impl/              # Default Product (Single-State MVVM)
│   ├── product-impl-classicmvvm/  # Classic MVVM Product
│   ├── product-impl-mvc/          # MVC Product
│   ├── product-impl-mvp/          # MVP Product
│   └── product-impl-mvi/          # MVI Product
│   │
│   ├── chat-impl/                 # Default Chat (Single-State MVVM)
│   ├── chat-impl-classicmvvm/     # Classic MVVM Chat
│   ├── chat-impl-mvc/             # MVC Chat
│   ├── chat-impl-mvp/             # MVP Chat
│   └── chat-impl-mvi/             # MVI Chat
│
└── benchmark/                     # MacroBenchmark tests
```

### Technology Stack

- **Language**: Kotlin 2.1.10
- **UI Framework**: Jetpack Compose
- **Architecture**: 5 patterns (MVVM, MVC, MVP, Single-State MVVM, MVI)
- **Dependency Injection**: Hilt
- **Async**: Kotlin Coroutines + Flow
- **Build**: Gradle 8.x with Version Catalog
- **Min SDK**: 21
- **Target SDK**: 36
- **Java Version**: 11

## 🎯 Three Implemented Features

### 1. Cart Feature
Shopping cart management with:
- Add/remove items
- Quantity updates
- Multi-step checkout flow
- Address selection
- Payment method selection

### 2. Chat Feature
Real-time messaging with:
- Chat list with latest messages
- Chat detail with message history
- Real-time message streaming
- Send/receive messages

### 3. Product List Feature
E-commerce product browsing with:
- Product list with grid view
- Category filtering
- Pagination
- Product details

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2024.1.1) or later
- JDK 11 or higher
- Android SDK 36
- Gradle 8.x

### ⚙️ Architecture Configuration

**Important**: Only **one architecture pattern** can be active at a time. To test a specific architecture, you must configure the `app/build.gradle.kts` file.

#### Configuring Architecture Implementation

In `app/build.gradle.kts`, locate the dependency section and configure it as follows. **Uncomment only the architecture you want to test** for each feature (Product, Cart, Chat), and keep the other 4 architectures commented out:

```kotlin
// Product - Select ONE architecture
//implementation(projects.feature.productImpl)              // Single-State MVVM
//implementation(projects.feature.productImplMvp)          // MVP
//implementation(projects.feature.productImplMvc)          // MVC
//implementation(projects.feature.productImplClassicmvvm)   // Classic MVVM
//implementation(projects.feature.productImplMvi)           // MVI

// Cart - Select ONE architecture
//implementation(projects.feature.cartImpl)                 // Single-State MVVM
//implementation(projects.feature.cartImplMvp)               // MVP
//implementation(projects.feature.cartImplMvc)              // MVC
//implementation(projects.feature.cartImplClassicmvvm)      // Classic MVVM
//implementation(projects.feature.cartImplMvi)               // MVI

// Chat - Select ONE architecture
//implementation(projects.feature.chatImpl)                 // Single-State MVVM
//implementation(projects.feature.chatImplMvp)              // MVP
//implementation(projects.feature.chatImplMvc)              // MVC
//implementation(projects.feature.chatImplClassicmvvm)      // Classic MVVM
//implementation(projects.feature.chatImplMvi)               // MVI
```

**Example**: To test MVP architecture, uncomment all `*Mvp` implementations and keep others commented.

### Build

```bash
# Clone the repository
git clone https://github.com/yfy/android-architecture-benchmarks

# Build the project
./gradlew assembleDebug

# Run benchmarks (see Benchmark section below)
./gradlew :benchmark:connectedCheck
```

### 📊 Running Benchmarks

Benchmarks are configured to run using `BenchmarkTestSuite` class. **All modules must use the `mockRelease` build variant** for accurate benchmarking.

1. **Select Architecture**: Configure `app/build.gradle.kts` as described above
2. **Select Build Variant**: Ensure `mockRelease` is selected for all modules (especially the `app` module)
3. **Run Benchmarks**: Execute `BenchmarkTestSuite` using Android Studio's test runner or via Gradle:

```bash
# Run all benchmarks
./gradlew :benchmark:connectedCheck

# Or run specific benchmark test
./gradlew :benchmark:connectedCheck --tests "BenchmarkTestSuite"
```

**Note**: Make sure your device or emulator is connected and unlocked before running benchmarks.

### Run Static Analysis

```bash
# Run Detekt
./gradlew detekt

# Run Lint
./gradlew lint

# SonarQube analysis (requires local SonarQube server configuration)
# Configure SonarQube in sonar-project.properties and run:
# ./gradlew sonar
```

## 📁 Key Documentation

### Raw Data

- **Raw Benchmark Results**: `rawdata/` contains unprocessed raw benchmark results in JSON format for each architecture:
  - `classicmvvm_result.json`
  - `mvc_result.json`
  - `mvp_result.json`
  - `mvi_result.json`
  - `singlestatemvvm_result.json`
  - `runs_detail.json` - Detailed runs data extracted from raw benchmark result files

### Processed Data

- **Processed and Consolidated Results**: `analysis_result/` contains processed and consolidated benchmark data extracted from the raw data:
  - `benchmarks.csv` - Performance benchmark metrics for all architectures
  - `memory.csv` - Memory usage metrics
  - `static.csv` - Static code analysis metrics
  - `memory_phases.csv` - Memory usage by phase
  - `statistical_tests.csv` - Statistical comparison tests between architectures

## 🤝 Contributing

This is a research project for thesis purposes. Contributions and feedback are welcome through issues or pull requests.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👨‍💻 Author

Yusuf Furkan Yılmaz - Master Thesis Research Project

## 🔗 References

- [Android Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Hilt Dependency Injection](https://dagger.dev/hilt/)
- [MacroBenchmark](https://developer.android.com/topic/performance/benchmarking/benchmarking-in-app)
- [Detekt Static Analysis](https://detekt.github.io/detekt/)

---

**Note**: This project is part of a university thesis research project on Android architecture patterns and their impact on code quality and performance.
