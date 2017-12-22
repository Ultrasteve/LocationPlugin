package edu.cmu.chimps.location_plugin;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import java.util.List;

import rx.Observable;


/**
 * Created by Steve_su on 22/12/2017.
 */
public class LocationUtils {

    private volatile static LocationUtils uniqueInstance;
    private LocationManager locationManager;
    private String locationProvider;
    private Location location;
    private Context mContext;
    private String TAG = "LocationUtils";

    private LocationUtils(Context context) {
        mContext = context;
        getLocation();
    }

    public static LocationUtils getInstance(Context context) {
        if (uniqueInstance == null) {
            synchronized (LocationUtils.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new LocationUtils( context );
                }
            }
        }
        return uniqueInstance;
    }

    private void getLocation() {

        locationManager = (LocationManager) mContext.getSystemService( Context.LOCATION_SERVICE );

        List<String> providers = locationManager.getProviders( true );
        if (providers.contains( LocationManager.NETWORK_PROVIDER )) {

            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else if (providers.contains( LocationManager.GPS_PROVIDER )) {

            locationProvider = LocationManager.GPS_PROVIDER;
        } else {

            return;
        }

        if (Build.VERSION.SDK_INT >= 23 &&
                ActivityCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (ActivityCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_FINE_LOCATION ) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION )
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = locationManager.getLastKnownLocation( locationProvider );
        if (location != null) {
            setLocation( location );
        }

    }

    private void setLocation(Location location) {
        this.location = location;
    }


    public Observable<Location> showLocation() {
        return Observable.just(location);
    }


}
