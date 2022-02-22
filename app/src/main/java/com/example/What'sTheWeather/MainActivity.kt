package com.example.testapp3

/*
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
 */

//import java.net.http.HttpRequest
//import java.net.http.HttpResponse
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


//import okhttp3.OkHttpClient
//import okhttp3.Request
//import java.io.IOException
////import okhttp3.MediaType.Companion.toMediaType
////import okhttp3.RequestBody.Companion.toRequestBody
//import com.squareup.okhttp.OkHttpClient;



class MainActivity : AppCompatActivity() {
    var city = "tucson,us"//  "dhaka,bd"
    val API: String = "f5979ed4f3b30e4e3dcc9c934eaeaa9f"

    var units = "imperial"
    var deg = "°F"
    var wind_units = "mph"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        doesn't affect start background for mobile device
        themeSwitch.isChecked = false

        weatherTask().execute()

//        // VisualCrossing (Kotlin, rapidapi.com):
//        val client = OkHttpClient()
//
//        val request = Request.Builder()
//            .url("https://visual-crossing-weather.p.rapidapi.com/forecast?aggregateHours=24&location=Washington%2CDC%2CUSA&contentType=csv&unitGroup=us&shortColumnNames=0")
//            .get()
//            .addHeader("x-rapidapi-host", "visual-crossing-weather.p.rapidapi.com")
//            .addHeader("x-rapidapi-key", "SIGN-UP-FOR-KEY")
//            .build()
//
//        val response = client.newCall(request).execute()
//
//        // VisualCrossing (from Java):
//        val request: java.net.http.HttpRequest = java.net.http.HttpRequest.newBuilder()
//            .uri(URI.create("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/tucson?unitGroup=metric&key=6DSK2NXVH9XAPD9KEBGFK36K6&contentType=json"))
//            .method("GET", java.net.http.HttpRequest.BodyPublishers.noBody()).build()
//        val response: java.net.http.HttpResponse = HttpClient.newHttpClient()
//            .send(request, java.net.http.HttpResponse.BodyHandlers.ofString())
//        println(response.body())

        // Special feature - Polymorphism: converts a TextView to a Button
        val btn_click_me = findViewById<TextView>(R.id.goSearchBut) // as Button // (Button) //findViewById(R.id.changeDegBtn) as Button
        // set on-click listener
        btn_click_me.setOnClickListener (object:View.OnClickListener {
            override fun onClick(p0: View?) {
//                TODO("Not yet implemented")
                // your code to perform when the user clicks on the button
//                Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
                city = findViewById<TextView>(R.id.cityString).text.toString()
                weatherTask().execute()
            }
        })

        val changeDegBtn = findViewById<TextView>(R.id.changeDegBtn) // as Button // (Button) //findViewById(R.id.changeDegBtn) as Button
        // set on-click listener
        changeDegBtn.setOnClickListener (object:View.OnClickListener {
            override fun onClick(p0: View?) {
//                TODO("Not yet implemented")
                // your code to perform when the user clicks on the button
//                Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
//                city = findViewById<TextView>(R.id.cityString).text.toString()
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

        // set the switch to listen on checked change
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->

            // if the button is checked, i.e., towards the right or enabled
            // enable dark mode, change the text to disable dark mode
            // else keep the switch text to enable dark mode
            if (themeSwitch.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                themeSwitch.text = "Disable dark mode"
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                themeSwitch.text = "Enable dark mode"
            }
        }


    }



    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
//            val units = "imperial"
            var response:String?
            try{
                // Current
                response = URL("https://api.openweathermap.org/data/2.5/weather?q="+city+"&units="+units+"&appid=$API").readText(
                    Charsets.UTF_8
                )

                // 16-day
//                response = URL("https://api.openweathermap.org/data/2.5/forecast/daily?q=London&units=metric&cnt=7&appid=$API").readText(
//                    Charsets.UTF_8
//                )

                // 5-day
//                response = URL("http://api.openweathermap.org/data/2.5/forecast?id=524901&lang=en&appid=$API").readText(
//                    Charsets.UTF_8
//                )

            }catch (e: Exception){
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

//                val list_main = jsonObj.getJSONArray("list").getJSONObject(1)
//                val sea_level = list_main.getString("sea_level")
//
//                val sea_level = jsonObj.getJSONObject("main").getString("sea_level")

//                val minutely = jsonObj.getJSONArray("minutely").getJSONObject(0)
//                val precip = minutely.getString("precipitation")
                val clouds = jsonObj.getJSONObject("clouds")



                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                    Date(updatedAt*1000)
                )
//                val label = "°F"



                val temp = main.getString("temp")+deg
                val feels_like = main.getString("feels_like")+deg
//                val minTemp = "Min Temp: " + main.getString("temp_min")+deg
//                val maxTemp = "Max Temp: " + main.getString("temp_max")+deg

                // () version:
//                val minTemp = "Min Temp \n" + "       " + main.getString("temp_min")+deg
//                val maxTemp = "Max Temp\n" + "        " + main.getString("temp_max")+deg
//                val pressure = "Pressure\n" + "       " + main.getString("pressure")+" hPa"
//                val humidity = "Humidity\n" + "       " + main.getString("humidity")+"%"
//                val sunrise:Long = sys.getLong("sunrise")
//                val sunset:Long = sys.getLong("sunset")
//                val windSpeed = "Wind (" + wind_units + ")\n" + "       " + wind.getString("speed")+" "+wind_units
//                val all = "Clouds\n" + "       " + clouds.getString("all")+"%"
//                val weatherDescription = weather.getString("description")
//                val address = jsonObj.getString("name")+", "+sys.getString("country")

                // units version:
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

                /* Populating extracted data into our views */

                // visibility?

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
                Toast.makeText(this@MainActivity, "Invalid city, please retry", Toast.LENGTH_SHORT).show()
//                toast.setGravity(Gravity.TOP or Gravity.LEFT, 0, 0)
//                Gravity.CENTER_VERTICAL
//                Gravity.TOP

//                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
//                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
//                Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
                city = "Tucson"
                weatherTask().execute()
            }

        }
    }
}
