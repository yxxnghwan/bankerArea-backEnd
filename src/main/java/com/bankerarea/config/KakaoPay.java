package com.bankerarea.config;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.bankerarea.vo.KakaoPayReadyVO;
import com.bankerarea.vo.ProductVO;

import lombok.extern.java.Log;

@Service
public class KakaoPay {
	 
    private static final String HOST = "https://kapi.kakao.com";
    @Value("${kakao.api.key}")
    private String kakaoApiKey;
    
    private KakaoPayReadyVO kakaoPayReadyVO;
    
    public String kakaoPayReady(ProductVO product) {
 
        RestTemplate restTemplate = new RestTemplate();
 
        // 서버로 요청할 Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + kakaoApiKey);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
        
        // 서버로 요청할 Body
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("cid", "TC0ONETIME");
        params.add("partner_order_id", product.getPartner_order_id());
        params.add("partner_user_id", product.getPartner_user_id());
        params.add("item_name", product.getItem_name());
        params.add("quantity", product.getQuantity());
     // 가격이 100만원 이상이면 에러 0원이어도 에러
        params.add("total_amount", product.getTotal_amount()); 
        params.add("tax_free_amount", "100");
        params.add("approval_url", "http://localhost:3000/kakaoPaySuccess");
        params.add("cancel_url", "http://localhost:3000/kakaoPaySuccess");
        params.add("fail_url", "http://localhost:3000/kakaoPaySuccess");
 
        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<MultiValueMap<String, String>>(params, headers);
 
        try {
            kakaoPayReadyVO = restTemplate.postForObject(new URI(HOST + "/v1/payment/ready"), body, KakaoPayReadyVO.class);
            
            return kakaoPayReadyVO.getNext_redirect_pc_url();
        } catch (RestClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return "/pay";
    }
    
}
