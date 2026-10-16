package com.example.applock

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var apps: List<Pair<String, String>> // label, package

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        val pm = packageManager
        val launch = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        apps = pm.queryIntentActivities(launch, PackageManager.MATCH_ALL)
            .map { it.loadLabel(pm).toString() to it.activityInfo.packageName }
            .filter { it.second != packageName }
            .distinctBy { it.second }
            .sortedBy { it.first.lowercase() }

        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val btn = Button(this).apply {
            text = "1) Enable AppLock service (Accessibility)"
            setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        }
        val hint = TextView(this).apply {
            text = "2) Tick the apps to protect:"
            setPadding(32, 16, 32, 8)
        }
        val list = ListView(this).apply {
            choiceMode = ListView.CHOICE_MODE_MULTIPLE
            adapter = ArrayAdapter(
                this@MainActivity, android.R.layout.simple_list_item_multiple_choice,
                apps.map { it.first }
            )
            val saved = Prefs.locked(this@MainActivity)
            apps.forEachIndexed { i, a -> setItemChecked(i, a.second in saved) }
            setOnItemClickListener { _, _, _, _ ->
                val sel = apps.indices.filter { isItemChecked(it) }.map { apps[it].second }.toSet()
                Prefs.setLocked(this@MainActivity, sel)
            }
        }
        root.addView(btn, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        root.addView(hint)
        root.addView(list, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setContentView(root)
    }
}
