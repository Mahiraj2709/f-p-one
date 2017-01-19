package providers.fairrepair.service.fairrepairpartner.fragment.mech_on_way;

/**
 * Created by admin on 1/2/2017.
 */

public interface Presenter {
    void onMapReady();

    void setOffer(String offer);

    void connectToGoogleApiClient();
    void onResume();
    void onStop();
    void onPause();

    void iHaveArrived();

    void finishTask();
}
