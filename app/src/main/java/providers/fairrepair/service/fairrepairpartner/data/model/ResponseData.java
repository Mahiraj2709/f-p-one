
package providers.fairrepair.service.fairrepairpartner.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseData {

    @SerializedName("user_info")
    @Expose
    private UserInfo userInfo;
    @SerializedName("session_token")
    @Expose
    private String sessTok;
    @SerializedName("error")
    @Expose
    private Object error;

    @SerializedName("content")
    private String staticContent;

    @SerializedName("services")
    private List<Service> serviceList;

    //after customer finish the reqeust
    @SerializedName("service_percentage")
    private String service_percentage;

    //for bill has been generated for the use
    @SerializedName("billing_price")
    private String billing_price;

    //after customer finish the reqeust
    @SerializedName("service_charge")
    private String service_charge;

    //after customer finish the reqeust
    @SerializedName("total_price")
    private String total_price;
    /**
     * 
     * @return
     *     The userInfo
     */
    public UserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * 
     * @param userInfo
     *     The user_info
     */
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * 
     * @return
     *     The sessTok
     */
    public String getSessTok() {
        return sessTok;
    }

    /**
     * 
     * @param sessTok
     *     The sess_tok
     */
    public void setSessTok(String sessTok) {
        this.sessTok = sessTok;
    }

    /**
     * 
     * @return
     *     The error
     */
    public Object getError() {
        return error;
    }

    /**
     * 
     * @param error
     *     The error
     */
    public void setError(Object error) {
        this.error = error;
    }


    public String getStaticContent() {
        return staticContent;
    }

    public void setStaticContent(String staticContent) {
        this.staticContent = staticContent;
    }

    public List<Service> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<Service> serviceList) {
        this.serviceList = serviceList;
    }

    public String getService_percentage() {
        return service_percentage;
    }

    public void setService_percentage(String service_percentage) {
        this.service_percentage = service_percentage;
    }

    public String getBilling_price() {
        return billing_price;
    }

    public void setBilling_price(String billing_price) {
        this.billing_price = billing_price;
    }

    public String getService_charge() {
        return service_charge;
    }

    public void setService_charge(String service_charge) {
        this.service_charge = service_charge;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }
}
