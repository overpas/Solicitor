package by.overpass.android.solicitor.sample

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import org.hamcrest.Matchers.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    private lateinit var uiDevice: UiDevice

    @Before
    fun setUp() {
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        uiDevice.waitForIdle(6000)
    }

    @Test
    fun testAllPermissionsGranted() {
        uiDevice.findObject(By.res(Constants.MY_PKG, "requestPermissions")).click()

        uiDevice.findObject(
            UiSelector()
                .packageName("com.google.android.permissioncontroller")
                .childSelector(
                    UiSelector()
                        .className("android.widget.Button")
                        .resourceId("com.android.permissioncontroller:id/permission_allow_foreground_only_button")
                )
        ).click()

        uiDevice.findObject(
            UiSelector()
                .packageName("com.google.android.permissioncontroller")
                .childSelector(
                    UiSelector()
                        .className("android.widget.Button")
                        .resourceId("com.android.permissioncontroller:id/permission_allow_button")
                )
        ).click()

        onView(withId(R.id.message)).check(matches(withText(containsString("GRANTED"))))
    }

    object Constants {
        const val MY_PKG = "by.overpass.android.solicitor.sample"
    }
}