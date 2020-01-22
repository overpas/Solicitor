package by.overpass.android.solicitor.impl

import by.overpass.android.solicitor.core.PermissionFramework
import by.overpass.android.solicitor.core.PermissionStatus
import by.overpass.android.solicitor.core.Permissions
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class PermissionRequestImplSpek : Spek({

    val permissions = arrayOf("perm1")
    val results = intArrayOf(1)
    val theRequestCode = 1

    val mockPermissionFramework by memoized { mock<PermissionFramework>() }
    val mockGrantedCallback by memoized { mock<(Permissions) -> Unit>() }
    val mockDeniedCallback by memoized { mock<(Permissions) -> Unit>() }
    val mockDeniedPermanentlyCallback by memoized { mock<(Permissions) -> Unit>() }
    val mockRationaleCallback by memoized { mock<(Permissions, () -> Unit) -> Unit>() }
    val request by memoized {
        PermissionRequestFragment.create(
            {
                PermissionRequestFragment()
            },
            {
                onGranted = mockGrantedCallback
                onDenied = mockDeniedCallback
                onDeniedPermanently = mockDeniedPermanentlyCallback
                onShouldShowRationale = mockRationaleCallback
            }
        ).apply {
            permissionFramework = mockPermissionFramework
            requestCode = theRequestCode
        }
    }

    describe("When a permission request result is") {

        describe("GRANTED") {

            beforeEachTest {
                whenever(mockPermissionFramework.status(permissions, results))
                    .thenReturn(
                        PermissionStatus(
                            granted = permissions.asList(),
                            denied = emptyList(),
                            needRationale = emptyList()
                        )
                    )

                request.onRequestPermissionsResult(
                    theRequestCode,
                    permissions,
                    results
                )
            }

            it("onGranted callback must be triggered") {
                verify(mockGrantedCallback).invoke(argForWhich { this[0] == "perm1" })
            }
        }

        describe("DENIED") {

            beforeEachTest {
                whenever(mockPermissionFramework.status(permissions, results))
                    .thenReturn(
                        PermissionStatus(
                            granted = emptyList(),
                            denied = permissions.asList(),
                            needRationale = permissions.asList()
                        )
                    )

                request.onRequestPermissionsResult(
                    theRequestCode,
                    permissions,
                    results
                )
            }

            it("onDenied callback must be triggered") {
                verify(mockDeniedCallback).invoke(argForWhich { this[0] == "perm1" })
            }
        }

        describe("DENIED PERMANENTLY") {

            beforeEachTest {
                whenever(mockPermissionFramework.status(permissions, results))
                    .thenReturn(
                        PermissionStatus(
                            granted = emptyList(),
                            denied = permissions.asList(),
                            needRationale = emptyList()
                        )
                    )

                request.onRequestPermissionsResult(
                    theRequestCode,
                    permissions,
                    results
                )
            }

            it("onDeniedPermanently callback must be triggered") {
                verify(mockDeniedPermanentlyCallback).invoke(argForWhich { this[0] == "perm1" })
            }
        }

        describe("with other request code") {

            beforeEachTest {
                request.onRequestPermissionsResult(
                    2,
                    permissions,
                    results
                )
            }

            it("callbacks mustn't be triggered") {
                verifyZeroInteractions(mockGrantedCallback)
                verifyZeroInteractions(mockDeniedCallback)
                verifyZeroInteractions(mockDeniedPermanentlyCallback)
            }
        }

        describe("with inconsistent permissions and results") {

            beforeEachTest {
                request.onRequestPermissionsResult(
                    theRequestCode,
                    permissions,
                    intArrayOf()
                )
            }

            it("callbacks mustn't be triggered") {
                verifyZeroInteractions(mockGrantedCallback)
                verifyZeroInteractions(mockDeniedCallback)
                verifyZeroInteractions(mockDeniedPermanentlyCallback)
            }
        }
    }

    describe("When requesting permissions") {

        val requestCode = 2

        describe("if all permissions already granted") {

            beforeEachTest {
                whenever(mockPermissionFramework.allGranted(permissions)).thenReturn(true)

                request.request(requestCode, permissions)
            }

            it("onGranted callback must be triggered") {
                verify(mockGrantedCallback).invoke(argForWhich { this[0] == "perm1" })
            }
            it("requestCode must be set") {
                assertEquals(requestCode, request.requestCode)
            }
        }

        describe("if permissions are not granted, no permissions need rationale and some are denied") {

            beforeEachTest {
                whenever(mockPermissionFramework.allGranted(permissions)).thenReturn(false)
                whenever(mockPermissionFramework.status(permissions))
                    .thenReturn(
                        PermissionStatus(
                            emptyList(),
                            permissions.asList(),
                            emptyList()
                        )
                    )

                request.request(requestCode, permissions)
            }

            it("permissions must be requested") {
                verify(mockPermissionFramework).requestPermissions(permissions, requestCode)
            }
            it("requestCode must be set") {
                assertEquals(requestCode, request.requestCode)
            }
        }

        describe("if permissions are not granted, some permissions need rationale") {

            beforeEachTest {
                whenever(mockPermissionFramework.allGranted(permissions)).thenReturn(false)
                whenever(mockPermissionFramework.status(permissions))
                    .thenReturn(
                        PermissionStatus(
                            emptyList(),
                            emptyList(),
                            permissions.asList()
                        )
                    )

                request.request(requestCode, permissions)
            }

            it("onShouldShowRationaleCallback must be triggered") {
                verify(mockRationaleCallback).invoke(argThat { this[0] == "perm1" }, any())
            }
            it("showedRationale property must be set to true") {
                assertTrue(request.showedRationale)
            }
            it("requestCode must be set") {
                assertEquals(requestCode, request.requestCode)
            }
        }
    }
})