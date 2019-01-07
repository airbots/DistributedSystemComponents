package edu.unl.hcc.http.client;

/*
import org.apache.commons.httpclient.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.HttpClients;

public class HttpClientExample {
}
    /*
    static {

        HttpClient client = HttpClients.createDefault(){
            try {
                HttpPost request = new HttpPost("http://yoururl");
                StringEntity params = new StringEntity("details={\"name\":\"myname\",\"age\":\"20\"} ");
                request.addHeader("content-type", "application/x-www-form-urlencoded");
                request.setEntity(params);
            } catch (Exception ex) {
                //handle exception here
            }
            ;
        }

    }
    public void connect(){
      try {
          CloseableHttpResponse response = httpClient.execute(request);

          //handle response here...
      } catch (Exception ex) {
          //handle exception here
      };
    }

    public void setProxy(String host, int port) {
        //client.
    }

    public static void main(String[] args) {
        HttpClientExample client = new HttpClientExample();
        client.setProxy(null, 8080);
        client.connect();
    }
}
*/