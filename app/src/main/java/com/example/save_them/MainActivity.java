package com.example.save_them;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.save_them.AccessibilityService.WhatsappAccessibilityService;
import com.example.save_them.controllers.UsersController;
import com.example.save_them.modelos.Users;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private List<Users> listaDeUsers;
    private RecyclerView recyclerView;
    private AdaptadorUsers adaptadorUsers;
    private UsersController usersController;
    private FloatingActionButton fabAgregarContacto;
    SensorManager senSensorManager;
    Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 10000;



    LocationManager locationManager;
    double longitudeGPS, latitudeGPS;
    TextView longitudeValueGPS, latitudeValueGPS;





    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        longitudeValueGPS = (TextView) findViewById(R.id.longitudeValueGPS);
        latitudeValueGPS = (TextView) findViewById(R.id.latitudeValueGPS);

        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        locationManager.removeUpdates(locationListenerGPS);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 2 * 20 * 1000, 10, locationListenerGPS);


        if (!isAccessibilityOn (getApplicationContext(), WhatsappAccessibilityService.class)) {
            Intent intent = new Intent (Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity (intent);
        }


        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener((SensorEventListener) this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        getRandomNumber();

        // Definir nuestro controlador
        usersController = new UsersController(MainActivity.this);

        // Instanciar vistas
        recyclerView = findViewById(R.id.recyclerViewContactos);
        fabAgregarContacto = findViewById(R.id.fabAgregarContactos);


        // Por defecto es una lista vacía,
        // se la ponemos al adaptador y configuramos el recyclerView
        listaDeUsers = new ArrayList<>();
        adaptadorUsers = new AdaptadorUsers(listaDeUsers);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adaptadorUsers);

        // Una vez que ya configuramos el RecyclerView le ponemos los datos de la BD
        refrescarListaDeUser();

        // Listener de los clicks en la lista, o sea el RecyclerView
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override // Un toque sencillo
            public void onClick(View view, int position) {
                Users usersSeleccionada = listaDeUsers.get(position);
                Intent intent = new Intent(MainActivity.this, EditarUserActivity.class);
                intent.putExtra("idContact", usersSeleccionada.getId());
                intent.putExtra("nameContact", usersSeleccionada.getNombre());
                intent.putExtra("numberContact", usersSeleccionada.getNumero());
                startActivity(intent);
            }

            @Override // Un toque largo
            public void onLongClick(View view, int position) {
                final Users usersParaEliminar = listaDeUsers.get(position);
                AlertDialog dialog = new AlertDialog
                        .Builder(MainActivity.this)
                        .setPositiveButton("Sí, eliminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                usersController.eliminarUser(usersParaEliminar);
                                refrescarListaDeUser();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setTitle("Confirmar")
                        .setMessage("¿Eliminar user " + usersParaEliminar.getNombre() + "?")
                        .create();
                dialog.show();

            }
        }));

        // Listener del FAB
        fabAgregarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simplemente cambiamos de actividad
                Intent intent = new Intent(MainActivity.this, AgregarUserActivity.class);
                startActivity(intent);
            }
        });

        fabAgregarContacto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("cfsdafas")
                        .setMessage("dsadas ")
                        .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogo, int which) {
                                dialogo.dismiss();
                            }
                        })
                        .setPositiveButton("Sitio web", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intentNavegador = new Intent(Intent.ACTION_VIEW, Uri.parse(""));
                                startActivity(intentNavegador);
                            }
                        })
                        .create()
                        .show();
                return false;
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        refrescarListaDeUser();
    }

    public void refrescarListaDeUser() {

        if (adaptadorUsers == null) return;
        listaDeUsers = usersController.obtenerUsers();
        adaptadorUsers.setListaDeContactos(listaDeUsers);
        adaptadorUsers.notifyDataSetChanged();
    }
    private void getRandomNumber() {

        Toast.makeText(this, "lento", Toast.LENGTH_SHORT).show();
        try {
            TextView numero = findViewById(R.id.tvEdad);

            String nn = numero.getText().toString();
            String lat = latitudeValueGPS.getText().toString();
            String lon = longitudeValueGPS.getText().toString();
            String group = "play";
            String text = "message" + " La ubicacion actual es : https://www.google.com/maps/place/"+lat+","+lon;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.whatsapp");
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+ nn +"&text="+text + " hi"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
            Toast.makeText(getApplicationContext(), nn,Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    getRandomNumber();
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    private boolean isAccessibilityOn (Context context, Class<? extends AccessibilityService> clazz) {
        Log.d(TAG, "check the Accessibility");
        int accessibilityEnabled = 0;
        final String service = context.getPackageName () + "/" + clazz.getCanonicalName ();
        Log.d(TAG, "String service: " + service);
        try {
            accessibilityEnabled = Settings.Secure.getInt (context.getApplicationContext ().getContentResolver (), Settings.Secure.ACCESSIBILITY_ENABLED, 0);
            Log.d(TAG, "accessibilityEnabled:" + accessibilityEnabled);
        } catch (Exception e) {
            Log.e(TAG, "get accessibility enable failed, the err:" + e.getMessage());
        }
        Log.d(TAG, "debug for accessibility.");

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter (':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString (context.getApplicationContext ().getContentResolver (), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue == null)
                Log.d(TAG, "settingValue == null");
            if (settingValue != null) {
                Log.d(TAG, "settingValue != null");
                colonSplitter.setString (settingValue);

                Log.d(TAG, "colonSplitter:" + colonSplitter);
                Log.d(TAG, "settingValue:" + settingValue);

                while (colonSplitter.hasNext ()) {
                    String accessibilityService = colonSplitter.next ();
                    Log.d(TAG, "while loop.");
                    Log.d(TAG, "accessibilityService:" + accessibilityService);
                    if (accessibilityService.equalsIgnoreCase (service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        }else{
            Log.d(TAG,"Accessibility service disable");
        }
        return false;
    }

    /*
     *IN THIS SECTION WE ARE CHECKING IF THE PERMISSION ARE ENABLED
     */
    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }
    /*
     *THIS PART WORK AS MESSAGE WHERE SHOW IF THE LOCATION IS DISABLED, SO
     *YOU NEED PUSH THE ACTIVATED BUTTON
     */
    private void showAlert() {
        final androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " +
                        "usa esta app")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }
    /*
     *THIS METHOD WORK IF THE LOCATION IS ENABLED
     */
    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    /*
     *THIS METHOD UPDATE THE LATITUDE AND MAGNITUDE
     */
//    public void toggleGPSUpdates(View view) {
//        if (!checkLocation())
//            return;
//        Button button = (Button) view;
//        if (button.getText().equals(getResources().getString(R.string.pause))) {
//            locationManager.removeUpdates(locationListenerGPS);
//            button.setText(R.string.resume);
//        } else {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            }
//            locationManager.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER, 2 * 20 * 1000, 10, locationListenerGPS);
//            button.setText(R.string.pause);
//        }
//    }


    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    longitudeValueGPS.setText(longitudeGPS + "");
                    latitudeValueGPS.setText(latitudeGPS + "");
                    Toast.makeText(MainActivity.this, "GPS Provider update", Toast.LENGTH_SHORT).show();
                }
            });

        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }
        @Override
        public void onProviderDisabled(String s) {
        }
    };
}
