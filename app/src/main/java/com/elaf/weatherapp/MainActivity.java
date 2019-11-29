package com.elaf.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    final int MY_PERMISSIONS_REQUEST_GET_LOCATION = 1;
    double lat, lot;
    String url = "https://api.darksky.net/forecast/74057e3a4d8504262ea3d83644d2438f/";
    String timeZone;
    String pastTemp;
    String temperature = "55";
    String precipitation = "";
    String humid = "";
    String windSpeed = "";
    String avgTempDeg = "49";
    String circle = "O";
    String response1;
    String response2;
    Button Search;
    TextView symbol;
    TextView weeklyTemps;
    TextView temperatureView;
    TextView rainChance;
    TextView humidity;
    TextView wind;
    TextView avgTempDes;
    TextView avgTempDegr;
    TextView Hour1Deg;
    TextView Hour2Deg;
    TextView Hour3Deg;
    TextView Hour4Deg;
    TextView Hour5Deg;
    TextView cityView;
    TextView daysTemps;
    TextView city;
    String avgTemp = "48hr Avg";
    String[] coordinates = new String[2];
    int permission = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViews();
        weatherApi();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        final EditText pastDate = findViewById(R.id.pastDate);
        pastDate.setText("");

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pastDate.getText().toString().equals("")) {
                    Log.d("Search -----------", pastDate.getText().toString());
                    String input = pastDate.getText().toString();
                    String time = unixToTime(input);
                    if (time != null) {
                        coordinates = locationCoords();
                        String url1 = url + coordinates[0] + "," + coordinates[1] + "," + time;
                        final OkHttpClient client = new OkHttpClient();
                        final Request request = new Request.Builder().url(url1).build();
                        client.newCall(request).enqueue(new Callback() {

                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    response2 = response.body().string();
                                    showPastData();
                                    response.close();

                                } else
                                    throw new IOException("Error in Response " + response);
                            }
                        });
                    } else {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                alertDialog.setTitle("Incorrect Format:");
                                alertDialog.setMessage("Enter date in this form (yyyy/mm/dd-HH24).");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }
                        });
                    }
                }
            }
        });
    }



    public void showPastData() {
        try {
            JSONObject data = new JSONObject(response2);
            JSONObject temp = data.getJSONObject("currently");
            pastTemp = String.valueOf(Math.round(temp.getDouble("temperature")));
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("PatTemp-----", pastTemp);
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Results:");
                    alertDialog.setMessage("The past/future temperature is " + pastTemp + "F.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Back",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public void getPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_GET_LOCATION);
            Log.d("MainActivity-------", "You don't have access for the location");
        }
    }



    public void setViews() {
        temperatureView = findViewById(R.id.temperatureView);
        temperatureView.setText("");
        rainChance = findViewById(R.id.rain);
        rainChance.setText("");
        humidity = findViewById(R.id.humidity);
        humidity.setText("");
        wind = findViewById(R.id.wind);
        wind.setText("");
        Hour1Deg = findViewById(R.id.Deg1);
        Hour1Deg.setText("");
        Hour2Deg = findViewById(R.id.Deg2);
        Hour2Deg.setText("");
        Hour3Deg = findViewById(R.id.Deg3);
        Hour3Deg.setText("");
        Hour4Deg = findViewById(R.id.Deg4);
        Hour4Deg.setText("");
        Hour5Deg = findViewById(R.id.Deg5);
        Hour5Deg.setText("");
        cityView = findViewById(R.id.cityView);
        cityView.setText("");
        weeklyTemps = findViewById(R.id.weeklyTemps);
        weeklyTemps.setText("");
        avgTempDes = findViewById(R.id.avgTemp);
        avgTempDes.setText("");
        avgTempDegr = findViewById(R.id.avgTempDeg);
        avgTempDegr.setText("");
        symbol = findViewById(R.id.Symbol);
        symbol.setText("");
        daysTemps = findViewById(R.id.textView2);
        daysTemps.setText("");
        city = findViewById(R.id.city);
        city.setText("");
        Search = findViewById(R.id.Search);
    }



    public void weatherApi() {
        coordinates = locationCoords();
        String url2 = url + coordinates[0] + "," + coordinates[1];
        Log.d("Coordinate.............", coordinates[0] + " " + coordinates[1]);
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url(url2).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    response1 = response.body().string();
                    showData();
                    response.close();
                } else
                    throw new IOException("Error in Response " + response);
            }
        });
    }



    public void showData() {
        try {
            JSONObject data = new JSONObject(response1);
            JSONObject temp = data.getJSONObject("currently");
            JSONObject byHour = data.getJSONObject("hourly");
            JSONArray temp5Hour = byHour.getJSONArray("data");
            JSONObject state = data.getJSONObject("minutely");
            String weatherState = state.getString("icon");
            final String weatherState1 = weatherState.replaceAll("-", " ");
            final JSONObject dailyTemps = data.getJSONObject("daily");
            JSONArray byDay = dailyTemps.getJSONArray("data");
            final String[] dayTempArrayHi = new String[7];
            final String[] dayTempArrayLo = new String[7];
            long[] dayTimeArray = new long[7];
            final String[] tempArray = new String[48];
            final long[] times = new long[5];
            int TempAddition = 0;
            for (int i = 1; i < 49; i++) {
                JSONObject hour = (JSONObject) temp5Hour.get(i);
                if (i < 6) {
                    tempArray[i - 1] = String.valueOf(Math.round(hour.getDouble("temperature")));
                    times[i - 1] = hour.getLong("time");
                }
                TempAddition += Math.round(hour.getDouble("temperature"));
                Log.d("Next Hour Temps", String.valueOf(TempAddition));
            }
            Log.d("Unix time1-------", String.valueOf(times[0]));

            for (int j = 1; j < 8; j++) {
                JSONObject day = (JSONObject) byDay.get(j);
                dayTempArrayHi[j - 1] = String.valueOf(Math.round(day.getDouble("temperatureHigh")));
                dayTempArrayLo[j - 1] = String.valueOf(Math.round(day.getDouble("temperatureLow")));
                dayTimeArray[j - 1] = day.getLong("time");
            }
            double twoDayTemp = Math.round(TempAddition / 48);
            Log.d("TWO DAY TEMPS-------", dayTempArrayHi[0] + ", " + dayTempArrayHi[1]);
            avgTempDeg = String.valueOf(Math.round(twoDayTemp));
            timeZone = data.getString("timezone");
            final String[] nextHours = nextHours(times, timeZone);
            final String[] nextDays = nextDays(dayTimeArray, timeZone);

            temperature = String.valueOf(Math.round(temp.getDouble("temperature")));
            double precipProbability = temp.getDouble("precipProbability");
            double humidityIndex = (temp.getDouble("humidity"));
            windSpeed = String.valueOf(Math.round(temp.getDouble("windSpeed")));
            precipitation = String.format("%.1f", precipProbability);
            humid = String.valueOf(Math.round(humidityIndex * 100));
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    city.setText("");
                    city.setText(getCity());
                    cityView.setText(weatherState1);
                    avgTempDes.setText(avgTemp);
                    temperatureView.setText(temperature);
                    symbol.setText(circle);
                    avgTempDegr.setText(avgTempDeg);
                    rainChance.setText("");
                    rainChance.append("Precip.\n");
                    rainChance.append(precipitation);
                    humidity.setText("");
                    humidity.append("Humidity\n");
                    humidity.append(humid + "%");
                    wind.setText("");
                    wind.append("Wind\n");
                    wind.append(windSpeed + "mph");
                    weeklyTemps.setText("");
                    daysTemps.setText("");
                    for (int i = 0; i < nextDays.length; i++) {
                        weeklyTemps.append(nextDays[i]);
                        daysTemps.append(dayTempArrayHi[i] + "   ");
                        daysTemps.append(dayTempArrayLo[i]);
                        if (i + 1 < nextDays.length) {
                            weeklyTemps.append("\n");
                            daysTemps.append("\n");
                        }
                    }
                    Hour1Deg.setText("");
                    Hour1Deg.append(nextHours[0] + "\n");
                    Hour1Deg.append(tempArray[0]);
                    Hour2Deg.setText("");
                    Hour2Deg.append(nextHours[1] + "\n");
                    Hour2Deg.append(tempArray[1]);
                    Hour3Deg.setText("");
                    Hour3Deg.append(nextHours[2] + "\n");
                    Hour3Deg.append(tempArray[2]);
                    Hour4Deg.setText("");
                    Hour4Deg.append(nextHours[3] + "\n");
                    Hour4Deg.append(tempArray[3]);
                    Hour5Deg.setText("");
                    Hour5Deg.append(nextHours[4] + "\n");
                    Hour5Deg.append(tempArray[4]);
                }
            });

        } catch (JSONException e1) {
            e1.printStackTrace();
        }

    }



    public String unixToTime(String input) {
        String time = "";
        if (input.length() > 13)
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-HH", Locale.US);
        sdf.setTimeZone(java.util.TimeZone.getTimeZone(timeZone));
        try {
            Date dt = sdf.parse(input);
            if (Integer.parseInt(input.substring(input.indexOf("-") + 1)) > 24)
                return null;
            long unixTime = dt.getTime();
            unixTime = unixTime / 1000L;
            time = String.valueOf((int) unixTime);
            Log.d("unix time -----------", time);
            return time;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }



    public String[] nextHours(long[] times, String timeZone) {
        String[] hours = new String[times.length];
        for (int i = 0; i < times.length; i++) {
            SimpleDateFormat df = new SimpleDateFormat("HH", Locale.ENGLISH);
            df.setTimeZone(java.util.TimeZone.getTimeZone(timeZone));
            hours[i] = df.format(new Date(times[i] * 1000)) + ":00";
        }
        return hours;
    }



    public String[] nextDays(long[] days, String timezone) {
        String[] dayNames = new String[days.length];
        for (int i = 0; i < days.length; i++) {
            SimpleDateFormat df = new SimpleDateFormat("EEEE", Locale.ENGLISH);
            df.setTimeZone(java.util.TimeZone.getTimeZone(timezone));
            Date dateFormat = new java.util.Date(days[i] * 1000);
            dayNames[i] = df.format(dateFormat);
        }
        return dayNames;
    }



    public String[] locationCoords() {
        getPermissions();
        String[] coords = new String[2];
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                lat = location.getLatitude();
                lot = location.getLongitude();
            } else {
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                lat = location.getLatitude();
                lot = location.getLongitude();
            }
            coords[0] = String.valueOf(lat);
            coords[1] = String.valueOf(lot);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return coords;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            weatherApi();
        } else {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Alert:");
                    alertDialog.setMessage("This application requires permission to access location services. Application will " +
                            "exit if you deny permission again!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    permission -= 1;
                                    getPermissions();
                                }
                            });
                    if (permission > 0)
                        alertDialog.show();
                    else finish();
                }
            });
        }

    }



    public String getCity() {
        String city = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lot, 1);
            city = addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return city;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
            weatherApi();
    }
}
