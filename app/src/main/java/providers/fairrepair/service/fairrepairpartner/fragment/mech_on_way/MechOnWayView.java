package providers.fairrepair.service.fairrepairpartner.fragment.mech_on_way;

import com.google.android.gms.maps.model.LatLng;

import providers.fairrepair.service.fairrepairpartner.model.OfferAccepted;

/**
 * Created by admin on 1/2/2017.
 */

public interface MechOnWayView {
    void generateMap();

    void setView(OfferAccepted offer);

    void setMap(LatLng mechLagLng, LatLng customerLatLng);
    void iHaveArrived();

}
