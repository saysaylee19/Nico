package com.nicotrax.nicotrax;

import com.parse.Parse;
import android.app.Application;

public class NicotraxApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //Parse Initialization
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "AfkB6YO9Y9dWyBj3d75I32W8l97VcEVl6PLuAtxm", "hpcaSorCjsfhwhga4wpo6qDmiaUSJdLTtiXo0SNB");
    }
}
