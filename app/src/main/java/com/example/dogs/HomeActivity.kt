package com.example.dogs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val explorarFragment = ExplorarFragment()
    private val solicitudesFragment = SolicitudesFragment()
    private val trabajosFragment = TrabajosFragment()
    private val perfilFragment = PerfilFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Configurar el título y el ícono en la ActionBar
        supportActionBar?.apply {
            // Mostrar el ícono del logo a la izquierda
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_action_name)
            // Configurar el nombre de la aplicación en el centro
            title = getString(R.string.app_name)
        }

        // Agregar un ícono de configuración a la derecha de la ActionBar
        //supportActionBar?.setDisplayShowCustomEnabled(true)
        //supportActionBar?.setCustomView(R.layout.action_bar_settings_icon)

        val navigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        // Cargar el primer fragmento al iniciar la actividad
        loadFragment(homeFragment)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.homeFragment -> {
                loadFragment(homeFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.explorarFragment -> {
                loadFragment(explorarFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.solicitudesFragment -> {
                loadFragment(solicitudesFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.trabajosFragment -> {
                loadFragment(trabajosFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.perfilFragment -> {
                loadFragment(perfilFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.commit()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_navigation, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Acción cuando se hace clic en el ícono del logo en la ActionBar
                // Puedes realizar una acción específica aquí
                openLogoIconAction()
                return true
            }
            R.id.action_settings_menu -> {
                // Acción cuando se selecciona la opción del ícono de configuraciones desde el menú
                openSettings()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun openLogoIconAction() {
        // Ejemplo: Realizar una acción específica para el ícono del logo
        // Puedes abrir una nueva actividad, mostrar un diálogo, etc.
    }

    private fun openSettings() {
        // Ejemplo: Abrir una nueva actividad de configuraciones
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

}
