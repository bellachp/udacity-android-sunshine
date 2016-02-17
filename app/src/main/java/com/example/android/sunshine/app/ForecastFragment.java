package com.example.android.sunshine.app;

/**
 * Created by agcpb on 2/16/2016.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A forecast fragment containing a simple view.
 */
public class ForecastFragment extends Fragment
{

    ArrayAdapter<String> weatherDataAdapter;

    String localBaseWeather = "http://api.openweathermap.org/data/2.5/forecast/daily?zip=63128&mode=json&units=metric&cnt=7";

    public ForecastFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_refresh)
        {
            FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
            fetchWeatherTask.execute(localBaseWeather);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] fakeData =
                {
                        "Monday - Sunny - 45/14",
                        "Tuesday - Snowy - 33/5",
                        "Wednesday - Cloudy - 55/34",
                        "Thursday - Painful - 45/23",
                        "Friday - Great - 65/44",
                        "Saturday - Sunny - 50/38",
                        "Sunday - BUT WHY?? - 9/0"
                };
        List<String> fakeWeather = new ArrayList<String>(Arrays.asList(fakeData));

        String dummyDay = "theFuture - umm - ?/?";
        for (int k = 0; k < 10; k++)
            fakeWeather.add(dummyDay);

        weatherDataAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast, R.id.list_item_forecast_textview, fakeWeather);

        ListView listViewRef = (ListView) rootView.findViewById(R.id.listView_forecast);
        listViewRef.setAdapter(weatherDataAdapter);


        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urlStringsIn)
        {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try
            {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
                URL url = new URL(urlStringsIn[0].concat(apiKey));


                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null)
                {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null)
                {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0)
                {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();

                //temp
                Log.v("FetchWeatherDEBUG", "Forecast JSON string: " + forecastJsonStr);

            } catch (IOException e)
            {
                Log.e("FetchWeatherTask", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                return null;
            } finally
            {
                if (urlConnection != null)
                {
                    urlConnection.disconnect();
                }
                if (reader != null)
                {
                    try
                    {
                        reader.close();
                    } catch (final IOException e)
                    {
                        Log.e("FetchWeatherTask", "Error closing stream", e);
                    }
                }
            }
            return forecastJsonStr;
        }

    }
}