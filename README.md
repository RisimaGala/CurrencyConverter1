
# 💱 Currency Converter App

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![Minimum SDK](https://img.shields.io/badge/minSdk-23-green.svg)](https://developer.android.com/about/versions/android-6.0)
[![Target SDK](https://img.shields.io/badge/targetSdk-34-orange.svg)](https://developer.android.com/about/versions/android-14)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM-blueviolet.svg)](https://developer.android.com/jetpack/guide)

A modern Android currency conversion application built with **Kotlin**, featuring real-time exchange rates, secure user authentication, and transaction history tracking.

---

<div align="center">

| Conversion Screen | History | Profile |
|:---:|:---:|:---:|
| <img src="screenshots/converter.png" width="200"> | <img src="screenshots/history.png" width="200"> | <img src="screenshots/profile.png" width="200"> |

</div>

---

## ✨ Features

- 🔐 **Secure Authentication** — Login and registration with session management  
- 💰 **Real-Time Conversion** — Live exchange rates for 150+ currencies  
- 📊 **Transaction History** — Local storage with search and filter options  
- 🎨 **Modern UI** — Built with Material Design 3 and supports dark/light themes  
- 📱 **Offline Support** — Access recent conversions even without internet  
- 🔍 **Search & Filter** — Quickly find conversions from your history  
- ⭐ **Favorite Currencies** — Pin frequently used currencies for quick access  

---

## 🏗️ Architecture

📱 Currency Converter App
├── 🎯 UI Layer (Fragments + ViewModels)
├── 🔄 Domain Layer (Repositories + Use Cases)
├── 💾 Data Layer (Local DB + Remote API)
└── 🛠 Infrastructure (DI, Networking, Storage)

yaml
Copy code

---

## ⚙️ Tech Stack

- **Language**: Kotlin  
- **Architecture**: MVVM (Model–View–ViewModel)  
- **UI**: XML Layouts + Material Components  
- **Database**: Room (SQLite)  
- **Networking**: Retrofit + OkHttp  
- **Dependency Injection**: Hilt  
- **Navigation**: Jetpack Navigation Component  
- **Asynchronous**: Coroutines + Flow  

---

## 🚀 Getting Started

### 📋 Prerequisites

- Android Studio **Hedgehog** or later  
- JDK **17+**  
- Android SDK **34**  

---

### 🧩 Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/currency-converter-android.git
   cd currency-converter-android
Open in Android Studio

Open Android Studio

Click File → Open → Select project directory

Wait for Gradle sync to complete

Configure API Keys

Create a local.properties file in the root directory

Add your API configuration:

properties
Copy code
api.key=YOUR_CURRENCY_API_KEY
base.url=https://api.exchangerate.host/
Build and Run

Connect an Android device or start an emulator

Click Run ▶️ or press Shift + F10

📁 Project Structure
pgsql
Copy code
app/
├── data/
│   ├── dao/              # Data Access Objects
│   ├── entity/           # Database Entities
│   ├── network/          # API Clients & DTOs
│   └── repository/       # Data Repositories
├── ui/
│   ├── auth/             # Authentication Screens
│   ├── converter/        # Main Conversion Screen
│   ├── profile/          # User Profile
│   └── settings/         # App Settings
├── utils/                # Helpers & Extensions
└── viewmodel/            # ViewModels
🔧 Configuration
🌐 API Integration
The app uses the ExchangeRate API for fetching real-time currency exchange data.
You can replace the API URL in local.properties or your BuildConfig.

🗃 Database Schema
Built with Room, including entities for:

User data and preferences

Conversion history

Favorite currencies

🛠 Development
Build from Source
bash
Copy code
./gradlew assembleDebug
Run Tests
bash
Copy code
./gradlew test
./gradlew connectedAndroidTest
🤝 Contributing
We welcome contributions!

Fork the repository

Create your feature branch

bash
Copy code
git checkout -b feature/AmazingFeature
Commit your changes

bash
Copy code
git commit -m "Add some AmazingFeature"
Push to your branch

bash
Copy code
git push origin feature/AmazingFeature
Open a Pull Request

📄 License
This project is licensed under the MIT License — see the LICENSE file for details.

🐛 Bug Reports & Feature Requests
Found a bug or have an idea for improvement?
Please open an issue.

<div align="center">
Made with ❤️ using Kotlin and Android Jetpack




</div> ```
✅ Improvements Made:
Fixed markdown syntax errors and inconsistent code blocks

Added clear section separators and icons

Improved readability and hierarchy (headings, lists, spacing)

Organized the setup steps and file structure

Moved badges and footer to visually balanced positions

Ensured all code blocks use the right language tags


💱 Currency Converter
A modern, responsive currency converter web application that provides real-time exchange rates with an intuitive user interface. Built with pure client-side technologies for fast and reliable performance.



✨ Features
Real-Time Exchange Rates - Live data fetched from reliable APIs for accurate currency conversions

Intuitive User Interface - Clean, minimalist design optimized for seamless user experience

Dynamic Currency Selection - Comprehensive dropdown with searchable global currencies

Quick Swap Functionality - Instant currency pair reversal with a single click

Full Responsive Design - Optimized for desktop, tablet, and mobile devices

Lightweight Performance - Built with vanilla JavaScript for fast loading and smooth operation

🚀 Live Demo
Experience the application live: [Live Demo Link]

[Deploy your application using GitHub Pages, Netlify, or Vercel and update this link]

🛠️ Technology Stack
Frontend: HTML5, CSS3, Vanilla JavaScript (ES6+)

API Integration: ExchangeRate-API for real-time currency data

Styling: Modern CSS with Flexbox/Grid layouts and smooth animations

Compatibility: Cross-browser support and mobile-responsive design

📦 Installation & Setup
Follow these steps to run the project locally:

Prerequisites
Web browser with JavaScript enabled

Git (for cloning repository)

Local server (recommended for development)

Quick Start
Clone the repository

bash
git clone https://github.com/RisimaGala/CurrencyConverter1.git
Navigate to project directory

bash
cd CurrencyConverter1
Run the application

Option A: Direct file access

Open index.html in your web browser

Option B: Local development server (recommended)

bash


# Using Node.js (if installed)
npx http-server

# Using PHP
php -S localhost:8000
Access the application at http://localhost:8000

🧩 Usage Guide
Enter Amount: Input the numerical value you want to convert

Select Source Currency: Choose your base currency from the dropdown

Choose Target Currency: Select the currency you want to convert to

Convert: Click "Get Exchange Rate" to perform the conversion

View Results: The converted amount displays instantly below the button

Quick Actions
Use the swap button (↕️) to instantly reverse currency pairs

Search currencies by name or code in dropdown menus

The interface automatically updates with real-time rates

🔧 API Integration
This application integrates with ExchangeRate-API for reliable, real-time currency data.

API Configuration:

Base URL: https://api.exchangerate-api.com/v4/latest/

Request Method: GET

Data Format: JSON

Update Frequency: Rates refresh automatically with each conversion

🏗️ Project Structure
text
CurrencyConverter1/
├── index.html          # Main application file
├── style.css           # Stylesheets and responsive design
├── script.js           # Application logic and API integration
└── README.md           # Project documentation
🤝 Contributing
We welcome contributions from the community! Here's how you can help improve this project:

Contribution Workflow
Fork the Repository

Click 'Fork' on GitHub to create your copy

Create a Feature Branch

bash
git checkout -b feature/YourFeatureName
Make Your Changes

Follow existing code style

Test your changes thoroughly

Commit and Push

bash
git commit -m 'Add: Description of your feature'
git push origin feature/YourFeatureName
Submit Pull Request

Provide clear description of changes

Reference any related issues

Development Guidelines
Write clean, commented code

Ensure cross-browser compatibility

Test responsive design on multiple devices

Update documentation for new features

🐛 Issue Reporting
Found a bug or have a feature request? Please create an issue with:

Detailed description of the problem

Steps to reproduce

Expected vs actual behavior

Browser/device information

📄 License
This project is licensed under the MIT License - see the LICENSE file for details.

📧 Contact & Support
Developer: Risima Gala

Repository: github.com/RisimaGala/CurrencyConverter1

Issues: GitHub Issues

🙏 Acknowledgments
ExchangeRate-API for reliable currency data

Icons provided by Font Awesome

Open-source community for inspiration and resources

<div align="center">
⭐ Star this repository if you find it helpful!

</div>
