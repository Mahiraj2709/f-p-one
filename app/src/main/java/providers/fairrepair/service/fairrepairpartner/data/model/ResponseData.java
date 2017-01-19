
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
}
