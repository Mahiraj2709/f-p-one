
package providers.fairrepair.service.fairrepairpartner.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignInResponse {

    @SerializedName("response_msg")
    @Expose
    private String responseMsg;
    @SerializedName("response_key")
    @Expose
    private Integer responseKey;
    @SerializedName("response_status")
    @Expose
    private Integer responseStatus;
    @SerializedName("response_invalid")
    @Expose
    private Integer responseInvalid;
    @SerializedName("response_data")
    @Expose
    private ResponseData responseData;

    /**
     * 
     * @return
     *     The responseMsg
     */
    public String getResponseMsg() {
        return responseMsg;
    }

    /**
     * 
     * @param responseMsg
     *     The response_msg
     */
    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    /**
     * 
     * @return
     *     The responseKey
     */
    public Integer getResponseKey() {
        return responseKey;
    }

    /**
     * 
     * @param responseKey
     *     The response_key
     */
    public void setResponseKey(Integer responseKey) {
        this.responseKey = responseKey;
    }

    /**
     * 
     * @return
     *     The responseStatus
     */
    public Integer getResponseStatus() {
        return responseStatus;
    }

    /**
     * 
     * @param responseStatus
     *     The response_status
     */
    public void setResponseStatus(Integer responseStatus) {
        this.responseStatus = responseStatus;
    }

    /**
     * 
     * @return
     *     The responseInvalid
     */
    public Integer getResponseInvalid() {
        return responseInvalid;
    }

    /**
     * 
     * @param responseInvalid
     *     The response_invalid
     */
    public void setResponseInvalid(Integer responseInvalid) {
        this.responseInvalid = responseInvalid;
    }

    /**
     * 
     * @return
     *     The responseData
     */
    public ResponseData getResponseData() {
        return responseData;
    }

    /**
     * 
     * @param responseData
     *     The response_data
     */
    public void setResponseData(ResponseData responseData) {
        this.responseData = responseData;
    }

}
