package providers.fairrepair.service.fairrepairpartner;

import android.app.Application;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by admin on 11/24/2016.
 */

public class FairRepairApplication extends Application {

    public static boolean isVisible = false;
    public static boolean isAvailable = false;
    public static Bus bus = null;

    public static int timeToAcceptRequest = -1;
    public static Bus getBus() {
        if (bus == null) {
            bus = new Bus(ThreadEnforcer.ANY);
        }
        return bus;
    }
}
