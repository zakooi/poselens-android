# 🚀 PoseLens - Setup Guide

Chào mừng đến với **PoseLens**! Đây là hướng dẫn chi tiết để setup và chạy project.

---

## ✅ Điều kiện tiên quyết

### Phần mềm cần thiết:
- ✅ **Android Studio**: Hedgehog (2023.1.1) hoặc mới hơn
- ✅ **JDK**: Java 17 (được bundle với Android Studio)
- ✅ **Android SDK**: 
  - Min SDK: 24 (Android 7.0)
  - Target SDK: 34 (Android 14)
  - Compile SDK: 34

### Thiết bị test:
- 📱 **Physical device** (recommended): Để test camera và ML features tốt nhất
- 💻 **Emulator**: Pixel 6 hoặc cao hơn với API 34

---

## 📦 Cài đặt

### Bước 1: Mở project trong Android Studio

1. Launch **Android Studio**
2. Click **File → Open**
3. Navigate đến folder `PoseLens`
4. Click **OK**

### Bước 2: Cấu hình SDK Path

1. Mở file `local.properties`
2. Update `sdk.dir` với đường dẫn SDK của bạn:
   ```properties
   # Windows
   sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
   
   # macOS/Linux
   sdk.dir=/Users/YourUsername/Library/Android/sdk
   ```

### Bước 3: Sync Gradle

1. Android Studio sẽ tự động prompt **Sync Now**
2. Hoặc click **File → Sync Project with Gradle Files**
3. Đợi Gradle download dependencies (~5-10 phút lần đầu)

**Note**: Nếu có lỗi sync, check:
- ✅ Internet connection stable
- ✅ JDK 17 được cấu hình đúng
- ✅ Android SDK installed đầy đủ

### Bước 4: Setup App Icons (Optional)

Project hiện tại chưa có app icons. Để tạo icons:

**Option 1: Sử dụng Android Studio Image Asset Studio**
1. Right-click `res` folder
2. New → Image Asset
3. Icon Type: Launcher Icons
4. Upload icon image của bạn
5. Generate icons

**Option 2: Placeholder icons**
- Project sẽ sử dụng default Android icon tạm thời

---

## 🏃‍♂️ Chạy ứng dụng

### Trên Physical Device (Recommended)

1. **Enable Developer Options** trên điện thoại:
   - Settings → About Phone
   - Tap "Build number" 7 lần
   
2. **Enable USB Debugging**:
   - Settings → Developer Options
   - Turn on "USB Debugging"

3. **Connect device** qua USB cable

4. **Click Run** ▶️ trong Android Studio (hoặc Shift+F10)

5. Select device trong dialog và click **OK**

### Trên Emulator

1. **Create Emulator**:
   - Tools → Device Manager
   - Click "Create Device"
   - Select Pixel 6
   - Download System Image (API 34)
   - Finish

2. **Run**:
   - Click Run ▶️
   - Select emulator
   - Đợi emulator boot up (~2-3 phút)

---

## 🧪 Testing

### Run Unit Tests
```bash
# Terminal trong Android Studio
./gradlew test

# Hoặc
./gradlew testDebugUnitTest
```

### Run UI Tests (Instrumented)
```bash
./gradlew connectedAndroidTest
```

### Lint Check
```bash
./gradlew lint
```

---

## 🔧 Build Commands

### Debug Build
```bash
./gradlew assembleDebug
```
APK output: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build (Unsigned)
```bash
./gradlew assembleRelease
```
APK output: `app/build/outputs/apk/release/app-release-unsigned.apk`

### Install on Device
```bash
# Install debug
./gradlew installDebug

# Install release
./gradlew installRelease
```

### Clean Build
```bash
./gradlew clean
./gradlew build
```

---

## 📱 Features hiện tại (MVP v1.0.0)

### ✅ Đã hoàn thành:
- [x] Project structure với Clean Architecture
- [x] Jetpack Compose UI setup
- [x] Hilt Dependency Injection setup
- [x] Navigation system
- [x] Home Screen UI
- [x] Theme & Design System
- [x] Domain models (SceneAnalysis, PoseResult, EditPreset)

### 🚧 Đang phát triển:
- [ ] Scene analysis với ML Kit
- [ ] Pose detection integration
- [ ] Analyze Screen UI
- [ ] Edit Screen UI
- [ ] Image editing tools
- [ ] Camera integration

---

## 🐛 Troubleshooting

### Lỗi Gradle Sync Failed

**Giải pháp**:
```bash
# 1. Invalidate Caches
File → Invalidate Caches → Invalidate and Restart

# 2. Clean project
./gradlew clean

# 3. Delete .gradle folder và rebuild
```

### Lỗi "Manifest merger failed"

**Giải pháp**:
- Check AndroidManifest.xml syntax
- Đảm bảo không có duplicate permissions
- Clean và rebuild

### Lỗi "SDK location not found"

**Giải pháp**:
1. Mở `local.properties`
2. Add đúng đường dẫn SDK:
   ```
   sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
   ```

### App crashes khi mở

**Giải pháp**:
1. Check Logcat trong Android Studio
2. Look for stack trace
3. Đảm bảo Hilt đã setup đúng (@HiltAndroidApp, @AndroidEntryPoint)

### ML Kit models không download

**Giải pháp**:
- Đảm bảo internet connection
- Check Play Services updated trên device
- Thử restart app

---

## 📚 Tài liệu bổ sung

### Official Documentation:
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [ML Kit Pose Detection](https://developers.google.com/ml-kit/vision/pose-detection)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [CameraX](https://developer.android.com/training/camerax)

### Project Structure:
```
PoseLens/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/poselens/
│   │   │   ├── PoseLensApplication.kt       # App entry point
│   │   │   ├── MainActivity.kt               # Main activity
│   │   │   ├── domain/                       # Business logic & models
│   │   │   │   └── model/
│   │   │   │       ├── SceneAnalysisResult.kt
│   │   │   │       ├── PoseResult.kt
│   │   │   │       └── EditPreset.kt
│   │   │   ├── presentation/                 # UI layer
│   │   │   │   ├── ui/
│   │   │   │   │   ├── navigation/
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   └── home/
│   │   │   │   │   └── theme/
│   │   │   │   │       ├── Color.kt
│   │   │   │   │       ├── Theme.kt
│   │   │   │   │       └── Type.kt
│   │   │   └── [data layer - coming soon]
│   │   ├── res/                              # Resources
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🎯 Next Steps

Sau khi setup xong, bạn có thể:

1. **Explore Home Screen**: Chạy app và xem UI
2. **Add ML Kit**: Implement scene analysis (Task #3)
3. **Camera Integration**: Thêm CameraX (Task #4)
4. **Build Analyze Screen**: UI cho analysis results (Task #10)

---

## 📞 Support

Nếu gặp vấn đề:
1. Check **Troubleshooting** section phía trên
2. Search [StackOverflow](https://stackoverflow.com/questions/tagged/android)
3. Check [Android Developers](https://developer.android.com/)

---

## 📄 License

MIT License © 2025 PoseLens Team

**Happy Coding! 🚀📸**
