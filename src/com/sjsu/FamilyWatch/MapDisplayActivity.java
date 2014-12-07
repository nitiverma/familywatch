package com.sjsu.FamilyWatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.*;

public class MapDisplayActivity extends Activity {

    private static final String TAG = "MapDisplayActivity";
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private Map<String, Member> mMembers = new HashMap<String, Member>();

    public void makeUseOfNewLocation(Location location) {
        updateSelfLocation(location);
        setupMarkers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_add_family_member:
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == 1) {
            Uri contactData = data.getData();
            Cursor contactCursor = getContentResolver().query(contactData,
                    new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME },
                    null,
                    null,
                    null);
            if (contactCursor.getCount() == 0) {
                Log.e(TAG, "No contact found");
                return;
            }

            contactCursor.moveToFirst();
            String name = contactCursor.getString(contactCursor
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            contactCursor.close();
            Cursor phoneCursor = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "= ? ",
                    new String[] { name }, null);
            if (phoneCursor.getCount() <= 0) {
                Log.e(TAG, "No phone no found");
                return;
            }
            phoneCursor.moveToFirst();
            String phoneNumber = phoneCursor.getString(0);
            phoneCursor.close();
            Log.e(TAG, "Name: " + name + " Phone No: " + phoneNumber);
            Uri smsUri = Uri.parse("smsto:" + phoneNumber);
            Intent intent = new Intent(Intent.ACTION_SENDTO, smsUri);
            intent.putExtra("sms_body", getString(R.string.greeting) + name + getString(R.string.invite_sms));
            startActivity(intent);
        }
    }


    private class FetchDataTask extends AsyncTask<Void, Void, List<Member>> {
        @Override
        protected List<Member> doInBackground(Void... params) {
            MemberStore store = MemberStore.get();
            return store.getMembers();
        }

        @Override
        protected void onPostExecute(List<Member> members) {
            for (Member member : members) {
                mMembers.put(member.getId(), member);
            }
            setupMarkers();
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        setContentView(R.layout.main);
        // Acquire a reference to the system Location Manager
        mLocationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


        // Get a handle to the Map Fragment
        mMap = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                Member member = mMembers.get(marker.getTitle());
                View v = getLayoutInflater().inflate(R.layout.info_window, null, false);
                if (member != null) {
                    ((TextView) v.findViewById(R.id.name_textview)).setText(member.getName());
                    String date = DateFormat.getDateTimeInstance().format(new Date(member.getTimestamp()));
                    ((TextView) v.findViewById(R.id.timestamp_textview)).setText(getString(R.string.timestamp) + date);
                } else {
                    Log.e(TAG, "MEMBER IS NULL");
                }
                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                final Member member = mMembers.get(marker.getTitle());
                if (member == null) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MapDisplayActivity.this);
                String[] options = { getString(R.string.are_you_ok), getString(R.string.when_are_you_coming),
                getString(R.string.custom_message), getString(R.string.call)};
                builder.setTitle(R.string.choose_action)
                        .setItems(options, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Uri smsUri = Uri.parse("smsto:" + member.getPhoneNo());
                                String phoneUri = "tel:" + member.getPhoneNo() ;
                                Intent it;
                                switch (which) {
                                    case 0:
                                        it = new Intent(Intent.ACTION_SENDTO, smsUri);
                                        it.putExtra("sms_body", getString(R.string.are_you_ok_sms));
                                        startActivity(it);
                                        break;
                                    case 1:
                                        it = new Intent(Intent.ACTION_SENDTO, smsUri);
                                        it.putExtra("sms_body", getString(R.string.when_are_you_coming_sms));
                                        startActivity(it);
                                        break;
                                    case 2:
                                        it = new Intent(Intent.ACTION_SENDTO, smsUri);
                                        startActivity(it);
                                        break;
                                    case 3:
                                        it = new Intent(Intent.ACTION_DIAL);
                                        it.setData(Uri.parse(phoneUri));
                                        startActivity(it);
                                        break;
                                }
                            }
                        }
                ).create().show();
            }
        });
        new FetchDataTask().execute();

    }

    private void updateSelfLocation(Location lastKnownLocation) {
        Member self = mMembers.get("0");
        if (self != null) {
            self.setLocation(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
            long timestamp = lastKnownLocation.getTime();
            self.setTimestamp(timestamp);
            mMembers.put("0", self);
        }
    }

    private void setupMarkers() {
        double averageLatitude = 0;
        double averageLongitude = 0;
        List<Double> latitudes = new ArrayList<Double>();
        List<Double> longitudes = new ArrayList<Double>();
        Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        updateSelfLocation(lastKnownLocation);
        if (mMembers.size() == 0) {
            return;
        }
        for (Member member : mMembers.values()) {
            mMembers.put(member.getId(), member);
            double latitude = member.getLocation().latitude;
            double longitude = member.getLocation().longitude;
            latitudes.add(latitude);
            longitudes.add(longitude);
            averageLatitude += latitude;
            averageLongitude += longitude;
            LatLng position = new LatLng(latitude, longitude);
            // TODO: Add the proper formatting of the timestamp last seen.
            mMap.addMarker(new MarkerOptions()
                    .title(member.getId())
                    .position(position)
                    .icon(BitmapDescriptorFactory.fromBitmap(member.getImage())));

        }
        averageLatitude /= mMembers.size();
        averageLongitude /= mMembers.size();
        double latitudeDifference = Collections.max(latitudes) - Collections.min(latitudes);
        double zoomByLatitude = Math.log(360 / latitudeDifference) / Math.log(2);
        double longitudeDifference = Collections.max(longitudes) - Collections.min(longitudes);
        double zoomByLongtitude = Math.log(180 / longitudeDifference) / Math.log(2);

        float zoomBy = (float) Math.min(zoomByLatitude, zoomByLongtitude);
        Log.e(TAG, "Zoom by level : " + String.valueOf(zoomBy));
        LatLng center = new LatLng(averageLatitude, averageLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoomBy));
    }
}
