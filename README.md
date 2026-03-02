# L1 Cloning Support Tool — Android App

## How to build the APK (free, no Android Studio needed)

### Option 1 — Buildozer online (easiest)
1. Go to https://appetize.io or https://www.appsource.io
2. Upload this zip — they will build and return the APK

### Option 2 — GitHub Actions (free, recommended)
1. Create a free GitHub account at https://github.com
2. Create a new repository and upload all these files
3. Go to Actions tab → New workflow → paste the workflow below
4. It will build the APK automatically and you download it from the Actions run

GitHub Actions workflow (.github/workflows/build.yml):
```yaml
name: Build APK
on: [push]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Build APK
        run: |
          chmod +x gradlew
          ./gradlew assembleDebug
      - name: Upload APK
        uses: actions/upload-artifact@v3
        with:
          name: l1-cloning-tool-debug
          path: app/build/outputs/apk/debug/app-debug.apk
```

### Option 3 — Android Studio (full control)
1. Download Android Studio free from https://developer.android.com/studio
2. Open this folder as a project
3. Click Build → Build Bundle(s)/APK(s) → Build APK(s)
4. APK will be in app/build/outputs/apk/debug/

## App Features
- Login screen loads l1training.com/login/ — uses your Paid Memberships Pro credentials
- After successful login, loads the Cloning Support Tool full screen
- Pull-to-refresh gesture
- Refresh button in toolbar
- Log Out option in toolbar menu (clears session cookies)
- Back button navigates within the WebView

## Icon
Replace the placeholder icon by dropping your Boot_Icon.png into:
  app/src/main/res/mipmap-xxxhdpi/ic_launcher.png  (192x192)
  app/src/main/res/mipmap-xxhdpi/ic_launcher.png   (144x144)
  app/src/main/res/mipmap-xhdpi/ic_launcher.png    (96x96)
  app/src/main/res/mipmap-hdpi/ic_launcher.png     (72x72)
  app/src/main/res/mipmap-mdpi/ic_launcher.png     (48x48)
And delete the .xml ic_launcher files in each mipmap folder.
