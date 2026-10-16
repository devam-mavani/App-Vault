package com.example.applock

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class LockService : AccessibilityService() {
    companion object { @Volatile var unlockedPkg: String? = null }

    override fun onAccessibilityEvent(e: AccessibilityEvent) {
        if (e.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = e.packageName?.toString() ?: return
        if (pkg == packageName || pkg == "com.android.systemui") return

        if (pkg in Prefs.locked(this)) {
            if (pkg != unlockedPkg) {
                startActivity(
                    Intent(this, LockActivity::class.java)
                        .putExtra("pkg", pkg)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        } else {
            unlockedPkg = null // user moved to another app: re-lock next time
        }
    }

    override fun onInterrupt() {}
}
