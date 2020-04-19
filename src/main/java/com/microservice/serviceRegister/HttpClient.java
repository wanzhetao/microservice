package com.microservice.serviceRegister;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;

public class HttpClient {

    /**
     * 向目的URL发送post请求
     * @param url       目的url
     * @param params    发送的参数
     * @return  AdToutiaoJsonTokenData
     */
    public static String sendPostRequest(String url, MultiValueMap<String, String> params){
        String returnstr="";
    	try
        {
	    	RestTemplate client = new RestTemplate();
	        //新建Http头，add方法可以添加参数
	        HttpHeaders headers = new HttpHeaders();
	        //设置请求发送方式
	        HttpMethod method = HttpMethod.POST;
	        // 以表单的方式提交
	        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	        //将请求头部和参数合成一个请求
	        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
	        //执行HTTP请求，将返回的结构使用String 类格式化（可设置为对应返回值格式的类）
	        ResponseEntity<String> response = client.exchange(url, method, requestEntity,String .class);
	        if(response.getStatusCodeValue()==200)
	        {
	        	returnstr= response.getBody();
	        }
        }catch(Exception ex)
        {
        }
    	return returnstr;
    }

    /**
     * 向目的URL发送get请求
     * @param url       目的url
     * @param params    发送的参数
     * @param headers   发送的http头，可在外部设置好参数后传入
     * @return  String
     */
    public static String sendGetRequest(String url, MultiValueMap<String, String> params,HttpHeaders headers){
    	String returnstr="";
    	try
        {
	    	RestTemplate client = new RestTemplate();

	        HttpMethod method = HttpMethod.GET;
	        // 以表单的方式提交
	        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	        //将请求头部和参数合成一个请求
	        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
	        //执行HTTP请求，将返回的结构使用String 类格式化
	        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);

	        returnstr= response.getBody();

        }catch(Exception ex)
        {
        }
    	return returnstr;
    }

    /**
     * 向目的URL发送post请求
     * @param url       目的url
     * @param params    发送的参数
     * @return  AdToutiaoJsonTokenData
     */
    public static String sendPostRequestbyjson(String url, JSONArray arr){
    	String returnstr="";
    	try
        {
	    	RestTemplate client = new RestTemplate();
	        //新建Http头，add方法可以添加参数
	        HttpHeaders headers = new HttpHeaders();
	        //设置请求发送方式
	        HttpMethod method = HttpMethod.POST;
	        // 以JSON的方式提交
	        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
	        //将请求头部和JSON合成一个请求
	        HttpEntity requestEntity = new HttpEntity(arr, headers);
	        //执行HTTP请求，将返回的结构使用String 类格式化（可设置为对应返回值格式的类）
	        ResponseEntity<String> response = client.exchange(url, method, requestEntity,String.class);
	        if(response.getStatusCodeValue()==200)
	        {
	        	returnstr= response.getBody();
	        }
        }catch(Exception ex)
        {
        }
    	return returnstr;
    }
}
