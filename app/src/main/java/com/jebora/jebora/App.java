package com.jebora.jebora;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Tiffanie on 15-05-30.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "kfDdVWjDzVz5m5JKbETfPvR2u7NLGS4oTHB2vczN", "ElbkPnUHks44zRZm5hdNt21Sva0o4GxN6ZJlAwkJ");

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
    }
}
