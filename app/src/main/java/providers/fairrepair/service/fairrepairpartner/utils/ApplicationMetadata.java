package providers.fairrepair.service.fairrepairpartner.utils;

/**
 * Created by admin on 11/22/2016.
 */

public interface ApplicationMetadata {
    float MAP_ZOOM_VALUE = 16.0f;
    String DEVICE_ID = "device_id";
    String DEVICE_TOKEN = "device_token";
    String APP_LANGUAGE = "app_language";
    int SUCCESS_RESPONSE_STATUS = 1;
    int FAILURE_RESPONSE_STATUS = 0;

    String RESPONSE_MSG = "response_msg";
    String RESPONSE_DATA = "response_data";
    String USER_NAME = "name";
    String USER_EMAIL = "email";
    String USER_MOBILE = "mobile";
    String USER_IMAGE = "profile_pic";
    String LOGIN = "login";
    String USER_ID = "customer_id";
    String BOOKING_STATUS = "booking_status";
    String VEHICLE_TYPE = "type";
    String VEHICLE_MESSAGE = "msg";
    String SESSION_TOKEN = "session_token";
    String LOGOUT = "logout";
    String LATITUDE = "latitude";
    String LONGITUDE = "longitude";


    String ADDRESS = "address";
    String HOURLY_CHARGES = "hourly_charges";
    String PASSWORD = "password";
    String PERSONAL_DESC = "personal_desc";
    String SERVICE_TYPE = "service_type";
    String STRIPE_ID = "stripe_id";
    String STRIPE_TOKEN = "stripe_token";
    String USER_LATITUDE = "user_latitude";
    String USER_LONGITUDE = "user_longitude";
    String USER_ADD_DATE = "user_add_date";
    String USER_MOD_DATE = "user_mod_date";
    String IMAGE_BASE_URL = "http://fairrepair.onsisdev.info/public/media/mechanic/";
    String CUSTOMER_IMAGE_BASE_URL = "http://fairrepair.onsisdev.info/public/media/customer/";
    String LANG_ENGLISH = "en";
    String LANGUAGE = "language" ;
    String PAGE_IDENTIFIER = "page_identifier";
    String ABOUT_MECH = "aboutusmechanic";
    String TNC_MECH = "termofservicesmechanic";
    String PRIVACY_POLICY_MECH = "privecypolicymechanic";
    String TEST_SELECT_TYPES = "test_select_type";
    String AVAILABLE = "1";
    String NOT_AVAILABLE = "0";

    String APP_STATUS = "app_status";
    String NOTIFICATION_DATA = "notification_data";

    String REQUEST_ID = "request_id";
    String APP_CUSTOMER_ID = "app_customer_id";
    String OFFER_PRICE = "offer_price";
    int NOTIFICATION_NEW_OFFER = 1;
    int NOTIFICATION_REQ_ACCEPTED = 2;
    int NOTIFICATION_OFFER_ACCEPTED = 3;
    int NOTIFICATION_REQ_COMPLETED = 5;
    String NOTIFICATION_TYPE = "notification_type";
    String BILLING_PRICE = "billing_price";
    String SERVICE_DETAIL = "service_detail";
    String APP_PROVIDER_ID = "app_provider_id";
    String CUSTOMER_ID = "customer_id";
    String MESSAGE = "message";
    String LOCATION = "location";
    String SERVICE_CHARGE = "service_charge";
    String TOTAL_PRICE = "total_price";
    String SERVICE_CHARGE_PRICE = "service_charge_price";
}
