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
    fun testAllGranted() {
        uiDevice.findObject(By.res(Constants.PKG_MY_APP, Constants.RES_BTN_REQUEST_PERMISSIONS))
            .click()

        uiDevice.findObject(
            UiSelector()
                .packageName(Constants.PKG_PERMISSION_CONTROLLER)
                .childSelector(
                    UiSelector()
                        .className(Constants.CLASS_BTN)
                        .resourceId(Constants.RES_BTN_ALLOW)
                )
        ).click()

        uiDevice.findObject(
            UiSelector()
                .packageName(Constants.PKG_PERMISSION_CONTROLLER)
                .childSelector(
                    UiSelector()
                        .className(Constants.CLASS_BTN)
                        .resourceId(Constants.RES_BTN_ALLOW)
                )
        ).click()

        onView(withId(R.id.message)).check(matches(withText(containsString("GRANTED"))))
    }

    @Test
    fun testAllDenied() {
        uiDevice.findObject(By.res(Constants.PKG_MY_APP, Constants.RES_BTN_REQUEST_PERMISSIONS))
            .click()

        uiDevice.findObject(
            UiSelector()
                .packageName(Constants.PKG_PERMISSION_CONTROLLER)
                .childSelector(
                    UiSelector()
                        .className(Constants.CLASS_BTN)
                        .resourceId(Constants.RES_BTN_DENY)
                )
        ).click()

        uiDevice.findObject(
            UiSelector()
                .packageName(Constants.PKG_PERMISSION_CONTROLLER)
                .childSelector(
                    UiSelector()
                        .className(Constants.CLASS_BTN)
                        .resourceId(Constants.RES_BTN_DENY)
                )
        ).click()

        onView(withId(R.id.message)).check(matches(withText(containsString("DENIED"))))
    }

    object Constants {

        const val PKG_MY_APP = "by.overpass.android.solicitor.sample"
        const val PKG_PERMISSION_CONTROLLER = "com.google.android.permissioncontroller"
        const val PKG_SETTINGS = "com.android.settings"

        const val CLASS_BTN = "android.widget.Button"
        const val CLASS_TV = "android.widget.TextView"
        const val CLASS_RV = "androidx.recyclerview.widget.RecyclerView"
        const val CLASS_RG = "android.widget.RadioGroup"
        const val CLASS_RB = "android.widget.RadioButton"

        const val RES_BTN_ALLOW = "com.android.permissioncontroller:id/permission_allow_button"
        const val RES_BTN_ALLOW_FG_ONLY =
            "com.android.permissioncontroller:id/permission_allow_foreground_only_button"
        const val RES_BTN_REQUEST_PERMISSIONS = "requestPermissions"
        const val RES_BTN_DENY = "com.android.permissioncontroller:id/permission_deny_button"
        const val RES_BTN_DENY_DONT_ASK =
            "com.android.permissioncontroller:id/permission_deny_and_dont_ask_again_button"
        const val RES_TV_ALERT_DIALOG_TITLE = "by.overpass.android.solicitor.sample:id/alertTitle"
        const val RES_BTN_1_ALERT_DIALOG = "android:id/button1"
        const val RES_RV_SETTINGS = "com.android.settings:id/recycler_view"
        const val RES_RV_PERMISSION_CONTROLLER = "com.android.permissioncontroller:id/recycler_view"
        const val RES_TV_RV_ITEM_TITLE = "android:id/title"
        const val RES_RG_PERMISSION = "com.android.permissioncontroller:id/radiogroup"
        const val RES_RB_DENY = "com.android.permissioncontroller:id/deny_radio_button"
        const val RES_RB_ALLOW = "com.android.permissioncontroller:id/allow_radio_button"
        const val RES_BTN_NO = "android:id/button2"
        const val RES_BTN_YES = "android:id/button1"

        const val TEXT_PERMISSIONS = "Permissions"
        const val TEXT_SMS = "SMS"
        const val TEXT_STORAGE = "Storage"
    }
}
