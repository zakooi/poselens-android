# 🧪 TESTING GUIDE
## PoseLens Android - Comprehensive Testing Strategy

**Version:** 1.0.0  
**Last Updated:** October 14, 2025

---

## 📑 TABLE OF CONTENTS

1. [Testing Philosophy](#testing-philosophy)
2. [Test Coverage Goals](#test-coverage-goals)
3. [Unit Tests](#unit-tests)
4. [Integration Tests](#integration-tests)
5. [UI Tests](#ui-tests)
6. [Manual Testing Checklist](#manual-testing-checklist)
7. [Test Data & Fixtures](#test-data--fixtures)
8. [CI/CD Integration](#cicd-integration)

---

## 🎯 TESTING PHILOSOPHY

### Testing Pyramid
```
           /\
          /  \         E2E Tests (10%)
         /____\        UI tests, full flows
        /      \
       /        \      Integration Tests (30%)
      /__________\     Repository, DB, API tests
     /            \
    /              \   Unit Tests (60%)
   /________________\  Use cases, utils, models
```

### Principles:
1. **Fast Feedback** - Unit tests run in <1s
2. **Isolated Tests** - No shared state between tests
3. **Deterministic** - Same input → Same output
4. **Readable** - Test names describe behavior
5. **Maintainable** - Easy to update when requirements change

---

## 📊 TEST COVERAGE GOALS

### Target Coverage:
```
Domain Layer:       95%+ coverage
Data Layer:         85%+ coverage
Presentation Layer: 70%+ coverage
Overall:            80%+ coverage
```

### Priority Matrix:
```
HIGH Priority (Must Test):
✓ Use cases
✓ Repository implementations
✓ ML analyzers
✓ Image utilities
✓ Mappers

MEDIUM Priority (Should Test):
✓ ViewModels
✓ DAOs
✓ API service
✓ Extensions

LOW Priority (Nice to Test):
✓ UI screens
✓ Theme
✓ Constants
```

---

## 🔬 UNIT TESTS

### Setup Dependencies
```kotlin
// app/build.gradle.kts
dependencies {
    // Unit testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("app.cash.turbine:turbine:1.0.0")
    testImplementation("com.google.truth:truth:1.1.5")
    
    // Android testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
```

---

### 1. Use Case Tests

#### Example: AnalyzeImageUseCase Test
```kotlin
// test/java/com/example/poselens/domain/usecase/AnalyzeImageUseCaseTest.kt
@ExperimentalCoroutinesTest
class AnalyzeImageUseCaseTest {

    private lateinit var useCase: AnalyzeImageUseCase
    private lateinit var repository: ImageRepository
    private val testDispatcher = UnconfinedTestDispatcher()
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = AnalyzeImageUseCase(repository, testDispatcher)
    }
    
    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `invoke returns success when repository succeeds`() = runTest {
        // Given
        val mockUri = mockk<Uri>()
        val expectedResult = SceneAnalysisResult(
            sceneType = SceneType.NATURE,
            confidence = 0.95f,
            lightingCondition = LightingCondition.BRIGHT to 0.9f,
            dominantColors = listOf("#00FF00", "#0000FF"),
            detectedObjects = emptyList(),
            composition = CompositionAnalysis()
        )
        coEvery { repository.analyzeImage(mockUri) } returns flowOf(Result.Success(expectedResult))
        
        // When
        val results = useCase(mockUri).toList()
        
        // Then
        assertEquals(1, results.size)
        assertTrue(results[0] is Result.Success)
        assertEquals(expectedResult, (results[0] as Result.Success).data)
    }
    
    @Test
    fun `invoke returns error when repository fails`() = runTest {
        // Given
        val mockUri = mockk<Uri>()
        val exception = Exception("Network error")
        coEvery { repository.analyzeImage(mockUri) } returns flowOf(Result.Error(exception, "Failed"))
        
        // When
        val results = useCase(mockUri).toList()
        
        // Then
        assertEquals(1, results.size)
        assertTrue(results[0] is Result.Error)
    }
    
    @Test
    fun `invoke emits loading state first`() = runTest {
        // Given
        val mockUri = mockk<Uri>()
        coEvery { repository.analyzeImage(mockUri) } returns flow {
            emit(Result.Loading)
            delay(100)
            emit(Result.Success(mockk()))
        }
        
        // When
        val results = useCase(mockUri).take(1).toList()
        
        // Then
        assertTrue(results[0] is Result.Loading)
    }
}
```

#### Example: DetectPoseUseCase Test
```kotlin
@ExperimentalCoroutinesTest
class DetectPoseUseCaseTest {

    private lateinit var useCase: DetectPoseUseCase
    private lateinit var poseRepository: PoseRepository
    private val testDispatcher = UnconfinedTestDispatcher()
    
    @Before
    fun setup() {
        poseRepository = mockk()
        useCase = DetectPoseUseCase(poseRepository, testDispatcher)
    }

    @Test
    fun `detectPose returns pose result when detection succeeds`() = runTest {
        // Given
        val mockUri = mockk<Uri>()
        val expectedPose = PoseResult(
            landmarks = mapOf("nose" to Pair(100f, 100f)),
            confidence = 0.9f,
            detectedAt = System.currentTimeMillis()
        )
        coEvery { poseRepository.detectPose(mockUri) } returns flowOf(Result.Success(expectedPose))
        
        // When
        val result = useCase.detectPose(mockUri).first()
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedPose, (result as Result.Success).data)
    }
    
    @Test
    fun `trackRealtime emits poses continuously`() = runTest {
        // Given
        val mockBitmap = mockk<Bitmap>()
        val poses = listOf(
            PoseResult(landmarks = mapOf("nose" to Pair(100f, 100f)), confidence = 0.9f, detectedAt = 1L),
            PoseResult(landmarks = mapOf("nose" to Pair(110f, 105f)), confidence = 0.85f, detectedAt = 2L),
            PoseResult(landmarks = mapOf("nose" to Pair(120f, 110f)), confidence = 0.88f, detectedAt = 3L)
        )
        coEvery { poseRepository.trackPoseRealtime(mockBitmap) } returns flow {
            poses.forEach { emit(Result.Success(it)) }
        }
        
        // When
        val results = useCase.trackRealtime(mockBitmap).take(3).toList()
        
        // Then
        assertEquals(3, results.size)
        results.forEachIndexed { index, result ->
            assertTrue(result is Result.Success)
            assertEquals(poses[index], (result as Result.Success).data)
        }
    }
}
```

#### Example: GetPoseSuggestionsUseCase Test
```kotlin
@ExperimentalCoroutinesTest
class GetPoseSuggestionsUseCaseTest {

    private lateinit var useCase: GetPoseSuggestionsUseCase
    private lateinit var poseRepository: PoseRepository
    private lateinit var templateRepository: TemplateRepository
    private val testDispatcher = UnconfinedTestDispatcher()
    
    @Before
    fun setup() {
        poseRepository = mockk()
        templateRepository = mockk()
        useCase = GetPoseSuggestionsUseCase(poseRepository, templateRepository, testDispatcher)
    }

    @Test
    fun `invoke combines quality validation and template suggestions`() = runTest {
        // Given
        val currentPose = PoseResult(
            landmarks = mapOf("nose" to Pair(100f, 100f), "left_shoulder" to Pair(80f, 120f)),
            confidence = 0.7f,
            detectedAt = System.currentTimeMillis()
        )
        val sceneType = "NATURE"
        val qualitySuggestions = listOf("Improve lighting", "Center subject")
        val templateSuggestions = listOf("Try standing pose", "Arms extended")
        
        coEvery { poseRepository.getPoseSuggestions(currentPose, sceneType) } returns 
            Result.Success(qualitySuggestions)
        coEvery { templateRepository.getRecommendedTemplates(any(), any()) } returns 
            Result.Success(listOf(mockk { every { name } returns "Standing Pose" }))
        
        // When
        val result = useCase(currentPose, sceneType).first()
        
        // Then
        assertTrue(result is Result.Success)
        val suggestions = (result as Result.Success).data
        assertTrue(suggestions.isNotEmpty())
        assertTrue(suggestions.size <= 10) // Max 10 suggestions
    }
    
    @Test
    fun `invoke returns scene-specific suggestions for SUNSET`() = runTest {
        // Given
        val currentPose = mockk<PoseResult>()
        val sceneType = "SUNSET"
        coEvery { poseRepository.getPoseSuggestions(any(), any()) } returns Result.Success(emptyList())
        coEvery { templateRepository.getRecommendedTemplates(any(), any()) } returns Result.Success(emptyList())
        
        // When
        val result = useCase(currentPose, sceneType).first()
        
        // Then
        assertTrue(result is Result.Success)
        val suggestions = (result as Result.Success).data
        assertTrue(suggestions.any { it.contains("silhouette", ignoreCase = true) })
    }
}
```

---

### 2. Repository Tests

#### Example: ImageRepositoryImpl Test
```kotlin
@ExperimentalCoroutinesTest
class ImageRepositoryImplTest {

    private lateinit var repository: ImageRepositoryImpl
    private lateinit var context: Context
    private lateinit var imageAnalyzer: ImageAnalyzer
    private lateinit var apiService: ApiService
    private val testDispatcher = UnconfinedTestDispatcher()
    
    @Before
    fun setup() {
        context = mockk(relaxed = true)
        imageAnalyzer = mockk()
        apiService = mockk()
        repository = ImageRepositoryImpl(context, imageAnalyzer, apiService, testDispatcher)
        
        // Mock ImageUtils
        mockkObject(ImageUtils)
    }
    
    @After
    fun tearDown() {
        unmockkObject(ImageUtils)
        clearAllMocks()
    }

    @Test
    fun `analyzeImage emits Loading then Success`() = runTest {
        // Given
        val mockUri = mockk<Uri>()
        val mockBitmap = mockk<Bitmap>(relaxed = true)
        val expectedResult = SceneAnalysisResult(
            sceneType = SceneType.BEACH,
            confidence = 0.92f,
            lightingCondition = LightingCondition.BRIGHT to 0.95f,
            dominantColors = listOf("#FFD700"),
            detectedObjects = emptyList(),
            composition = CompositionAnalysis()
        )
        
        every { ImageUtils.loadBitmapFromUri(context, mockUri) } returns mockBitmap
        coEvery { imageAnalyzer.analyzeScene(mockBitmap) } returns Result.Success(expectedResult)
        
        // When
        val results = repository.analyzeImage(mockUri).toList()
        
        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Success)
        assertEquals(expectedResult, (results[1] as Result.Success).data)
        verify { mockBitmap.recycle() } // Verify cleanup
    }
    
    @Test
    fun `applyImageAdjustments applies brightness correctly`() = runTest {
        // Given
        val originalBitmap = createTestBitmap(100, 100, Color.rgb(128, 128, 128))
        val adjustments = ImageAdjustments(brightness = 50) // Increase brightness
        
        // When
        val resultBitmap = repository.applyImageAdjustments(originalBitmap, adjustments)
        
        // Then
        assertNotNull(resultBitmap)
        val centerPixel = resultBitmap.getPixel(50, 50)
        val originalPixel = originalBitmap.getPixel(50, 50)
        assertTrue(centerPixel != originalPixel) // Color changed
        
        // Cleanup
        originalBitmap.recycle()
        resultBitmap.recycle()
    }
    
    private fun createTestBitmap(width: Int, height: Int, color: Int): Bitmap {
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            val canvas = Canvas(this)
            canvas.drawColor(color)
        }
    }
}
```

---

### 3. ML Analyzer Tests

#### Example: PoseEstimator Test
```kotlin
@ExperimentalCoroutinesTest
class PoseEstimatorTest {

    private lateinit var estimator: PoseEstimator
    private lateinit var poseDetector: PoseDetector
    
    @Before
    fun setup() {
        poseDetector = mockk()
        estimator = PoseEstimator(poseDetector)
    }

    @Test
    fun `calculatePoseSimilarity returns 1_0 for identical poses`() {
        // Given
        val landmarks = mapOf(
            "nose" to Pair(100f, 100f),
            "left_shoulder" to Pair(80f, 120f),
            "right_shoulder" to Pair(120f, 120f)
        )
        val pose1 = PoseResult(landmarks, 0.9f, System.currentTimeMillis(), 1920, 1080)
        val pose2 = PoseResult(landmarks, 0.9f, System.currentTimeMillis(), 1920, 1080)
        
        // When
        val similarity = estimator.calculatePoseSimilarity(pose1, pose2)
        
        // Then
        assertEquals(1.0f, similarity, 0.01f)
    }
    
    @Test
    fun `calculatePoseSimilarity returns lower score for different poses`() {
        // Given
        val landmarks1 = mapOf("nose" to Pair(100f, 100f), "left_shoulder" to Pair(80f, 120f))
        val landmarks2 = mapOf("nose" to Pair(150f, 150f), "left_shoulder" to Pair(130f, 170f))
        val pose1 = PoseResult(landmarks1, 0.9f, System.currentTimeMillis(), 1920, 1080)
        val pose2 = PoseResult(landmarks2, 0.9f, System.currentTimeMillis(), 1920, 1080)
        
        // When
        val similarity = estimator.calculatePoseSimilarity(pose1, pose2)
        
        // Then
        assertTrue(similarity < 1.0f)
        assertTrue(similarity > 0.0f)
    }
    
    @Test
    fun `validatePoseQuality suggests centering when pose is off-center`() {
        // Given
        val landmarks = mapOf(
            "nose" to Pair(1800f, 100f), // Far right
            "left_shoulder" to Pair(1780f, 120f),
            "right_shoulder" to Pair(1820f, 120f)
        )
        val pose = PoseResult(landmarks, 0.9f, System.currentTimeMillis(), 1920, 1080)
        
        // When
        val suggestions = estimator.validatePoseQuality(pose)
        
        // Then
        assertTrue(suggestions.any { it.contains("center", ignoreCase = true) })
    }
}
```

---

### 4. Utility Tests

#### Example: ImageUtils Test
```kotlin
class ImageUtilsTest {

    @Test
    fun `compressBitmap reduces file size while maintaining aspect ratio`() {
        // Given
        val originalBitmap = Bitmap.createBitmap(2000, 1500, Bitmap.Config.ARGB_8888)
        val quality = 80
        
        // When
        val compressedBitmap = ImageUtils.compressBitmap(originalBitmap, quality)
        
        // Then
        assertNotNull(compressedBitmap)
        assertTrue(compressedBitmap.width <= 1920)
        assertTrue(compressedBitmap.height <= 1080)
        assertEquals(
            originalBitmap.width.toFloat() / originalBitmap.height,
            compressedBitmap.width.toFloat() / compressedBitmap.height,
            0.01f
        )
        
        // Cleanup
        originalBitmap.recycle()
        compressedBitmap.recycle()
    }
    
    @Test
    fun `createThumbnail maintains aspect ratio`() {
        // Given
        val originalBitmap = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888)
        val thumbnailSize = 200
        
        // When
        val thumbnail = ImageUtils.createThumbnail(originalBitmap, thumbnailSize)
        
        // Then
        assertNotNull(thumbnail)
        assertTrue(thumbnail.width <= thumbnailSize)
        assertTrue(thumbnail.height <= thumbnailSize)
        assertTrue(thumbnail.width == thumbnailSize || thumbnail.height == thumbnailSize)
        
        // Cleanup
        originalBitmap.recycle()
        thumbnail.recycle()
    }
    
    @Test
    fun `bitmapToBase64 produces valid base64 string`() {
        // Given
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        
        // When
        val base64 = ImageUtils.bitmapToBase64(bitmap)
        
        // Then
        assertNotNull(base64)
        assertTrue(base64.isNotEmpty())
        // Verify it's valid base64 (should not throw exception)
        assertDoesNotThrow { android.util.Base64.decode(base64, android.util.Base64.DEFAULT) }
        
        // Cleanup
        bitmap.recycle()
    }
}
```

---

## 🔗 INTEGRATION TESTS

### 1. Room Database Tests

```kotlin
// androidTest/java/com/example/poselens/data/local/dao/PoseTemplateDaoTest.kt
@RunWith(AndroidJUnit4::class)
class PoseTemplateDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: PoseTemplateDao
    
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.poseTemplateDao()
    }
    
    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndRetrieveTemplate() = runTest {
        // Given
        val template = PoseTemplateEntity(
            id = "test-1",
            name = "Standing Pose",
            description = "Basic standing",
            category = "BASIC",
            difficulty = "EASY",
            thumbnailUrl = "http://example.com/thumb.jpg",
            imageUrl = "http://example.com/image.jpg",
            landmarkPositions = "{}",
            confidence = 0.9f,
            tags = "[\"standing\"]",
            isFavorite = false,
            usageCount = 0,
            isCustom = false
        )
        
        // When
        dao.insertTemplate(template)
        val retrieved = dao.getAllTemplates().first()
        
        // Then
        assertEquals(1, retrieved.size)
        assertEquals(template.id, retrieved[0].id)
        assertEquals(template.name, retrieved[0].name)
    }
    
    @Test
    fun searchTemplatesFindsMatches() = runTest {
        // Given
        val templates = listOf(
            PoseTemplateEntity("1", "Yoga Tree", "", "YOGA", "MEDIUM", "", "", "{}", 0.9f, "[]", false, 0, 0, false, 0, 0),
            PoseTemplateEntity("2", "Warrior Pose", "", "YOGA", "HARD", "", "", "{}", 0.9f, "[]", false, 0, 0, false, 0, 0),
            PoseTemplateEntity("3", "Standing Basic", "", "BASIC", "EASY", "", "", "{}", 0.9f, "[]", false, 0, 0, false, 0, 0)
        )
        templates.forEach { dao.insertTemplate(it) }
        
        // When
        val results = dao.searchTemplates("yoga").first()
        
        // Then
        assertEquals(1, results.size)
        assertTrue(results[0].name.contains("Yoga", ignoreCase = true))
    }
    
    @Test
    fun toggleFavoriteUpdatesStatus() = runTest {
        // Given
        val template = PoseTemplateEntity("1", "Test", "", "BASIC", "EASY", "", "", "{}", 0.9f, "[]", false, 0, 0, false, 0, 0)
        dao.insertTemplate(template)
        
        // When
        dao.updateFavoriteStatus("1", true)
        val updated = dao.getTemplateById("1").first()
        
        // Then
        assertNotNull(updated)
        assertTrue(updated!!.isFavorite)
    }
    
    @Test
    fun incrementUsageCountWorks() = runTest {
        // Given
        val template = PoseTemplateEntity("1", "Test", "", "BASIC", "EASY", "", "", "{}", 0.9f, "[]", false, 0, 0, false, 0, 0)
        dao.insertTemplate(template)
        
        // When
        dao.incrementUsageCount("1")
        dao.incrementUsageCount("1")
        val updated = dao.getTemplateById("1").first()
        
        // Then
        assertNotNull(updated)
        assertEquals(2, updated!!.usageCount)
    }
}
```

---

### 2. API Service Tests

```kotlin
// androidTest/java/com/example/poselens/data/remote/ApiServiceTest.kt
@RunWith(AndroidJUnit4::class)
class ApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService
    
    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
        
        apiService = retrofit.create(ApiService::class.java)
    }
    
    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun analyzeImageReturnsSuccessResponse() = runTest {
        // Given
        val responseJson = """
            {
                "status": "success",
                "data": {
                    "analysis_id": "test-123",
                    "scene_analysis": {
                        "scene_type": "NATURE",
                        "confidence": 0.95
                    },
                    "suggestions": ["Great composition"]
                }
            }
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(responseJson).setResponseCode(200))
        
        val request = AnalyzeImageRequest(imageBase64 = "base64string", includePose = true, includeScene = true)
        
        // When
        val response = apiService.analyzeImage(request)
        
        // Then
        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
        assertEquals("success", response.body()?.status)
    }
    
    @Test
    fun getTemplatesHandlesPagination() = runTest {
        // Given
        val responseJson = """
            {
                "status": "success",
                "data": {
                    "templates": [],
                    "total": 100,
                    "page": 2,
                    "page_size": 50
                }
            }
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(responseJson).setResponseCode(200))
        
        // When
        val response = apiService.getTemplates(page = 2, pageSize = 50)
        
        // Then
        assertTrue(response.isSuccessful)
        val data = response.body()?.data
        assertEquals(2, data?.page)
        assertEquals(50, data?.pageSize)
    }
}
```

---

## 🖥️ UI TESTS

### 1. Navigation Tests

```kotlin
@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun navigationToAnalyzeScreenWorks() {
        // Start at home
        composeTestRule.setContent {
            PoseLensTheme {
                PoseLensNavigation()
            }
        }
        
        // Click analyze button (when implemented)
        composeTestRule.onNodeWithText("Analyze Image").performClick()
        
        // Verify navigation
        composeTestRule.onNodeWithText("Analysis Results").assertExists()
    }
}
```

### 2. Screen Tests

```kotlin
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreenDisplaysTitle() {
        composeTestRule.setContent {
            PoseLensTheme {
                HomeScreen(navController = rememberNavController())
            }
        }
        
        composeTestRule.onNodeWithText("PoseLens").assertExists()
    }
    
    @Test
    fun quickActionCardsAreDisplayed() {
        composeTestRule.setContent {
            PoseLensTheme {
                HomeScreen(navController = rememberNavController())
            }
        }
        
        composeTestRule.onNodeWithText("Analyze Photo").assertExists()
        composeTestRule.onNodeWithText("Browse Templates").assertExists()
    }
}
```

---

## ✅ MANUAL TESTING CHECKLIST

### Pre-Release Testing

#### 1. Image Analysis Flow
- [ ] Select image from gallery
- [ ] Camera capture (when implemented)
- [ ] Scene type detection accurate
- [ ] Lighting condition correct
- [ ] Composition analysis displayed
- [ ] Suggestions are relevant
- [ ] Loading states display correctly
- [ ] Error handling works (no image, network failure)

#### 2. Pose Detection Flow
- [ ] Upload image with person
- [ ] 33 landmarks detected
- [ ] Pose confidence displayed
- [ ] Template matching works
- [ ] Similarity scores calculated
- [ ] Real-time tracking (when implemented)
- [ ] Multiple people handling

#### 3. Template Management
- [ ] Browse all templates
- [ ] Filter by category
- [ ] Search templates
- [ ] Favorite/unfavorite
- [ ] Usage tracking increments
- [ ] Recent templates displayed
- [ ] Sync from server works

#### 4. Image Editing
- [ ] Apply brightness adjustment
- [ ] Apply contrast adjustment
- [ ] Apply all 12 adjustments
- [ ] Use default presets
- [ ] Save custom preset
- [ ] Preview before save
- [ ] Save to gallery
- [ ] Share edited image

#### 5. Permissions
- [ ] Camera permission request
- [ ] Storage permission (Android <13)
- [ ] Media permission (Android 13+)
- [ ] Permission denial handling
- [ ] Settings deep link works

#### 6. Settings & Preferences
- [ ] Toggle theme mode
- [ ] Save image quality setting
- [ ] Toggle auto-save
- [ ] Toggle analytics
- [ ] Clear preferences
- [ ] Onboarding completed flag

#### 7. Performance
- [ ] App launches in <2s
- [ ] Image analysis completes in <5s
- [ ] Pose detection in <3s
- [ ] No memory leaks (use LeakCanary)
- [ ] Smooth scrolling (60fps)
- [ ] Background operations don't block UI

#### 8. Edge Cases
- [ ] No internet connection
- [ ] Very large images (>10MB)
- [ ] Corrupted image files
- [ ] Empty database
- [ ] API timeout
- [ ] Low storage space
- [ ] Low memory device

#### 9. Device Compatibility
- [ ] Android 7.0 (API 24)
- [ ] Android 10 (API 29)
- [ ] Android 11 (API 30)
- [ ] Android 12 (API 31) - dynamic colors
- [ ] Android 13 (API 33) - media permissions
- [ ] Android 14 (API 34)
- [ ] Small screens (480x800)
- [ ] Large screens (1080x1920)
- [ ] Tablets (landscape)

---

## 📦 TEST DATA & FIXTURES

### Sample Test Images
```
test/resources/images/
├── nature_bright.jpg       # Clear nature scene, bright lighting
├── beach_sunset.jpg        # Sunset at beach, warm tones
├── urban_night.jpg         # City night scene, low light
├── portrait_indoor.jpg     # Person indoors, neutral lighting
├── yoga_standing.jpg       # Person in yoga pose, clear landmarks
├── multiple_people.jpg     # Multiple people, test filtering
├── blurry_image.jpg        # Low quality, test error handling
└── corrupted.jpg           # Corrupted file, test exception handling
```

### Mock Data
```kotlin
// test/java/com/example/poselens/TestFixtures.kt
object TestFixtures {
    
    fun createMockPoseResult(confidence: Float = 0.9f): PoseResult {
        return PoseResult(
            landmarks = mapOf(
                "nose" to Pair(960f, 540f),
                "left_eye" to Pair(920f, 500f),
                "right_eye" to Pair(1000f, 500f),
                "left_shoulder" to Pair(850f, 700f),
                "right_shoulder" to Pair(1070f, 700f)
                // Add all 33 landmarks
            ),
            confidence = confidence,
            detectedAt = System.currentTimeMillis(),
            imageWidth = 1920,
            imageHeight = 1080
        )
    }
    
    fun createMockSceneResult(sceneType: SceneType = SceneType.NATURE): SceneAnalysisResult {
        return SceneAnalysisResult(
            sceneType = sceneType,
            confidence = 0.95f,
            lightingCondition = LightingCondition.BRIGHT to 0.9f,
            dominantColors = listOf("#00FF00", "#0000FF"),
            detectedObjects = listOf(
                DetectedObject("Tree", 0.9f, BoundingBox(100, 100, 300, 400))
            ),
            composition = CompositionAnalysis(
                subjectPosition = SubjectPosition.CENTER,
                ruleOfThirdsAlignment = 0.8f,
                balanceScore = 0.7f,
                depthScore = 0.6f
            )
        )
    }
    
    fun createMockTemplate(id: String = "test-1"): PoseTemplate {
        return PoseTemplate(
            id = id,
            name = "Standing Pose",
            description = "Basic standing position",
            category = PoseCategory.BASIC,
            difficulty = PoseDifficulty.EASY,
            thumbnailUrl = "http://example.com/thumb.jpg",
            imageUrl = "http://example.com/image.jpg",
            landmarks = mapOf("nose" to Pair(0.5f, 0.5f)),
            tags = listOf("standing", "basic")
        )
    }
}
```

---

## 🚀 CI/CD INTEGRATION

### GitHub Actions Workflow
```yaml
# .github/workflows/test.yml
name: Run Tests

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  unit-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Grant execute permission for gradlew
        run: chmod +x gradlew
      
      - name: Run unit tests
        run: ./gradlew test --stacktrace
      
      - name: Generate test report
        uses: dorny/test-reporter@v1
        if: always()
        with:
          name: Unit Test Results
          path: '**/build/test-results/test/TEST-*.xml'
          reporter: java-junit
      
      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          files: ./app/build/reports/jacoco/test/jacocoTestReport.xml
  
  instrumented-tests:
    runs-on: macos-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Run instrumented tests
        uses: reactivecircus/android-emulator-runner@v2
        with:
          api-level: 29
          script: ./gradlew connectedAndroidTest
      
      - name: Upload test results
        uses: actions/upload-artifact@v3
        if: always()
        with:
          name: instrumentation-test-results
          path: '**/build/reports/androidTests/'
```

### Test Coverage Report
```kotlin
// app/build.gradle.kts
android {
    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }
}

tasks.register("jacocoTestReport", JacocoReport::class) {
    dependsOn("testDebugUnitTest")
    
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    
    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*"
    )
    
    sourceDirectories.setFrom(files("src/main/java"))
    classDirectories.setFrom(files(
        fileTree("build/intermediates/classes/debug") { exclude(fileFilter) }
    ))
    executionData.setFrom(fileTree(buildDir) {
        include("**/*.exec", "**/*.ec")
    })
}
```

---

## 📝 TEST EXECUTION

### Run All Tests
```bash
# Unit tests only
./gradlew test

# Instrumented tests only (requires emulator/device)
./gradlew connectedAndroidTest

# All tests
./gradlew test connectedAndroidTest

# With coverage
./gradlew jacocoTestReport
```

### Run Specific Test Class
```bash
./gradlew test --tests "AnalyzeImageUseCaseTest"
```

### Run Tests in Watch Mode
```bash
./gradlew test --continuous
```

---

## 🎯 TESTING BEST PRACTICES

### 1. Test Naming Convention
```
fun `[unit under test] [scenario] [expected result]`()

Examples:
fun `analyzeImage returns success when valid uri`()
fun `detectPose throws exception when bitmap is null`()
fun `calculateSimilarity returns 1_0 for identical poses`()
```

### 2. Test Structure (AAA Pattern)
```kotlin
@Test
fun testExample() {
    // Arrange (Given)
    val input = createTestInput()
    val expected = createExpectedOutput()
    
    // Act (When)
    val actual = systemUnderTest.execute(input)
    
    // Assert (Then)
    assertEquals(expected, actual)
}
```

### 3. Mock External Dependencies
```kotlin
// ✅ DO: Mock external dependencies
@Test
fun testWithMocks() {
    val mockRepository = mockk<ImageRepository>()
    coEvery { mockRepository.analyzeImage(any()) } returns flowOf(Result.Success(mockData))
    val useCase = AnalyzeImageUseCase(mockRepository, testDispatcher)
    // ...
}

// ❌ DON'T: Test with real implementations
@Test
fun testWithRealDependencies() {
    val repository = ImageRepositoryImpl(realContext, realAnalyzer, realApi, realDispatcher)
    // This is an integration test, not a unit test
}
```

### 4. Use Test Coroutines
```kotlin
@ExperimentalCoroutinesTest
class MyTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    
    @Test
    fun testCoroutine() = runTest {
        // Test code with coroutines
    }
}
```

### 5. Clean Up Resources
```kotlin
@After
fun tearDown() {
    bitmap.recycle()
    clearAllMocks()
    database.close()
}
```

---

## 📊 CURRENT TEST STATUS

```
✅ Test Strategy Defined
⚠️ Unit Tests: 0% implemented (TODO)
⚠️ Integration Tests: 0% implemented (TODO)
⚠️ UI Tests: 0% implemented (TODO)
⚠️ Manual Testing: Not started

Next Steps:
1. Implement use case unit tests (HIGH PRIORITY)
2. Add repository integration tests
3. Create UI tests for main flows
4. Set up CI/CD pipeline
5. Achieve 80%+ code coverage
```

---

**Document Version:** 1.0.0  
**Last Updated:** October 14, 2025  
**Next Review:** After test implementation

