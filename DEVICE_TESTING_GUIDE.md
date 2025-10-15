# 📱 HƯỚNG DẪN TEST TRÊN THIẾT BỊ THẬT
## PoseLens Android - Device Testing Guide

**Version:** 1.0.1  
**Last Updated:** October 15, 2025

---

## 📝 Changelog

- 2025-10-15: Updated examples and commands to include Linux/macOS (bash) paths and replaced PowerShell-specific snippets; clarified quick-start path for this repo workspace.

---

## 🎯 TỔNG QUAN

Hướng dẫn này sẽ giúp bạn:
1. ✅ Cài đặt app từ GitHub lên thiết bị Android
2. ✅ Kích hoạt Developer Options
3. ✅ Debug qua USB
4. ✅ Test các tính năng
5. ✅ Thu thập logs và fix bugs

---

## 📋 YÊU CẦU

### Thiết Bị Android:
```
✅ Android 7.0 (API 24) trở lên
✅ Ít nhất 2GB RAM
✅ Khoảng 100MB bộ nhớ trống
✅ Camera hoạt động tốt
✅ Kết nối Internet (cho API testing)
```

### Máy Tính / Development Host:
```
✅ Linux (Ubuntu/Debian) or macOS or Windows 10/11
✅ Android Studio installed
✅ USB cable tốt (không lỗi)
✅ Git installed
✅ JDK 17
✅ adb in PATH (thường được cài cùng Android SDK)
```

---

## 🚀 CÁCH 1: CLONE & BUILD TỪ GITHUB

### Bước 1: Clone Repository

Mở terminal (bash/zsh) trên Linux hoặc macOS, hoặc PowerShell / CMD trên Windows. Ví dụ (Linux/macOS):

```bash
# Di chuyển đến thư mục làm việc (ví dụ thư mục workspace của dự án)
cd ~/projects || cd /workspaces/poselens-android

# Clone repository (nếu chưa có)
git clone https://github.com/zakooi/poselens-android.git

# Hoặc nếu đã clone trước đó, vào thư mục và pull code mới nhất
cd poselens-android
git pull origin main
```

### Bước 2: Mở Project trong Android Studio

1. **Mở Android Studio**
2. **File → Open**
3. Chọn thư mục `poselens-android` (hoặc đường dẫn nơi bạn clone repo, ví dụ `/workspaces/poselens-android`)
4. Chờ Gradle sync xong (2-5 phút lần đầu)

### Bước 3: Kích Hoạt Developer Mode Trên Điện Thoại

#### Trên điện thoại Android:

1. **Mở Settings (Cài đặt)**
2. **About Phone (Về điện thoại)**
3. **Tìm "Build Number" (Số bản dựng)**
4. **Tap liên tục 7 lần** vào Build Number
5. Sẽ thấy thông báo: "You are now a developer!"

#### Kích hoạt USB Debugging:

1. **Quay lại Settings**
2. **System → Developer Options**
3. **Bật "USB Debugging"**
4. **Bật "Install via USB"** (nếu có)
5. **Bật "Stay Awake"** (màn hình luôn sáng khi sạc)

### Bước 4: Kết Nối Điện Thoại với Máy Tính

1. **Cắm cable USB** vào máy tính
2. Trên điện thoại sẽ thấy popup **"Allow USB debugging?"**
3. **Chọn "Always allow from this computer"**
4. **Tap OK**

### Bước 5: Kiểm Tra Kết Nối

Trong Android Studio, kiểm tra toolbar phía trên:

```
[Dropdown thiết bị] → Nên thấy tên điện thoại của bạn
```

Hoặc mở Terminal trong Android Studio:

```bash
# Kiểm tra thiết bị đã kết nối
adb devices

# Kết quả mong đợi:
# List of devices attached
# ABC123XYZ    device
```

Nếu thấy `unauthorized`, hãy:
- Rút dây USB
- Cắm lại
- Accept lại popup trên điện thoại

### Bước 6: Build & Install App

#### Cách 1: Dùng Android Studio (Dễ nhất)

1. Chọn thiết bị trong dropdown
2. Click nút **▶ Run** (hoặc Shift+F10)
3. Chờ build xong (2-3 phút lần đầu)
4. App sẽ tự động cài và chạy trên điện thoại

