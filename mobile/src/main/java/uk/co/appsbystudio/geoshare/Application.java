package uk.co.appsbystudio.geoshare;

import android.content.Context;

public class Application extends android.app.Application {

    private static Application instance;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        this.setContext(getApplicationContext());
    }

    public static Application getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
