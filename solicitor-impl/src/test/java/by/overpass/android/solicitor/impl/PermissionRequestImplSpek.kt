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
                whenever(mockPermissionFramework.parseStatus(permissions, results))
                    .thenReturn(
                        PermissionStatus(
                            granted = permissions.asList(),
                            denied = emptyList(),
                            needRationale = emptyList()
                        )
                    )
            }

            it("onGranted callback must be triggered") {
                request.onRequestPermissionsResult(
                    theRequestCode,
                    permissions,
                    results
                )

                verify(mockGrantedCallback).invoke(argForWhich { this[0] == "perm1" })
            }
        }

        describe("DENIED") {

            beforeEachTest {
                whenever(mockPermissionFramework.parseStatus(permissions, results))
                    .thenReturn(
                        PermissionStatus(
                            granted = emptyList(),
                            denied = permissions.asList(),
                            needRationale = permissions.asList()
                        )
                    )
            }

            it("onDenied callback must be triggered") {
                request.onRequestPermissionsResult(
                    theRequestCode,
                    permissions,
                    results
                )

                verify(mockDeniedCallback).invoke(argForWhich { this[0] == "perm1" })
            }
        }

        describe("DENIED PERMANENTLY") {

            beforeEachTest {
                whenever(mockPermissionFramework.parseStatus(permissions, results))
                    .thenReturn(
                        PermissionStatus(
                            granted = emptyList(),
                            denied = permissions.asList(),
                            needRationale = emptyList()
                        )
                    )
            }

            it("onDeniedPermanently callback must be triggered") {
                request.onRequestPermissionsResult(
                    theRequestCode,
                    permissions,
                    results
                )

                verify(mockDeniedPermanentlyCallback).invoke(argForWhich { this[0] == "perm1" })
            }
        }

        describe("with other request code") {

            it("callbacks mustn't be triggered") {
                request.onRequestPermissionsResult(
                    2,
                    permissions,
                    results
                )

                verifyZeroInteractions(mockGrantedCallback)
                verifyZeroInteractions(mockDeniedCallback)
                verifyZeroInteractions(mockDeniedPermanentlyCallback)
            }
        }

        describe("with inconsistent permissions and results") {

            it("callbacks mustn't be triggered") {
                request.onRequestPermissionsResult(
                    theRequestCode,
                    permissions,
                    intArrayOf()
                )

                verifyZeroInteractions(mockGrantedCallback)
                verifyZeroInteractions(mockDeniedCallback)
                verifyZeroInteractions(mockDeniedPermanentlyCallback)
            }
        }
    }

    describe("When requesting permissions") {

        val requestCode = 2

        describe("with request code") {

            val requestCodeSetDescription = "request code must be set,"

            describe("if all permissions already granted") {

                beforeEachTest {
                    whenever(mockPermissionFramework.allGranted(permissions)).thenReturn(true)
                }

                it("$requestCodeSetDescription onGranted callback must be triggered") {
                    request.request(requestCode, permissions)

                    verify(mockGrantedCallback).invoke(argForWhich { this[0] == "perm1" })
                    assertEquals(requestCode, request.requestCode)
                }
            }

            describe("if permissions are not granted, no permissions need rationale and some are denied") {

                beforeEachTest {
                    whenever(mockPermissionFramework.allGranted(permissions)).thenReturn(false)
                    whenever(mockPermissionFramework.checkStatus(permissions))
                        .thenReturn(
                            PermissionStatus(
                                emptyList(),
                                permissions.asList(),
                                emptyList()
                            )
                        )
                }

                it("$requestCodeSetDescription permissions must be requested") {
                    request.request(requestCode, permissions)

                    verify(mockPermissionFramework).requestPermissions(permissions, requestCode)
                    assertEquals(requestCode, request.requestCode)
                }
            }

            describe("if permissions are not granted, some permissions need rationale") {

                beforeEachTest {
                    whenever(mockPermissionFramework.allGranted(permissions)).thenReturn(false)
                    whenever(mockPermissionFramework.checkStatus(permissions))
                        .thenReturn(
                            PermissionStatus(
                                emptyList(),
                                emptyList(),
                                permissions.asList()
                            )
                        )
                }

                it("$requestCodeSetDescription onShouldShowRationaleCallback must be" +
                        " triggered and showedRationale must be set to true") {
                    request.request(requestCode, permissions)

                    verify(mockRationaleCallback).invoke(argThat { this[0] == "perm1" }, any())
                    assertTrue(request.showedRationale)
                    assertEquals(requestCode, request.requestCode)
                }
            }
        }
    }
})