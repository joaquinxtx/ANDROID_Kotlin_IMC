package com.example.cursoaris

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.slider.RangeSlider
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.DecimalFormat
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MainActivity : AppCompatActivity() {



    private var isMaleSelected:Boolean=true
    private var isFemaleSelected:Boolean=false
    private var currentWeight:Int=70
    private var currentAge:Int=12
    private var currentHeight:Int=120

    private lateinit var viewMale:CardView
    private lateinit var viewFemale:CardView
    private lateinit var tvHeight:TextView
    private lateinit var rsHeight:RangeSlider
    private lateinit var btnSubtractWeight:FloatingActionButton
    private lateinit var btnPlusWeight:FloatingActionButton
    private lateinit var tvWight:TextView
    private lateinit var btnSubtractAge:FloatingActionButton
    private lateinit var btnPlusAge:FloatingActionButton
    private lateinit var tvAge:TextView
    private lateinit var btnCalculate:Button

    private lateinit var switchDarkMode: SwitchMaterial

    private var firstTime: Boolean = true

    companion object{
        const val IMC_KEY = "IMC_RESULT"
        const val KEY_DARK_MODE = "key_dark_mode"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
        initListeners()
        CoroutineScope(Dispatchers.IO).launch {
            getSettings().filter { firstTime }.collect { settingsModel ->
                if (settingsModel != null) {
                    runOnUiThread {
                        switchDarkMode.isChecked = settingsModel.darkMode
                        firstTime = !firstTime
                    }
                }

            }

        }
        initUI()

    }



    private fun initComponents() {
        viewMale = findViewById(R.id.viewMale)
        viewFemale = findViewById(R.id.viewFemale)
        tvHeight=findViewById(R.id.tvHeight)
        rsHeight=findViewById(R.id.rsHeight)
        btnSubtractWeight=findViewById(R.id.btnSubtractWeight)
        btnPlusWeight=findViewById(R.id.btnPlusWeight)
        tvWight=findViewById(R.id.tvWeight)
        btnSubtractAge=findViewById(R.id.btnSubtractAge)
        btnPlusAge=findViewById(R.id.btnPlusAge)
        tvAge=findViewById(R.id.tvAge)
        btnCalculate=findViewById(R.id.btnCalculate)
        switchDarkMode = findViewById(R.id.switchDarkMode)
    }
    private fun initListeners() {
        viewMale.setOnClickListener{
            changeGender()
            setGenderColor()
        }
        viewFemale.setOnClickListener{
            changeGender()
            setGenderColor()
        }

        rsHeight.addOnChangeListener { _, value, _ ->

            val df =DecimalFormat("#.##")
            currentHeight = df.format(value).toInt()
            tvHeight.text="$currentHeight cm"
        }
        btnPlusWeight.setOnClickListener{
            currentWeight += 1
            setWeight()
        }
        btnSubtractWeight.setOnClickListener{
            currentWeight -= 1
            setWeight()
        }
        btnPlusAge.setOnClickListener{
            currentAge += 1
            setAge()
        }
        btnSubtractAge.setOnClickListener{
            currentAge -= 1
            setAge()
        }

        btnCalculate.setOnClickListener{
           val result=calculateIMC()
            navigateToResult(result)
        }
    }

    private fun navigateToResult(result:Double) {
        val intent = Intent(this,ResultImcActivity::class.java)
        intent.putExtra(IMC_KEY,result)
        startActivity(intent)
    }

    private fun calculateIMC():Double {
        val df = DecimalFormat("#.##")
        val imc = currentWeight/(currentHeight.toDouble()/100*currentHeight.toDouble()/100)
        return df.format(imc).toDouble()

    }

    private fun setAge() {
        tvAge.text=currentAge.toString()
    }

    private fun setWeight() {
        tvWight.text=currentWeight.toString()
    }

    private fun changeGender(){
        isMaleSelected=!isMaleSelected
        isFemaleSelected=!isFemaleSelected
    }

    private fun setGenderColor(){

        viewMale.setBackgroundColor(getSelectBackgroundColor(isMaleSelected))
        viewFemale.setBackgroundColor(getSelectBackgroundColor(isFemaleSelected))
    }

    private fun getSelectBackgroundColor(isSelectedComponents:Boolean):Int{
        val colorReference = if(isSelectedComponents){
             R.color.background_component_selected
        }else{
             R.color.background_component
        }

        return ContextCompat.getColor(this,colorReference)
    }
    private fun initUI() {
        setGenderColor()
        setWeight()
        setAge()
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableDarkMode()
            } else {
                disableDarkMode()
            }
            CoroutineScope(Dispatchers.IO).launch {
                saveOptions(KEY_DARK_MODE, isChecked)

            }
        }
    }
    private suspend fun saveOptions(key: String, value: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(key)] = value
        }
    }

    private fun getSettings(): Flow<SettingsModel> {
        return dataStore.data.map { preferences ->
            SettingsModel(
                darkMode = preferences[booleanPreferencesKey(KEY_DARK_MODE)] ?: true
            )

        }
    }

    private fun disableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        delegate.applyDayNight()
    }

    private fun enableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        delegate.applyDayNight()
    }
}
