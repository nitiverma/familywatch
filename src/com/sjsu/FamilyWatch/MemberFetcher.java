package com.sjsu.FamilyWatch;

import android.net.Uri;
import android.util.JsonReader;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MemberFetcher {
    public static final String TAG = "MemberFetcher";

    private static final String ENDPOINT = "http://familywatch-server.herokuapp.com/members";

    public List<Member> getMembers(String id) {
        String urlString = Uri.parse(ENDPOINT).buildUpon()
                //.appendPath(id)
                .build().toString();

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            JsonReader reader = new JsonReader(new InputStreamReader(connection.getInputStream()));
            return parseJson(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Member> parseJson(JsonReader reader) throws IOException {
        reader.beginObject();
        List<Member> members = null;
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("members")) {
                members = parseMembers(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        for (int i = 0; i < members.size(); i++) {
            Log.d(TAG, members.get(i).toString());
        }
        return members;
    }

    private List<Member> parseMembers(JsonReader reader) throws IOException {
        List<Member> products = new ArrayList<Member>();
        reader.beginArray();
        while (reader.hasNext()) {
            products.add(parseMember(reader));
        }
        reader.endArray();
        return products;
    }

    private Member parseMember(JsonReader reader) throws IOException {
        Member member = new Member();
        reader.beginObject();
        double latitude = 0.0;
        double longitude = 0.0;
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("name")) {
                member.setName(reader.nextString());
            } else if (name.equals("latitude")) {
                latitude = reader.nextDouble();
            } else if (name.equals("longitude")) {
                longitude = reader.nextDouble();
            } else if (name.equals("timestamp")) {
                member.setTimestamp(reader.nextLong());
            } else if (name.equals("image")) {
                member.setImage(reader.nextString());
            } else if (name.equals("id")) {
                member.setId(reader.nextString());
            } else if (name.equals("phoneno")) {
                member.setPhoneNo(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        LatLng location = new LatLng(latitude, longitude);
        member.setLocation(location);

        reader.endObject();
        return member;
    }

}
