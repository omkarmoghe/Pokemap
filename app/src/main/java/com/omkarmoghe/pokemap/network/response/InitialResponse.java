package com.omkarmoghe.pokemap.network.response;

import com.google.gson.annotations.SerializedName;

/**
 * Response for the initial call.
 * <p>
 * Created by fess on 21.07.16.
 */
public class InitialResponse {

    @SerializedName("lt")
    private String lt;

    @SerializedName("execution")
    private String execution;

    public String getLt() {
        return lt;
    }

    public void setLt(String lt) {
        this.lt = lt;
    }

    public String getExecution() {
        return execution;
    }

    public void setExecution(String execution) {
        this.execution = execution;
    }

}
