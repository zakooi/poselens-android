# PoseLens - GitHub Push Script
# Run this after creating repository on GitHub

# Colors for output
$Green = 'Green'
$Yellow = 'Yellow'
$Red = 'Red'

Write-Host "🚀 PoseLens - GitHub Push Setup" -ForegroundColor $Green
Write-Host "=================================" -ForegroundColor $Green
Write-Host ""

# Get GitHub username
$username = Read-Host "Enter your GitHub username"

if ([string]::IsNullOrWhiteSpace($username)) {
    Write-Host "❌ Username cannot be empty!" -ForegroundColor $Red
    exit 1
}

# Repository name
$repoName = "poselens-android"

Write-Host ""
Write-Host "📋 Repository Info:" -ForegroundColor $Yellow
Write-Host "   Username: $username"
Write-Host "   Repository: $repoName"
Write-Host "   URL: https://github.com/$username/$repoName"
Write-Host ""

# Confirm
$confirm = Read-Host "Have you created the repository on GitHub? (y/n)"
if ($confirm -ne 'y' -and $confirm -ne 'Y') {
    Write-Host ""
    Write-Host "⚠️  Please create the repository first:" -ForegroundColor $Yellow
    Write-Host "   1. Go to https://github.com/new"
    Write-Host "   2. Repository name: $repoName"
    Write-Host "   3. Make it Public or Private"
    Write-Host "   4. DO NOT initialize with README"
    Write-Host "   5. Click 'Create repository'"
    Write-Host ""
    Write-Host "Then run this script again!" -ForegroundColor $Yellow
    exit 0
}

Write-Host ""
Write-Host "🔗 Connecting to GitHub..." -ForegroundColor $Green

# Add remote
$remoteUrl = "https://github.com/$username/$repoName.git"
git remote add origin $remoteUrl 2>$null

if ($LASTEXITCODE -ne 0) {
    Write-Host "⚠️  Remote 'origin' already exists. Updating URL..." -ForegroundColor $Yellow
    git remote set-url origin $remoteUrl
}

# Verify remote
Write-Host "✅ Remote added:" -ForegroundColor $Green
git remote -v

Write-Host ""
Write-Host "📤 Pushing to GitHub..." -ForegroundColor $Green

# Rename branch to main
git branch -M main

# Push to GitHub
git push -u origin main

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "🎉 SUCCESS! Code pushed to GitHub!" -ForegroundColor $Green
    Write-Host ""
    Write-Host "🔗 View your repository at:" -ForegroundColor $Yellow
    Write-Host "   https://github.com/$username/$repoName" -ForegroundColor $Green
    Write-Host ""
    Write-Host "📝 Next steps:" -ForegroundColor $Yellow
    Write-Host "   1. Add repository description and topics"
    Write-Host "   2. Enable GitHub Actions (optional)"
    Write-Host "   3. Invite collaborators (if needed)"
    Write-Host "   4. Start developing! 🚀"
} else {
    Write-Host ""
    Write-Host "❌ Push failed!" -ForegroundColor $Red
    Write-Host ""
    Write-Host "💡 Possible solutions:" -ForegroundColor $Yellow
    Write-Host "   1. Check your GitHub credentials"
    Write-Host "   2. Make sure you have access to the repository"
    Write-Host "   3. Try using SSH instead: git remote set-url origin git@github.com:$username/$repoName.git"
    Write-Host "   4. Create a Personal Access Token at: https://github.com/settings/tokens"
    Write-Host ""
    Write-Host "📖 See GITHUB_PUSH_GUIDE.md for detailed instructions"
}

Write-Host ""
Read-Host "Press Enter to exit"
