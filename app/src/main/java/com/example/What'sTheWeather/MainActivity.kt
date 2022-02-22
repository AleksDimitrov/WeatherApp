package com.example.testapp3
import android.os.AsyncTask
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

/*
File: MainActivity.kt
Author: Ishaan Ghosh & Aleks Dimitrov
Purpose: This is the logic behind the weather app. We have overriden the onCreate function to
         create the screen (activity) then call an async function to retrieve weather data through
         an API call, and format the data to appear on screen. The async weather API call is done
         using the Android AsyncTask structure (onPreExecute, etc..).
*/
class MainActivity : AppCompatActivity() {
    // Declaring default variables used in API call and data formatting
    var city = "tucson,us"
    val API: String = "f5979ed4f3b30e4e3dcc9c934eaeaa9f"
    var units = "imperial"
    var deg = "°F"
    var wind_units = "mph"

    /*
        This is where we initialize our activity (main app screen). We use it to set the
        base layout of the app using setContentView. WE then execute our async function
        to produce default results for our weather app. We also use this function to
        implement the code behind our city search and degree-switching buttons.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set display to main layout
        setContentView(R.layout.activity_main)
        // Default is light mode
        themeSwitch.isChecked = false
        // Call API and format results for screen
        weatherTask().execute()

        // Special feature - Polymorphism: converts a TextView to a Button
        // This is where the onclick logic for our search button goes.
        // It works by taking the string in the text view next to it and
        // doing an API call on the new string.
        val btn_click_me = findViewById<TextView>(R.id.goSearchBut)
        btn_click_me.setOnClickListener (object:View.OnClickListener {
            override fun onClick(p0: View?) {
                // Set city var to new city string and recall API
                city = findViewById<TextView>(R.id.cityString).text.toString()
                weatherTask().execute()
            }
        })

        // Special feature: Higher order function
        // This is where the onclick logic for our change degree button goes.
        // It works by conditionals, checking if the units are one way and
        // changing it to the other and vica versa.
        val changeDegBtn = findViewById<TextView>(R.id.changeDegBtn)
        changeDegBtn.setOnClickListener (object:View.OnClickListener {
            override fun onClick(p0: View?) {
                if (units == "metric") {
                    units = "imperial"
                    deg = "°F"
                    wind_units = "mph"
                } else if (units == "imperial") {
                    units = "metric"
                    deg = "°C"
                    wind_units = "m/s"
                }
                weatherTask().execute()
            }
        })

        // Declare the switch from the layout file
        val themeSwitch = findViewById<Switch>(R.id.themeSwitch)
        // Special feature: Lambda Function
        // set the switch to listen on checked change
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->

            // if the button is checked, i.e., towards the right or enabled
            // enable dark mode, change the text to disable dark mode
            // else keep the switch text to enable dark mode
            if (themeSwitch.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }


    }

    // Special Feature: Asynchronous Tasks
    /*
        This inner class is our apps AsyncTask. It contains all the code for making an
        API call, parsing the recieving information and displaying it on the screen.
        It does this through the basic AsyncTask structure (onPreExecute, doInBackground onPostExecute).
    */
    inner class weatherTask() : AsyncTask<String, Void, String>() {
        /*
            This function is invoked in UI thread before the AsyncTask is executed. We use it to show the
            loading bar while the app is loading the weather results from the API. It is generally used
            for UI changes before the AsyncTask.
        */
        override fun onPreExecute() {
            super.onPreExecute()
            // Show the loader and hide the main application and error text
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }
        /*
            This function does all the background execution in an AsyncTask. For our program we utilize
            it to do the API call and return the results, or null if there is an exception.

            Return The JSON file containing the weather information
        */
        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                // This is the API call, uses city and units as variables to allow for user customization
                response = URL("https://api.openweathermap.org/data/2.5/weather?q="+city+"&units="+units+"&appid=$API").readText(
                    Charsets.UTF_8
                )
            }catch (e: Exception){
                response = null
            }
            return response
        }

        /*
            This function gets triggered after doInBackground is over. The values returned from the doInBackground
            are received here. We then take that JSON file and parse it, so we can apply key information to the
            right elements in the application.

            Parameters result The JSON string result from the doInBack API call
        */
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                // Retrieve all important information from JSON file using ID's
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val clouds = jsonObj.getJSONObject("clouds")
                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                    Date(updatedAt*1000)
                )
                val temp = main.getString("temp")+deg
                val feels_like = main.getString("feels_like")+deg
                val minTemp = main.getString("temp_min")+deg
                val maxTemp = main.getString("temp_max")+deg
                val pressure = main.getString("pressure")+" hPa"
                val humidity = main.getString("humidity")+"%"
                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")+" "+wind_units
                val all = clouds.getString("all")+"%"
                val weatherDescription = weather.getString("description")
                val address = jsonObj.getString("name")+", "+sys.getString("country")

                // Find view's on application page and edit respective views to show correct weather info
                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text =  updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temp).text = temp // sea_level
                findViewById<TextView>(R.id.min_temp).text = minTemp // was tempMin
                findViewById<TextView>(R.id.max_temp).text = maxTemp // was tempMax
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                    Date(sunrise*1000)
                )
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                    Date(sunset*1000)
                )
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity

                findViewById<TextView>(R.id.clouds).text = all
                findViewById<TextView>(R.id.feels_like).text = feels_like

                /* Views populated, Hiding the loader, Showing the main design */
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            } catch (e: Exception) {
                // If there was an exception, something probably went wrong with the city name.
                Toast.makeText(this@MainActivity, "Invalid city, please retry", Toast.LENGTH_SHORT).show()
                // Reset to default and rerun AsyncTask
                city = "Tucson"
                weatherTask().execute()
            }

        }
    }
}