#### Cách 2: Dùng Gradle Command

Mở Terminal trong Android Studio:

```bash
# Build debug APK
./gradlew assembleDebug

# Install lên thiết bị
./gradlew installDebug

# Hoặc build + install cùng lúc
./gradlew installDebug
```

APK file sẽ ở: `app/build/outputs/apk/debug/app-debug.apk`

---

## 🔧 CÁCH 2: CÀI ĐẶT APK TRỰC TIẾP

### Bước 1: Build APK File

Trong Android Studio:

```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

Hoặc dùng command line:

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK (ký bằng debug key)
./gradlew assembleRelease
```

APK sẽ được tạo tại:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Bước 2: Chuyển APK vào Điện Thoại

#### Cách 2.1: Qua USB Cable

```bash
# Cài APK qua ADB
adb install app/build/outputs/apk/debug/app-debug.apk

# Nếu app đã cài trước đó, dùng -r để reinstall
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

#### Cách 2.2: Qua Google Drive / Cloud

1. Upload APK lên Google Drive
2. Mở Drive trên điện thoại
3. Download APK
4. Tap vào file để cài đặt
5. Nếu bị chặn, vào **Settings → Security → Install unknown apps** → Bật cho app đó

#### Cách 2.3: Qua Email

1. Gửi APK qua email cho chính mình
2. Mở email trên điện thoại
3. Download attachment
4. Cài đặt như cách 2.2

---

## 📊 CÁCH 3: WIRELESS DEBUGGING (Android 11+)

### Setup Wireless ADB

#### Trên Điện Thoại:

1. **Settings → Developer Options**
2. **Wireless debugging → Turn ON**
3. **Pair device with pairing code**
4. Note lại:
   - **IP address + Port** (VD: 192.168.1.100:5555)
   - **Pairing code** (VD: 123456)

#### Trên Máy Tính:

```bash
# Pair device (chỉ cần 1 lần đầu)
adb pair 192.168.1.100:5555
# Nhập pairing code khi được hỏi

# Connect
adb connect 192.168.1.100:5555

# Kiểm tra
adb devices
# Kết quả: 192.168.1.100:5555    device
```

Giờ có thể build & run không cần dây USB!

---

## 🧪 TESTING CHECKLIST

### 1. Khởi Động App

- [ ] App mở được (không crash)
- [ ] Splash screen hiển thị (nếu có)
- [ ] HomeScreen load ra
- [ ] Theme colors đúng
- [ ] Không có lag

### 2. Permissions

- [ ] App request Camera permission
- [ ] App request Storage/Media permission
- [ ] Có thể allow permissions
- [ ] Có thể deny permissions (app xử lý tốt)
- [ ] Có link đến Settings khi deny

### 3. Image Selection (HomeScreen)

Hiện tại HomeScreen có button "Analyze Photo":

```kotlin
// Test flows:
1. Tap "Analyze Photo"
   - [ ] Gallery picker mở ra
   - [ ] Có thể chọn ảnh
   - [ ] Ảnh được load vào app

2. Test với ảnh khác nhau:
   - [ ] Ảnh nhỏ (< 1MB)
   - [ ] Ảnh lớn (> 5MB)
   - [ ] Ảnh portrait (dọc)
   - [ ] Ảnh landscape (ngang)
   - [ ] Ảnh có người
   - [ ] Ảnh phong cảnh
```

### 4. Các Tính Năng Cần Test (Khi UI Hoàn Thiện)

#### AnalyzeScreen (Chưa có - sẽ có tuần này):
- [ ] Scene detection hiển thị đúng
- [ ] Lighting condition chính xác
- [ ] Pose landmarks render ra
- [ ] Confidence scores hiển thị
- [ ] Suggestions list load được
- [ ] Share functionality

#### EditScreen (Chưa có):
- [ ] Image preview với adjustments
- [ ] Sliders hoạt động smooth
- [ ] Preset buttons apply đúng
- [ ] Real-time preview không lag
- [ ] Save to gallery

#### CameraScreen (Chưa có):
- [ ] Camera preview hiển thị
- [ ] Capture button hoạt động
- [ ] Flash toggle
- [ ] Front/back switch
- [ ] Real-time pose overlay

### 5. Performance Testing

```
Mở app và kiểm tra:

