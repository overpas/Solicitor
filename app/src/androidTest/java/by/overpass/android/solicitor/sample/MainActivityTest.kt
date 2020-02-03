package by.overpass.android.solicitor.sample

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import by.overpass.android.solicitor.core.PermissionFramework
import by.overpass.android.solicitor.core.PermissionStatus
import by.overpass.android.solicitor.impl.Solicitor
import org.hamcrest.Matchers.containsString
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations.initMocks

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Mock
    private lateinit var mockPermissionFramework: PermissionFramework

    private lateinit var uiDevice: UiDevice

    @Before
    fun setUp() {
        initMocks(this)
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        uiDevice.waitForIdle(6000)
    }

    @Test
    fun testAllGranted() {
        onView(withId(R.id.requestPermissions)).perform(click())

        repeat(2) {
            uiDevice.findObject(
                UiSelector()
                    .packageName(Constants.PKG_PERMISSION_CONTROLLER)
                    .childSelector(
                        UiSelector()
                            .className(Constants.CLASS_BTN)
                            .resourceId(Constants.RES_BTN_ALLOW)
                    )
            ).click()
        }

        onView(withId(R.id.message)).check(matches(withText(containsString("GRANTED"))))
    }

    @Test
    fun testAllDenied() {
        onView(withId(R.id.requestPermissions)).perform(click())

        repeat(2) {
            uiDevice.findObject(
                UiSelector()
                    .packageName(Constants.PKG_PERMISSION_CONTROLLER)
                    .childSelector(
                        UiSelector()
                            .className(Constants.CLASS_BTN)
                            .resourceId(Constants.RES_BTN_DENY)
                    )
            ).click()
        }

        onView(withId(R.id.message)).check(matches(withText(containsString("DENIED"))))
    }

    @Test
    fun testOneGrantedOneDenied() {
        onView(withId(R.id.requestPermissions)).perform(click())

        arrayOf(Constants.RES_BTN_ALLOW, Constants.RES_BTN_DENY).forEach {
            uiDevice.findObject(
                UiSelector()
                    .packageName(Constants.PKG_PERMISSION_CONTROLLER)
                    .childSelector(
                        UiSelector()
                            .className(Constants.CLASS_BTN)
                            .resourceId(it)
                    )
            ).click()
        }

        onView(withId(R.id.message)).check(matches(withText(containsString("DENIED"))))
    }

    @Test
    fun testAllAlreadyGranted() {
        `when`(mockPermissionFramework.allGranted(any())).thenReturn(true)
        Solicitor.permissionFrameworkFactory = { mockPermissionFramework }

        onView(withId(R.id.requestPermissions)).perform(click())

        onView(withId(R.id.message)).check(matches(withText(containsString("GRANTED"))))
        verify(mockPermissionFramework).allGranted(any())
    }

    @Test
    fun testRationaleNeeded() {
        `when`(mockPermissionFramework.allGranted(any())).thenReturn(false)
        `when`(mockPermissionFramework.checkStatus(any())).thenReturn(
            PermissionStatus(
                emptyList(),
                emptyList(),
                listOf(Constants.TEST_PERMISSION)
            )
        )
        Solicitor.permissionFrameworkFactory = { mockPermissionFramework }

        onView(withId(R.id.requestPermissions)).perform(click())

        assertTrue(
            uiDevice.findObject(
                UiSelector()
                    .packageName(Constants.PKG_MY_APP)
                    .childSelector(
                        UiSelector()
                            .className(Constants.CLASS_TV)
                            .resourceId(Constants.RES_TV_ALERT_DIALOG_TITLE)
                    )
            ).exists()
        )
        val message = uiDevice.findObject(
            UiSelector()
                .packageName(Constants.PKG_MY_APP)
                .childSelector(
                    UiSelector()
                        .className(Constants.CLASS_TV)
                        .resourceId(Constants.RES_TV_MESSAGE)
                )
        ).text
        assertThat(message, containsString(Constants.TEST_PERMISSION))
    }

    @Test
    fun testRationaleDismissed() {
        `when`(mockPermissionFramework.allGranted(any())).thenReturn(false)
        `when`(mockPermissionFramework.checkStatus(any())).thenReturn(
            PermissionStatus(
                emptyList(),
                emptyList(),
                listOf(Constants.TEST_PERMISSION)
            )
        )
        Solicitor.permissionFrameworkFactory = { mockPermissionFramework }

        onView(withId(R.id.requestPermissions)).perform(click())

        uiDevice.findObject(
            UiSelector()
                .packageName(Constants.PKG_MY_APP)
                .childSelector(
                    UiSelector()
                        .className(Constants.CLASS_BTN)
                        .resourceId(Constants.RES_BTN_NO)
                )
        ).click()

        onView(withId(R.id.message)).check(matches(withText(R.string.rationale_dismissed)))
    }

    @Test
    fun testDeniedPermanently() {
        onView(withId(R.id.requestPermissions)).perform(click())

        repeat(2) {
            uiDevice.findObject(
                UiSelector()
                    .packageName(Constants.PKG_PERMISSION_CONTROLLER)
                    .childSelector(
                        UiSelector()
                            .className(Constants.CLASS_BTN)
                            .resourceId(Constants.RES_BTN_DENY)
                    )
            ).click()
        }

        onView(withId(R.id.message)).check(matches(withText(containsString("DENIED"))))

        onView(withId(R.id.requestPermissions)).perform(click())

        uiDevice.findObject(
            UiSelector()
                .packageName(Constants.PKG_MY_APP)
                .childSelector(
                    UiSelector()
                        .className(Constants.CLASS_BTN)
                        .resourceId(Constants.RES_BTN_YES)
                )
        ).click()

        repeat(2) {
            uiDevice.findObject(
                UiSelector()
                    .packageName(Constants.PKG_PERMISSION_CONTROLLER)
                    .childSelector(
                        UiSelector()
                            .className(Constants.CLASS_BTN)
                            .resourceId(Constants.RES_BTN_DENY_DONT_ASK)
                    )
            ).click()
        }

        assertEquals(
            "Permissions required",
            uiDevice.findObject(
                UiSelector()
                    .packageName(Constants.PKG_MY_APP)
                    .childSelector(
                        UiSelector()
                            .className(Constants.CLASS_TV)
                            .resourceId(Constants.RES_TV_ALERT_DIALOG_TITLE)
                    )
            ).text
        )

        uiDevice.setOrientationLeft()
    }

    object Constants {

        const val PKG_MY_APP = "by.overpass.android.solicitor.sample"
        const val PKG_PERMISSION_CONTROLLER = "com.google.android.permissioncontroller"

        const val CLASS_BTN = "android.widget.Button"
        const val CLASS_TV = "android.widget.TextView"

        const val RES_BTN_ALLOW = "com.android.permissioncontroller:id/permission_allow_button"
        const val RES_BTN_DENY = "com.android.permissioncontroller:id/permission_deny_button"
        const val RES_BTN_DENY_DONT_ASK =
            "com.android.permissioncontroller:id/permission_deny_and_dont_ask_again_button"
        const val RES_TV_ALERT_DIALOG_TITLE = "by.overpass.android.solicitor.sample:id/alertTitle"
        const val RES_BTN_NO = "android:id/button2"
        const val RES_BTN_YES = "android:id/button1"
        const val RES_TV_MESSAGE = "android:id/message"

        const val TEST_PERMISSION = "perm1"
    }
}
