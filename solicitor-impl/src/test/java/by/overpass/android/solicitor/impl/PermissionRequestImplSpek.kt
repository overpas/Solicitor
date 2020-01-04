package by.overpass.android.solicitor.impl

import by.overpass.android.solicitor.core.PermissionFramework
import by.overpass.android.solicitor.core.PermissionStatus
import by.overpass.android.solicitor.core.Permissions
import com.nhaarman.mockitokotlin2.argForWhich
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
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

            it("onGranted callback should be triggered") {
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

            it("onDenied callback should be triggered") {
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

            it("onDeniedPermanently callback should be triggered") {
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
    }
})