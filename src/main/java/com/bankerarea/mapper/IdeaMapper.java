package com.bankerarea.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.bankerarea.vo.GoodsVO;
import com.bankerarea.vo.IdeaVO;
import com.bankerarea.vo.SearchVO;
import com.bankerarea.vo.UserVO;


@Mapper
public interface IdeaMapper {
	
	void insertIdea(IdeaVO vo);	// 아이디어추가
	
	void insertGoods(GoodsVO vo);
	
	int getCurrentIdea_seq();
		
	IdeaVO getIdea(int idea_seq);	// 아이디어 상세조회
	
	List<GoodsVO> getGoodsList(int idea_seq);
	
	void increaseReadCnt(int idea_seq);
	/*
	GoodsVO getMotivation(IdeaVO vo);
	GoodsVO getNeed(IdeaVO vo);
	GoodsVO getStrategy(IdeaVO vo);
	GoodsVO getMarket_analysis(IdeaVO vo);
	GoodsVO getCompetitiveness(IdeaVO vo);
	*/
	
	int getLikey_count(int idea_seq);
	
	void updateIdea(IdeaVO vo);	// 아이디어 수정
	void updateGoods(GoodsVO vo);
	/*
	void updateMotivation(GoodsVO vo);
	void updateNeed(GoodsVO vo);
	void updateStrategy(GoodsVO vo);
	void updateMarket_analysis(GoodsVO vo);
	void updateCompetitiveness(GoodsVO vo);
	*/
	
	

	List<IdeaVO> getIdeaList();	// 아이디어 리스트
	
	void deleteIdea(int idea_seq);	// 아이디어 삭제
	
	List<IdeaVO> getYourLikeyList(String id); //좋아요한 아이디어 리스트
	List<IdeaVO> getYourPurchaseList(String id); // 구매한 아이디어 리스트
	List<IdeaVO> getMyIdeaList(String id); // 내가 올린 아이디어 리스트
	List<IdeaVO> searchTypeIdeaList(SearchVO vo); // 타입별검색결과
	List<IdeaVO> likeyTop10();
	int getTotalPriceOfIdea(int idea_seq);
	
	GoodsVO getGoods(int goods_seq);
}
