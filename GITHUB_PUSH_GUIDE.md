# 🚀 Push to GitHub - Quick Guide

## Bước 1: Tạo Repository trên GitHub

### Option A: Qua GitHub Website (Recommended)
1. Đăng nhập vào [GitHub](https://github.com)
2. Click nút "+" ở góc trên bên phải → "New repository"
3. Điền thông tin:
   - **Repository name**: `poselens-android`
   - **Description**: `AI-powered camera assistant for Android - Help users take better photos with pose suggestions and smart editing`
   - **Visibility**: 
     - ✅ Public (nếu muốn open source)
     - ⬜ Private (nếu muốn giữ riêng tư)
   - ⬜ **Initialize with README** (KHÔNG check - ta đã có rồi)
4. Click "Create repository"

### Option B: Qua GitHub CLI (Nếu đã cài `gh`)
```bash
gh repo create poselens-android --public --source=. --remote=origin --push
```

---

## Bước 2: Connect Local Repository to GitHub

Sau khi tạo repository trên GitHub, copy URL và chạy:

```bash
# Replace YOUR_USERNAME với username GitHub của bạn
cd d:\ANDROIAPP\PoseLens

# Thêm remote origin
git remote add origin https://github.com/YOUR_USERNAME/poselens-android.git

# Hoặc dùng SSH (nếu đã setup SSH key)
git remote add origin git@github.com:YOUR_USERNAME/poselens-android.git

# Đổi tên branch thành main (nếu cần)
git branch -M main

# Push code lên GitHub
git push -u origin main
```

---

## Bước 3: Verify

1. Refresh GitHub repository page
2. Bạn sẽ thấy tất cả files đã được upload
3. README.md sẽ được hiển thị tự động

---

## 📋 Commands Summary

```powershell
# Navigate to project
cd d:\ANDROIAPP\PoseLens

# Check Git status
git status

# Add remote (thay YOUR_USERNAME)
git remote add origin https://github.com/YOUR_USERNAME/poselens-android.git

# Verify remote
git remote -v

# Rename branch to main
git branch -M main

# Push to GitHub
git push -u origin main
```

---

## 🔐 Authentication

### If prompted for credentials:

**Option 1: Personal Access Token (Recommended)**
1. GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Generate new token với quyền: `repo`, `workflow`
3. Copy token
4. Dùng token làm password khi Git prompt

**Option 2: GitHub CLI**
```bash
gh auth login
```

**Option 3: SSH Key**
```bash
# Generate SSH key
ssh-keygen -t ed25519 -C "your_email@example.com"

# Copy public key
cat ~/.ssh/id_ed25519.pub

# Add to GitHub: Settings → SSH Keys → New SSH Key
```

---

## ✅ What's Included in the Commit

- ✅ 28 files committed
- ✅ Complete project structure
- ✅ Gradle configuration
- ✅ AndroidManifest with permissions
- ✅ Domain models
- ✅ UI theme & components
- ✅ Home Screen implementation
- ✅ Navigation system
- ✅ ViewModels
- ✅ Documentation (README, SETUP_GUIDE, PROJECT_STATUS)
- ✅ .gitignore (no sensitive files)
- ✅ GitHub Actions CI/CD workflow

---

## 🎉 After Pushing

### Enable GitHub Actions (Optional)
1. Go to repository → Actions tab
2. Enable workflows
3. CI will run automatically on push/PR

### Add Repository Topics
Add topics to help others find your repo:
- `android`
- `kotlin`
- `jetpack-compose`
- `machine-learning`
- `ml-kit`
- `camera`
- `ai`
- `pose-detection`
- `photography`

### Add Repository Description
Go to repository settings and add:
```
🤳 AI-powered camera assistant for Android. Get pose suggestions, scene analysis, and smart photo editing powered by ML Kit and Jetpack Compose. Built with Clean Architecture.
```

---

## 📝 Next Commits

When you make changes:
```bash
# Stage changes
git add .

# Commit with message
git commit -m "Your commit message"

# Push to GitHub
git push
```

---

## 🌟 Bonus: Create Development Branch

```bash
# Create and switch to develop branch
git checkout -b develop

# Push develop branch
git push -u origin develop

# Set develop as default branch on GitHub
# Repository → Settings → Branches → Default branch
```

---

## 🔗 Useful Links

- [GitHub Documentation](https://docs.github.com)
- [Git Cheat Sheet](https://education.github.com/git-cheat-sheet-education.pdf)
- [GitHub CLI](https://cli.github.com/)

---

**Ready to push! 🚀**

Làm theo các bước trên để đẩy code lên GitHub nhé!
