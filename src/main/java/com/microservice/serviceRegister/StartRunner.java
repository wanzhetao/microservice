package com.microservice.serviceRegister;

import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Component
public class StartRunner implements CommandLineRunner {

	@Autowired
    private Environment env;

    @Override
    public void run(String... args) {

    	String kong_login_addr=env.getProperty("kong.loginaddr");
    	String kong_login_user=env.getProperty("kong.loginuser");
    	String kong_login_pass=env.getProperty("kong.loginpass");
    	String kong_registeraddr=env.getProperty("kong.registeraddr");
    	String token="";

    	if(kong_login_addr!=null && kong_login_user!=null && kong_login_pass!=null && kong_registeraddr!=null)
    	{
    		MultiValueMap<String, String> parlogins = new LinkedMultiValueMap<>();
        	parlogins.add("identifier", kong_login_user);
        	parlogins.add("password", kong_login_pass);
        	String res="";
        	res= HttpClient.sendPostRequest(kong_login_addr,parlogins);
        	if(res=="") return;
        	JSONObject obj=JSONObject.parseObject(res);
        	token=obj.get("token").toString();

        	String swagres=env.getProperty("swagger.resources");
        	String serhost=env.getProperty("server.address")==null?"127.0.0.1":env.getProperty("server.address");
        	String serport=env.getProperty("server.port")==null?"8080":env.getProperty("server.port");

        	if(swagres!=null)
        	{

                try {

                	MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                	HttpHeaders headers = new HttpHeaders();
                	res= HttpClient.sendGetRequest("http://"+serhost+":"+serport+swagres,params,headers);

                	JSONArray jsonArray= JSONArray.parseArray(res);

                	//进行POST的API列表
            		JSONArray postarray=new JSONArray();

                	for(int i=0;i<jsonArray.size();i++)
                	{
                		String ctrllocation=jsonArray.getJSONObject(i).getString("location");
                		res= HttpClient.sendGetRequest("http://"+serhost+":"+serport+ctrllocation,params,headers);
                		if(res=="") continue;
                		JSONObject ctrgroup=JSONObject.parseObject(res);
                		String swaghost=ctrgroup.getString("host");
                		String swagbasepath=ctrgroup.getString("basePath");
                		String definitions=ctrgroup.getString("definitions");
                		JSONArray swagtags=ctrgroup.getJSONArray("tags");
                		JSONObject swagpaths=ctrgroup.getJSONObject("paths");

                		for (Map.Entry<String, Object> entry : swagpaths.entrySet()) {


                			JSONObject postapi=JSONObject.parseObject(entry.getValue().toString());

                			for (Map.Entry<String, Object> entry1 : postapi.entrySet()) {

                				JSONObject postobj=new JSONObject();
                        		postobj.put("servicehost", "http://"+swaghost+swagbasepath);
                        		postobj.put("host", swaghost);
                        		postobj.put("basePath", swagbasepath);
                        		postobj.put("enabledFlag", "Y");
                        		postobj.put("tbbelongto", kong_login_user);
                        		postobj.put("definitions", definitions);
                        		postobj.put("serviceapi", entry.getKey());
                				postobj.put("servicemethod", entry1.getKey());

                				JSONObject postmethod=JSONObject.parseObject(entry1.getValue().toString());

                				if(postmethod.getJSONArray("tags").size()>0)
                				{
                					String posttag=getArraybykey(postmethod.getJSONArray("tags").get(0),swagtags);
                					postobj.put("servicetype", posttag);
                				}
                				postobj.put("servicename", postmethod.getString("summary"));
                				postobj.put("operationId", postmethod.getString("operationId"));
                				postobj.put("consumes", postmethod.getString("consumes"));
                				postobj.put("responses", postmethod.getString("responses"));
                				postobj.put("parameters", postmethod.getString("parameters"));
                				postarray.add(postobj);

                			}

                		}

                	}

                	if(postarray.size()>0)
            		{
            			res= HttpClient.sendPostRequestbyjson(kong_registeraddr+"?token="+token,postarray);
            			if(res=="") return;
            			System.out.println("Swagger标识的接口已经全部注册成功,请到网关查看");

            		}



                } catch (Exception e) {
                    System.out.println("Swagger接口调用异常");
                }

        	}


    	}







    }

    private String getArraybykey(Object v,JSONArray arr)
	{
		String tagvalue="";
		for(int i=0;i<arr.size();i++)
		{
			JSONObject obj=arr.getJSONObject(i);
			if(obj.containsValue(v)){
				tagvalue=obj.get("description").toString();
				break;
			}
		}

		return tagvalue;
	}
}
