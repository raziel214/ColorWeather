package com.example.raziel214.colorweather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String DATA = "data";
    public static final String SUMMARY = "summary";
    public static final String ICON = "icon";
    public static final String TEMPERATURE = "temperature";
    public static final String TEMPERATURE_MAX = "temperatureMax";
    public static final String TEMPERATURE_MIN = "temperatureMin";
    public static final String PRECIP_PROBABILITY = "precipProbability";
    public static final String TIME = "time";
    public static final String CURRENTLY = "currently";
    public static final String DAILY = "daily";
    public static final String HOURLY = "hourly";
    public static final String MINUTELY = "minutely";
    public static final String TIMEZONE = "timezone";
    public static final String DAYS_ARRAY_LIST = "DAYS_ARRAY_LIST";

    //implemenar butterknife acorta los tiempos de trabajo  e implementacion
    @BindView(R.id.iconImageView)ImageView iconImageView;
    @BindView(R.id.descriptionTextView)
    TextView descriptionTextView;
    @BindView(R.id.currentTempTextView) TextView currentTempTextView;
    @BindView(R.id.highestTempTextView)TextView highestTempTextView;
    @BindView(R.id.LowesTempTextView)TextView LowesTempTextView;

    ArrayList<Day> days;
    ArrayList<Hour> hours;
    ArrayList<Minute> minutes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        /* Toast.makeText(this, "Hola alan desde android" , Toast.LENGTH_SHORT).show();

        CurrentWeather currentWeather=new CurrentWeather(MainActivity.this);
        currentWeather.setIconImage("sunny");
        currentWeather.setDescription("Sunny Day");
        currentWeather.setCurrentTemperature("19");
        currentWeather.setHighestTemperature("H:25");
        currentWeather.setLowestTemperature("L:10");
        iconImageView.setImageDrawable(currentWeather.getIconDrawableResource());
        descriptionTextView.setText(currentWeather.getDescription());
        currentTempTextView.setText(currentWeather.getCurrentTemperature());
        highestTempTextView.setText(currentWeather.getHighestTemperature());
        LowesTempTextView.setText(currentWeather.getLowestTemperature());*/






        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://api.darksky.net/forecast/84b63f48072a94a8efefc0f12f1d1011/37.8267,-122.4233?units=si";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                        //Log.d(TAG,"Response is: "+ response.substring(0,500));
                        try {


                            CurrentWeather currentWeather=getCurrentWeatherFromJson(response);

                            days=getDailyWeatherFromJson(response);

                            hours=getHourlyWeatherFromJson(response);
                            minutes=getMinutelyWeatherFormJson(response);





                            iconImageView.setImageDrawable(currentWeather.getIconDrawableResource());
                            //el %s equivale a un place holder en la clase  string format
                            descriptionTextView.setText(currentWeather.getDescription());
                            currentTempTextView.setText(currentWeather.getCurrentTemperature());
                            highestTempTextView.setText(String.format("H: %s°",currentWeather.getHighestTemperature()));
                            LowesTempTextView.setText(String.format("L: %s°",currentWeather.getLowestTemperature()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
                Log.d(TAG,"That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    @OnClick(R.id.DailyWheatherTextView)
    public void dailyWeatherClick(){
        Intent dailyActivityIntent = new Intent(MainActivity.this, DailyWheatherActivity.class);
        dailyActivityIntent.putParcelableArrayListExtra(DAYS_ARRAY_LIST,days);

        startActivity(dailyActivityIntent);

    }
    @OnClick(R.id.HourlyWheatherTextView)
    public void hourlyWeatherClick(){
        Intent hourlyWeatherActivity = new Intent(MainActivity.this, HourlyWeatherActivity.class);
        startActivity(hourlyWeatherActivity);
    }
    @OnClick(R.id.MinutelyWheatherTextView)
    public void minutelyWeatherClick(){
        Intent minutelyWeatherActivity = new Intent(MainActivity.this, MinutelyweatherActivity.class);
        startActivity(minutelyWeatherActivity);

    }



    private CurrentWeather getCurrentWeatherFromJson(String json) throws JSONException{

        JSONObject jsonObject=new JSONObject(json);

        JSONObject jsonWhitCurrentWeather = jsonObject.getJSONObject(CURRENTLY);

        JSONObject jsonWhitDailyWeather= jsonObject.getJSONObject(DAILY);
        JSONArray jsonWhitDailyWeatherData= jsonWhitDailyWeather.getJSONArray(DATA);
        JSONObject jsonWithTodaydata= jsonWhitDailyWeatherData.getJSONObject(0);

        JSONObject jsonWhitMinutelyWeather= jsonObject.getJSONObject(MINUTELY);
        JSONObject jsonWhitHourlyWeather= jsonObject.getJSONObject(HOURLY);

        String summary = jsonWhitCurrentWeather.getString(SUMMARY);

        String icon=jsonWhitCurrentWeather.getString(ICON);
        String temperature=Math.round(jsonWhitCurrentWeather.getDouble(TEMPERATURE))+"";
        String maxTemperature= Math.round(jsonWithTodaydata.getDouble(TEMPERATURE_MAX))+"";
        String minTemperature= Math.round(jsonWithTodaydata.getDouble(TEMPERATURE_MIN))+"";


        CurrentWeather currentWeather= new CurrentWeather(MainActivity.this);

        currentWeather.setDescription(summary);
        currentWeather.setIconImage(icon);
        currentWeather.setCurrentTemperature(temperature);
        currentWeather.setLowestTemperature(minTemperature);
        currentWeather.setHighestTemperature(maxTemperature);

        return currentWeather;





    }

    private ArrayList<Day> getDailyWeatherFromJson(String json)throws JSONException
    {

        DateFormat dateFormat= new SimpleDateFormat("EEEE");
        ArrayList<Day> days=new ArrayList<>();

        JSONObject jsonObject=new JSONObject(json);
        String timezone=jsonObject.getString(TIMEZONE);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));


        JSONObject jsonWhitDailyWeather= jsonObject.getJSONObject(DAILY);

        JSONArray jsonWhitDailyWeatherData= jsonWhitDailyWeather.getJSONArray(DATA);

        for (int i=0; i<jsonWhitDailyWeatherData.length();i++)
        {

            Day day=new Day();
            JSONObject jsonWithDayData = jsonWhitDailyWeatherData.getJSONObject(i);

            String rainProbability= jsonWithDayData.getDouble(PRECIP_PROBABILITY)+"";


            String description=jsonWithDayData.getString(SUMMARY);
            String dayName=dateFormat.format(jsonWithDayData.getLong(TIME)*1000);

            day.setDayname(dayName);
            day.setRainProbability(rainProbability);
            day.setWeatherDescription(description);

            days.add(day);



        }


        return days;

    }

    public ArrayList<Hour> getHourlyWeatherFromJson(String json)throws JSONException{

        DateFormat dateFormat= new SimpleDateFormat("HH:mm");

        ArrayList<Hour> hours=new ArrayList<>();

        JSONObject jsonObject=new JSONObject(json);

        String timezone=jsonObject.getString(TIMEZONE);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));



        JSONObject jsonWhitHourlyWeather= jsonObject.getJSONObject(HOURLY);

        JSONArray jsonWhitHourlyWeatherData= jsonWhitHourlyWeather.getJSONArray(DATA);


        for (int i=0; i<jsonWhitHourlyWeatherData.length();i++)
        {

            Hour hour=new Hour();
            JSONObject jsonWithHourDayData = jsonWhitHourlyWeatherData.getJSONObject(i);

            String rainProbability= jsonWithHourDayData.getDouble(PRECIP_PROBABILITY)+"";

            String description=jsonWithHourDayData.getString(SUMMARY);
            String title=dateFormat.format(jsonWithHourDayData.getDouble(TIME)*1000);

            hour.setTitle(title);
            hour.setWeatherDescription(description);

            hours.add(hour);



        }


        return hours;


    }

    public ArrayList<Minute> getMinutelyWeatherFormJson(String json) throws JSONException{

        DateFormat dateFormat= new SimpleDateFormat("HH:mm");

        ArrayList<Minute> minutes=new ArrayList<>();
        JSONObject jsonObject=new JSONObject(json);
        String timezone=jsonObject.getString(TIMEZONE);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));


        JSONObject jsonWhitMinutelyWeather= jsonObject.getJSONObject(MINUTELY);

        JSONArray jsonWhitMinutelyWeatherData= jsonWhitMinutelyWeather.getJSONArray(DATA);

        for(int i=0;i< jsonWhitMinutelyWeatherData.length();i++)
        {
            Minute minute = new Minute();

            JSONObject jsonWhitMinutelyData=jsonWhitMinutelyWeatherData.getJSONObject(i);

            String title=dateFormat.format(jsonWhitMinutelyData.getDouble(TIME)*1000);
            String precipProbability= jsonWhitMinutelyData.getDouble(PRECIP_PROBABILITY)+"";

            minute.setTitle(title);
            minute.setRainProbability(precipProbability);

            minutes.add(minute);



        }





        return minutes;

    }





}
