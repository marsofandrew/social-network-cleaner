package com.marsofandrew.social_network_cleaner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vk.api.sdk.client.actors.UserActor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public final class Authorization {
    private static final Integer APP_ID = 2274003;
    private static final String CLIENT_SECRET = "hHbZxrka2uZ6jB1inYsH";

    public static UserActor authenticate(String username, String password) {
        String codeUri = String.format(
                "https://oauth.vk.com/token?grant_type=password&client_id=%d" +
                        "&scope=wall,photos,offline,videos&username=%s" +
                        "&password=%s" +
                        "&client_secret=%s", APP_ID,
                username, password, CLIENT_SECRET);

        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) new URL(codeUri).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
            return null;
        }
        con.setRequestProperty("User-Agent", "VKAndroidApp/5.23-2978 (Android 4.4.2; SDK 19; x86; unknown Android SDK" +
                " built for x86; en; 320x240)");

        UserActor userActor = null;
        try (InputStreamReader is = new InputStreamReader(con.getInputStream());
             BufferedReader reader = new BufferedReader(is)) {
            JsonNode response = new ObjectMapper().readTree(reader);
            userActor = new UserActor(response.get("user_id").asInt(), response.get("access_token").asText());
        } catch (IOException e) {
           return null;
        }
        return userActor;
    }
}
