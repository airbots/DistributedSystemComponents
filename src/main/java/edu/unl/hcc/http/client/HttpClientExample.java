package edu.unl.hcc.http.client;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.HttpClients;

public class HttpClientExample {

    public void connect(){
      try (CloseableHttpClient httpClient = HttpClients.createDefault()){

         HttpPost request = new HttpPost("http://yoururl");
         StringEntity params =new StringEntity("details={\"name\":\"myname\",\"age\":\"20\"} ");
         request.addHeader("content-type", "application/x-www-form-urlencoded");
         request.setEntity(params);
         CloseableHttpResponse response = httpClient.execute(request);

         //handle response here...

         }catch (Exception ex) {
            //handle exception here
         }
    }
}