Memory Usage (trong Android Profiler):
- [ ] Idle: < 150MB
- [ ] Analyzing image: < 300MB
- [ ] Không memory leak (sau nhiều lần analyze)

CPU Usage:
- [ ] Idle: < 5%
- [ ] Analyzing: < 80%
- [ ] UI smooth 60fps

Battery:
- [ ] Không drain nhanh
- [ ] Không phone bị nóng
```

### 6. Network Testing

```
Test API calls:

1. Với Internet:
   - [ ] Analyze image call API thành công
   - [ ] Templates sync từ server
   - [ ] Upload image works

2. Không có Internet:
   - [ ] App không crash
   - [ ] Hiển thị error message rõ ràng
   - [ ] Local features vẫn hoạt động (ML Kit offline)
   - [ ] Có retry option
```

### 7. Rotation Testing

- [ ] Rotate screen khi đang ở HomeScreen
- [ ] Rotate khi đang analyze (không mất data)
- [ ] Rotate khi đang edit
- [ ] UI adapt tốt cho landscape

### 8. Edge Cases

```
Test các trường hợp đặc biệt:

1. Ảnh lỗi:
   - [ ] Corrupted image file
   - [ ] Unsupported format
   - [ ] Empty file
   - [ ] Very large image (>20MB)

2. System:
   - [ ] Low storage warning
   - [ ] Low battery
   - [ ] Incoming call (app pause/resume tốt)
   - [ ] Switch to other app và back

3. Permissions:
   - [ ] Revoke permission trong Settings
   - [ ] App request lại khi cần
```

---

## 🐛 DEBUG & TROUBLESHOOTING

### Xem Logs Realtime

#### Cách 1: Android Studio Logcat

```
View → Tool Windows → Logcat

Filters:
- Package: com.example.poselens
- Log Level: Debug
- Search: "PoseLens" hoặc tag cụ thể
```

#### Cách 2: ADB Command

```bash
# Xem tất cả logs
adb logcat

# Filter theo app (Linux/macOS)
adb logcat | grep --line-buffered "com.example.poselens"

# Filter theo tag
adb logcat -s "PoseLens"

# Clear logs cũ
adb logcat -c

# Save logs to file
adb logcat > logs.txt
```

### Common Issues & Solutions

#### Issue 1: App Crash Ngay Khi Mở

**Symptoms:** App mở rồi đóng ngay lập tức

**Check:**
```bash
# Xem crash log
adb logcat -s "AndroidRuntime:E"
```

**Common Causes:**
- Thiếu Hilt setup (@HiltAndroidApp, @AndroidEntryPoint)
- Missing dependencies trong DI modules
- ProGuard rules quá strict

**Fix:**
1. Check AndroidManifest có `android:name=".PoseLensApplication"`
2. Verify Hilt setup trong build.gradle
3. Check ProGuard rules

#### Issue 2: Permission Denied

**Symptoms:** Không thể chọn ảnh, không mở camera

**Check:**
```bash
# List permissions
adb shell dumpsys package com.example.poselens | grep "permission"
```

**Fix:**
1. Uninstall app completely
2. Reinstall
3. Allow permissions khi được hỏi

#### Issue 3: "Device Offline" trong adb devices

**Fix:**
```bash
# Restart ADB
adb kill-server
adb start-server

# Reconnect
adb devices
```

#### Issue 4: Gradle Build Failed

**Common Errors:**

```
Error: "Failed to install the following Android SDK packages"
Fix: 
→ Tools → SDK Manager → Install missing components

Error: "Kotlin version mismatch"
Fix:
→ Check kotlin version in build.gradle matches

Error: "Execution failed for task ':app:mergeDebugResources'"
Fix:
→ Clean project: Build → Clean Project
→ Rebuild: Build → Rebuild Project
```

#### Issue 5: App Quá Chậm

**Profiling:**

1. **Android Studio → View → Tool Windows → Profiler**
2. **Select device → Select app**
3. **Record:**
   - CPU usage
   - Memory allocation
   - Network activity

**Common Fixes:**
- Enable ProGuard (release build)
- Optimize image loading
- Add caching
- Reduce memory allocations

---

## 📊 PERFORMANCE PROFILING

### Memory Profiler

```
1. Run app on device
2. Android Studio → View → Tool Windows → Profiler
3. Click "+" → PoseLens
4. Memory tab

