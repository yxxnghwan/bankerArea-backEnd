package com.bankerarea.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.bankerarea.vo.GoodsVO;
import com.bankerarea.vo.IdeaVO;
import com.bankerarea.vo.LikeyVO;
import com.bankerarea.vo.PurchaseVO;


@Mapper
public interface PurchaseMapper {
	void insertPurchase(PurchaseVO vo);
	
	List<PurchaseVO> getPurchaseList(String buyer_id);
	
	Integer isThisSoldIdea(int idea_seq);
}
