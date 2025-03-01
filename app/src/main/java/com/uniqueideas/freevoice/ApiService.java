package com.uniqueideas.freevoice;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/start-call") // Must match backend route!
    Call<ApiResponse> makeCall(@Body CallRequest request);
}