Actions:
- Trigger analyze image nhiều lần
- Watch for memory leaks
- Check GC (Garbage Collection) frequency
- Dump heap if needed
```

### CPU Profiler

```
1. CPU tab in Profiler
2. Record trace
3. Analyze image (trigger heavy operations)
4. Stop recording

Look for:
- Methods taking > 100ms
- Blocking UI thread
- Inefficient algorithms
```

### Network Profiler

```
1. Network tab in Profiler
2. Trigger API calls
3. Check:
   - Request/response times
   - Payload sizes
   - Failed requests
```

---

## 📝 BUG REPORTING

### Bug Report Template

Khi tìm thấy bug, note lại theo format:

```markdown
## Bug Title
[Short description]

### Device Info
- Model: [e.g., Samsung Galaxy S21]
- Android Version: [e.g., Android 13]
- App Version: 1.0.0-debug

### Steps to Reproduce
1. Open app
2. Tap "Analyze Photo"
3. Select image from gallery
4. [Bug occurs here]

### Expected Behavior
[What should happen]

### Actual Behavior
[What actually happens]

### Screenshots
[Attach screenshots if applicable]

### Logcat Output
```
[Paste relevant logs]
```

### Frequency
- [ ] Always
- [ ] Sometimes (50%)
- [ ] Rare (<10%)

### Severity
- [ ] Critical (crash, data loss)
- [ ] High (feature broken)
- [ ] Medium (workaround exists)
- [ ] Low (cosmetic)
```

### Nơi Report Bugs

Tạo GitHub Issue tại: https://github.com/zakooi/poselens-android/issues

---

## 🎬 VIDEO RECORDING

### Record Screen for Bug Reports

#### Trên Điện Thoại:

```
1. Swipe down từ trên xuống (notification panel)
2. Tìm "Screen recorder"
3. Tap để bắt đầu record
4. Reproduce bug
5. Stop recording
6. Video saved to Gallery
```

#### Qua ADB:

```bash
# Start recording (max 3 minutes)
adb shell screenrecord /sdcard/bug-video.mp4

# Stop: Ctrl+C

# Pull video to computer
adb pull /sdcard/bug-video.mp4
```

---

## 🚀 ADVANCED: RELEASE BUILD

### Tạo Release APK (Ký Release Key)

**CHƯA CẦN LÀM BÂY GIỜ** - chỉ khi sẵn sàng production

#### Bước 1: Tạo Keystore

```bash
keytool -genkey -v -keystore poselens-release.keystore -alias poselens -keyalg RSA -keysize 2048 -validity 10000

# Nhập thông tin:
# Password: [tạo password mạnh]
# Name, Organization, etc.
```

#### Bước 2: Config Signing trong Gradle

Tạo file `keystore.properties`:

```properties
storePassword=YOUR_STORE_PASSWORD
keyPassword=YOUR_KEY_PASSWORD
keyAlias=poselens
storeFile=../poselens-release.keystore
```

#### Bước 3: Update build.gradle.kts

```kotlin
android {
    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file("keystore.properties")
            val keystoreProperties = Properties()
            keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(...)
        }
    }
}
```

#### Bước 4: Build Release APK

```bash
./gradlew assembleRelease

# APK tại: app/build/outputs/apk/release/app-release.apk
```

---

## 📋 TESTING BEST PRACTICES

### Daily Testing Routine

```
Mỗi khi có code mới:

1. Clean build:
   ./gradlew clean
   ./gradlew assembleDebug

2. Install fresh:
   adb uninstall com.example.poselens
   adb install app/build/outputs/apk/debug/app-debug.apk

3. Test core flows:
   - App launch
   - Permission requests
   - Main feature (analyze image khi ready)
   - Navigation
   - Rotation

4. Check logs for errors/warnings

5. Profile if performance feels off
```

### Test on Multiple Devices (If Possible)

```
Ideal test matrix:

Low-end:
- Android 7.0-8.0
- 2GB RAM
- Slow CPU

Mid-range:
- Android 10-11
- 4GB RAM
- Average CPU

High-end:
- Android 12-13
- 6GB+ RAM
- Fast CPU

