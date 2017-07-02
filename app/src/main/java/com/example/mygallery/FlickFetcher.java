package com.example.mygallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Денис on 01.07.2017.
 */

public class FlickFetcher {
    private static final String TAG = "FlickFetcher";
    private static final String API_KEY = "";

    private String getJSONString(String UrlSpec) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(UrlSpec)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public List<GalleryItem> fetchItems() {
        List<GalleryItem> galleryItems = new ArrayList<>();

        try {
            String url = Uri.parse("https://api.flicker.com/services/rest")
                    .buildUpon()
                    .appendQueryParameter("method", "flicker.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();
            String jsonString = getJSONString(url);
            JSONObject jsonObject = new JSONObject(jsonString);
            parseItems(galleryItems, jsonObject);
        } catch (IOException ioe){
            Log.e(TAG, "Ошибка загрузки данных ", ioe);
        } catch (JSONException joe){
            Log.e(TAG, "Ошибка парсинга json", joe);
        }

        return galleryItems;
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonObject) throws IOException, JSONException{
        JSONObject photosJsonObject = jsonObject.getJSONObject("photos");
        JSONArray photosJsonArray = photosJsonObject.getJSONArray("photo");

        for (int i = 0; i < photosJsonArray.length(); i++) {
            JSONObject photoJsonObject = photosJsonArray.getJSONObject(i);
            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("Id"));
            item.setCaption(photoJsonObject.getString("title"));

            if(!photoJsonObject.has("url_s")){
                continue;
            }
            item.setUrl(photoJsonObject.getString("url_s"));
            items.add(item);
        }
    }
}
