package com.hindu.cunow.PushNotification

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

public interface APIService {
    @Headers(
        "Content-Type:application/json",
        "Authorization:Key=AAAAPZoEYcc:APA91bHw0JgV7A9Vigtw9o8eFxFjUoXOMcdWiI1psHZERgEXiFklKt-EwfrraQPy8q6QJvSf9NqayPLlyrFeKxGOMF3tr1x7pFj__3w6dEP8WtaEdCKJ5SyaE6rGdsDfusnTZGWIk88N"
    )
    @POST("fcm/send")
    open fun sendNotification(@Body body:Sender?):Call<MyResponse?>


}