Different Screen Sizes:
- Small (480dp)
- Normal (720dp)
- Large (1080dp+)
- Tablet (960dp+)
```

---

## 🎯 CURRENT STATUS (Oct 14, 2025)

### ✅ Có Thể Test:

```
1. App Installation
   - Install APK thành công
   - App icon hiển thị
   - App mở được

2. HomeScreen
   - UI render đúng
   - Navigation bar
   - Quick action cards
   - Theme (light/dark)

3. Theme System
   - Material 3 colors
   - Dynamic colors (Android 12+)
   - Dark mode toggle

4. Permissions (khi implement camera)
   - Request permissions
   - Handle allow/deny
```

### ⚠️ Chưa Thể Test (UI chưa có):

```
1. Image Analysis
   - AnalyzeScreen (tuần này sẽ có)
   - Scene detection results
   - Pose landmarks
   - Suggestions

2. Image Editing
   - EditScreen (tuần sau)
   - Adjustments
   - Presets

3. Camera
   - CameraScreen (2 tuần nữa)
   - Real-time detection
   - Capture
```

### 🐛 Expected Issues:

```
Vì UI chưa hoàn thiện, có thể gặp:

1. Buttons không làm gì
   → Normal, ViewModels chưa integrate use cases

2. "Coming soon" messages
   → Feature chưa implement

3. Empty screens
   → Screens chưa tạo

4. Navigation crashes
   → Routes tồn tại nhưng destination chưa có
```

---

## 🔥 QUICK START (TL;DR)

```bash
# 1. Enable Developer Mode trên điện thoại
Settings → About Phone → Tap "Build Number" 7 lần

# 2. Enable USB Debugging
Settings → Developer Options → USB Debugging ON

# 3. Connect phone via USB
# Accept popup "Allow USB debugging" trên phone

# 4. Build & Install (ở workspace này)
cd /workspaces/poselens-android
./gradlew installDebug

# 5. App sẽ tự động cài và mở!
# Hoặc mở manually từ app drawer

# 6. Test cơ bản
- App mở được ✅
- HomeScreen hiển thị ✅
- Theme đẹp ✅
- Navigation works ✅
```

---

## 📞 SUPPORT

### Nếu gặp vấn đề:

1. **Check Logcat** - 90% issues có thể debug qua logs
2. **Google error message** - Stack Overflow is your friend
3. **Clean & Rebuild** - Fix nhiều weird issues
4. **Restart ADB** - Fix connection issues
5. **Reboot Phone** - Last resort 😅

### Useful Commands Cheat Sheet

```bash
# Device management
adb devices                          # List devices
adb reboot                           # Reboot device
adb shell                            # Shell into device

# App management
adb install app.apk                  # Install APK
adb install -r app.apk               # Reinstall
adb uninstall com.example.poselens   # Uninstall

# Logging
adb logcat                           # View logs
adb logcat -c                        # Clear logs
adb logcat > log.txt                 # Save logs

# Files
adb push local.txt /sdcard/          # Copy to device
adb pull /sdcard/file.txt .          # Copy from device

# Screen
adb shell screencap /sdcard/s.png    # Screenshot
adb shell screenrecord /sdcard/v.mp4 # Record video

# Debugging
adb shell dumpsys meminfo com.example.poselens  # Memory usage
adb shell dumpsys cpuinfo                       # CPU usage
adb shell pm list packages | Select-String "pose"  # Find package
```

---

## 🎉 KẾT LUẬN

Với guide này, bạn có thể:
✅ Clone project từ GitHub  
✅ Build & install lên thiết bị thật  
✅ Test app toàn diện  
✅ Debug issues  
✅ Profile performance  
✅ Report bugs chuyên nghiệp  

**Happy Testing!** 🚀📱

---

**Document Version:** 1.0.0  
**Last Updated:** October 14, 2025  
**Next Update:** Sau khi AnalyzeScreen hoàn thành

---

## 📚 REFERENCES

- [Android Developer - Test Your App](https://developer.android.com/studio/test)
- [ADB Documentation](https://developer.android.com/studio/command-line/adb)
- [Android Studio Profiler](https://developer.android.com/studio/profile)
- [Material Design Testing](https://m3.material.io/foundations/design-tokens/how-to-read-tokens)
