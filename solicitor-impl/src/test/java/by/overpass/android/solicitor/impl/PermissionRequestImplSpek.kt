package by.overpass.android.solicitor.impl

import by.overpass.android.solicitor.core.PermissionFramework
import by.overpass.android.solicitor.core.PermissionStatus
import by.overpass.android.solicitor.core.Permissions
import com.nhaarman.mockitokotlin2.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class PermissionRequestImplSpek : Spek({

    describe("When a permission request result is") {

        val permissions = arrayOf("perm1")
        val results = intArrayOf(1)
        val theRequestCode = 1

        val mockPermissionFramework by memoized { mock<PermissionFramework>() }
        val mockGrantedCallback by memoized { mock<(Permissions) -> Unit>() }
        val mockDeniedCallback by memoized { mock<(Permissions) -> Unit>() }
        val mockDeniedPermanentlyCallback by memoized { mock<(Permissions) -> Unit>() }
        val request by memoized {
            PermissionRequestFragment.create(
                {
                    PermissionRequestFragment()
                },
                {
                    onGranted = mockGrantedCallback
                    onDenied = mockDeniedCallback
                    onDeniedPermanently = mockDeniedPermanentlyCallback
                }
            ).apply {
                permissionFramework = mockPermissionFramework
                requestCode = theRequestCode
            }
        }

        describe("GRANTED") {

            it("onGranted callback must be triggered") {
                whenever(mockPermissionFramework.status(permissions, results))
                    .thenReturn(PermissionStatus(
                        granted = permissions.asList(),
                        denied = emptyList(),
                        needRationale = emptyList()
                    ))

                request.onRequestPermissionsResult(
                    theRequestCode,
                    permissions,
                    results
                )

                verify(mockGrantedCallback).invoke(argForWhich { this[0] == "perm1" })
            }
        }

        describe("DENIED") {

            it("onDenied callback must be triggered") {
                whenever(mockPermissionFramework.status(permissions, results))
                    .thenReturn(PermissionStatus(
                        granted = emptyList(),
                        denied = permissions.asList(),
                        needRationale = permissions.asList()
                    ))

                request.onRequestPermissionsResult(
                    theRequestCode,
                    permissions,
                    results
                )

                verify(mockDeniedCallback).invoke(argForWhich { this[0] == "perm1" })
            }
        }

        describe("DENIED PERMANENTLY") {

            it("onDeniedPermanently callback must be triggered") {
                whenever(mockPermissionFramework.status(permissions, results))
                    .thenReturn(PermissionStatus(
                        granted = emptyList(),
                        denied = permissions.asList(),
                        needRationale = emptyList()
                    ))

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
})