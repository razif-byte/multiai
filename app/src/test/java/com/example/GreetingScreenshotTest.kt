package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.AntigravityStats
import com.example.ui.components.AntigravityHud
import com.example.ui.components.WatermarkFooter
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme(darkTheme = true) {
        AntigravityHud(
          stats = AntigravityStats(
            latencySavedMs = 120,
            memoryFreedKb = 512,
            tokenEfficiencyPercent = 24,
            isZeroGActive = true,
            pipelineStatus = "Antigravity Core: Aktif & Stabil",
            activeNodes = 4,
            totalOptimizedRequests = 8
          )
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
