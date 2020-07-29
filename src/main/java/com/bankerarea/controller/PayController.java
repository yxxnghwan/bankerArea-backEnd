package com.bankerarea.controller;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bankerarea.config.KakaoPay;
import com.bankerarea.config.LoginManagementService;
import com.bankerarea.mapper.IdeaMapper;
import com.bankerarea.mapper.PurchaseMapper;
import com.bankerarea.vo.GoodsVO;
import com.bankerarea.vo.IdeaVO;
import com.bankerarea.vo.ProductVO;
import com.bankerarea.vo.PurchaseVO;

@CrossOrigin(origins = "", maxAge = 3600, allowCredentials="true")
@Controller
public class PayController {
    
    @Autowired
    private KakaoPay kakaopay;
    @Autowired
    private IdeaMapper ideaMapper;
    @Autowired
    private PurchaseMapper purchaseMapper;
       
    @GetMapping("/kakaoPay")
    public void kakaoPayGet() {
    	System.out.println("여기는 왔냐?");
    }
    
    @PostMapping("/purchase")
    public String kakaoPay(String purchaseList, HttpServletResponse response, HttpServletRequest request) throws Exception {
    	String user_id = LoginManagementService.signInCheck(request, response);
    	ProductVO product = new ProductVO();
    	if(user_id == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value()); //401
		} else {
			System.out.println(purchaseList);
	    	String[] purchaseList_ = purchaseList.split(",");
	    	
	    	List<GoodsVO> goodsList = new ArrayList<GoodsVO>();
	    	for(String goodsSeq : purchaseList_) {
	    		goodsList.add(ideaMapper.getGoods(Integer.parseInt(goodsSeq)));
	    	}
	    	IdeaVO idea = ideaMapper.getIdea(goodsList.get(0).getIdea_seq());
	    	
	    	System.out.println(idea);
	    	System.out.println(goodsList);
	    	
	    	product.setPartner_order_id(idea.getBanker_id());
	    	product.setPartner_user_id(user_id);
	    	
	    	String item_name = idea.getProject_name();
	    	int total_amount = 0;
	    	int quantity = goodsList.size();
	    	
	    	if(quantity == 1) {
	    		item_name += "(" + goodsList.get(0).getGoods_type() + ")";
	    		total_amount += goodsList.get(0).getPrice();
	    	} else {
	    		item_name += "(" + goodsList.get(0).getGoods_type() + "외 " + (quantity-1) + "개)";
	    		for(GoodsVO goods : goodsList) {
	    			total_amount += goods.getPrice();
	    		}
	    	}
	    	product.setItem_name(item_name);
	    	product.setQuantity(quantity + "");
	    	product.setTotal_amount(total_amount + "");
	    	
	    	System.out.println(product);
	    	response.setStatus(HttpStatus.OK.value()); //200
		}
    	
        return "redirect:" + kakaopay.kakaoPayReady(product);
    }
    
    @PostMapping("/kakaoPaySuccess")
    public void kakaoPaySuccess(HttpServletResponse response, HttpServletRequest request) throws Exception {
    	String user_id = LoginManagementService.signInCheck(request, response);
    	if(user_id == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value()); //401
		} else {
			Enumeration enumm = request.getParameterNames();
	    	String purchaseList_ = (String) enumm.nextElement();
	    	String[] purchaseList = purchaseList_.split(",");
	    	
	    	System.out.println("구매한거 ==> " + purchaseList);
	    	System.out.println("구매자 ==> " + user_id);
	        System.out.println("결제 성공(디비 저장하시오)");
	    	PurchaseVO pvo = new PurchaseVO();
	    	pvo.setBuyer_id(user_id);
	    	for(String goodsSeq : purchaseList) {
	    		pvo.setGoods_seq(Integer.parseInt(goodsSeq));
	    		purchaseMapper.insertPurchase(pvo);
	    	}
	    	response.setStatus(HttpStatus.OK.value()); //200
		}
    }
    
    @GetMapping("/kakaoPayCancel")
    public void kakaoPayCancel(@RequestParam("pg_token") String pg_token, Model model,
    		HttpServletResponse res) {
        System.out.println(pg_token);
        System.out.println("결제 취소");
        res.setStatus(HttpServletResponse.SC_OK);
    }
    
}
 
