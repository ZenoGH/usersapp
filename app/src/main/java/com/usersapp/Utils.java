package com.usersapp;


import android.content.res.Resources;
import android.util.TypedValue;
import com.google.gson.Gson;

public class Utils {
    static Gson gson = new Gson();

//    public static void test() throws IOException {
//        Payload payload = new Payload("John Doe", "jdoe666", "S@7@n9UL3Z");
//        String json = gson.toJson(payload);
//        Log.d("tag", json);
//        get(url + "read", new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e("failure", "FAIL!!!XDXD");
//                Log.e("ERROR", String.valueOf(e));
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    String responseStr = response.body().string();
//                    Log.i("success", responseStr);
//                    Payload[] payloads = gson.fromJson(responseStr, Payload[].class);
//                    Log.i("test", "test");
//                } else {
//                    Log.e("failure", "Error. Response code " + response.code());
//                }
//            }
//        });



    public static <T> int getIndex(T[] array, T element) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(element)) {
                return i;
            }
        }
        return -1;
    }
}
