package com.example.asuapp001


import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.navigation.NavOptions
import com.example.asuapp001.databinding.ActivityMainBinding
import com.example.asuapp001.utils.FirebaseHelper
import com.example.asuapp001.utils.SharedPreferencesHelper

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var MytextBar : TextView

    private lateinit var prefs: SharedPreferencesHelper

    // Добавляем FirebaseHelper
    private lateinit var firebaseHelper: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- Подключаем макет через View Binding ---
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- Инициализируем SharedPreferencesHelper ---
        // Передаём this (контекст активности)
        // getInstance — возвращает единственный экземпляр
        prefs = SharedPreferencesHelper.getInstance(this)

        // --- Устанавливаем Toolbar ---
        setSupportActionBar(binding.appBarMain.toolbar)

        // --- Настраиваем боковое меню ---
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Настройки панели: какие экраны — "верхние"
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.menu_dailyPlanner,
                R.id.menu_creators,
                R.id.menu_ad,
                R.id.menu_admin,
                R.id.menu_bopros,
                R.id.nav_settings
            ),
            drawerLayout
        )

        // Связываем: кнопка "назад/меню" работает правильно
        setupActionBarWithNavController(navController, appBarConfiguration)
        // Связываем: клик по пункту меню → переход
        navView.setupWithNavController(navController)

        // --- Находим TextView внутри пункта меню ---
        MytextBar = MenuItemCompat.getActionView(navView.menu.findItem(R.id.menu_ad)) as TextView
        MytextBar.gravity = Gravity.CENTER_VERTICAL  // выравнивание по вертикали
        // Загружаем текст из настроек
        MytextBar.text = prefs.getString("dataMainValueTTT", "") ?: ""

        // --- Настройки навигации ---
        val navOptions = NavOptions.Builder()
            .setLaunchSingleTop(false)  // разрешаем несколько копий экрана
            .build()

        // Обработчик клика по пункту меню
        navView.menu.findItem(R.id.menu_ad)?.setOnMenuItemClickListener {
            MytextBar.text = ""
            prefs.saveString("dataMainValueTTT", "")
            navController.popBackStack()
            navController.navigate(R.id.menu_ad, null, navOptions)
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Инициализация FirebaseHelper
        firebaseHelper = FirebaseHelper(prefs) {
            // Колбэк: пришло новое объявление
            runOnUiThread {
                MytextBar.text = "⁉️"
                prefs.saveString("dataMainValueTTT", "⁉️")
            }
        }
        firebaseHelper.startListening()
        updateAdMenuItem()
    }

    // --- Меню в Toolbar ---
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    // --- Кнопка "назад" в Toolbar ---
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun updateAdMenuItem() {
        val savedText = prefs.getString("dataMainValueTTT", "") ?: ""
        runOnUiThread {
            MytextBar.text = savedText
        }
    }

}