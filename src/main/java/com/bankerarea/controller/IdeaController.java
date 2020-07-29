package com.bankerarea.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankerarea.config.LoginManagementService;
import com.bankerarea.mapper.IdeaMapper;
import com.bankerarea.mapper.LikeyMapper;
import com.bankerarea.mapper.PurchaseMapper;
import com.bankerarea.vo.GoodsVO;
import com.bankerarea.vo.IdeaVO;
import com.bankerarea.vo.LikeyVO;
import com.bankerarea.vo.PurchaseVO;
import com.bankerarea.vo.SearchVO;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/idea")
public class IdeaController {
	
	@Autowired
	IdeaMapper ideaMapper;
	
	@Autowired
	PurchaseMapper purchaseMapper;
	
	@Autowired
	LikeyMapper likeyMapper;
	
	@GetMapping("/list")
		public List<IdeaVO> getIdeaList() {
		System.out.println("/idea/list ==> 아이디어 리스트 조회 처리");
		List<IdeaVO> ideaList = ideaMapper.getIdeaList();
		
		for(int i=0;i<ideaList.size();i++) {
			ideaList.get(i).setLikey_count(ideaMapper.getLikey_count(ideaList.get(i).getIdea_seq()));
			ideaList.get(i).setTotalPriceOfIdea(ideaMapper.getTotalPriceOfIdea(ideaList.get(i).getIdea_seq()));
		}
		return ideaList;
	}
	
	
	@GetMapping("/detail")
	public IdeaVO getIdea(int idea_seq, HttpServletResponse response, HttpServletRequest request) {
		System.out.println("/idea/detail ==> 아이디어 상세 조회 처리");
		
		// 아이디어 조회
		IdeaVO idea = ideaMapper.getIdea(idea_seq);
		ideaMapper.increaseReadCnt(idea_seq);
		// 굿즈 리스트 조회 및 배열로 변경
		idea.setLikey_count(ideaMapper.getLikey_count(idea_seq));
		idea.setLiked(false);
		List<GoodsVO> goodsList_ = ideaMapper.getGoodsList(idea_seq);
		GoodsVO[] goodsList = goodsList_.toArray(new GoodsVO[goodsList_.size()]);
		idea.setTotalPriceOfIdea(ideaMapper.getTotalPriceOfIdea(idea.getIdea_seq()));
		idea.setIdeaLikeyList(likeyMapper.ideaLikeyList(idea_seq));
		// 여기까진 공통 // 이제부턴 로그인된 사용자의 경우
		String id = LoginManagementService.signInCheck(request, response);
		if(id != null) { // 로그인된 사용자의 경우
			//1. 좋아요 여부 
			LikeyVO vo = new LikeyVO();
			vo.setIdea_seq(idea_seq);
			vo.setId(id);
			if(likeyMapper.doYouLike(vo) != null) {
				idea.setLiked(true);
			} 
			//2. 굿즈리스트 공개여부
			if(id.equals(idea.getBanker_id())) { // 등록자랑 같으면..
				for(int i = 0; i< goodsList.length; ++i) {
					goodsList[i].setOpen_status(true);
				}
			} else { // 로그인 됐는데 등록자가 아님
				List<PurchaseVO> purchaseList = purchaseMapper.getPurchaseList(id);
				for(PurchaseVO purchase : purchaseList) {
					for(int i = 0; i< goodsList.length; ++i) { 
						if(goodsList[i].getGoods_seq() == purchase.getGoods_seq()) { // 근데 샀어
							goodsList[i].setOpen_status(true);
						}
					}
				}
			}
			
			// 반전시키기 프론트랑 반대..
			for(int i = 0; i< goodsList.length; ++i) {
				goodsList[i].setOpen_status(!goodsList[i].isOpen_status());
			}
			
		}
		
		idea.setGoodsList(goodsList);
		return idea;
	}
	
	@PostMapping("/post")
	public void postIdea(@RequestBody IdeaVO vo, HttpServletResponse response, HttpServletRequest request) {
		System.out.println("등록");
		String banker_id = LoginManagementService.signInCheck(request, response);
		if(banker_id == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value()); //401
			System.out.println("실패");
		} else {
			vo.setBanker_id(banker_id);
			ideaMapper.insertIdea(vo);
			int current_seq = ideaMapper.getCurrentIdea_seq();
			vo.setIdea_seq(current_seq);
			for(GoodsVO goods : vo.getGoodsList()) {
				goods.setIdea_seq(vo.getIdea_seq());
				goods.setOpen_status(!goods.isOpen_status()); // 프론트랑 반대...
				ideaMapper.insertGoods(goods);
			}
			System.out.println(vo.toString());
			response.setStatus(HttpStatus.OK.value()); //200
		}
	}
	
	@PostMapping("/likey")
	public Map<String, Object> likey(HttpServletResponse response, HttpServletRequest request, @RequestBody LikeyVO vo) {
		System.out.println("라이키♡");
		List<LikeyVO> ideaLikeyList = null;
		Map<String, Object> likeyMap = new HashMap<String, Object>();
		
		String user_id = LoginManagementService.signInCheck(request, response);
		if(user_id == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value()); //401
		} else {
			vo.setId(user_id);
			if(likeyMapper.doYouLike(vo)==null) {
				System.out.println("라이키 추가");
				likeyMapper.insertLikey(vo);
				ideaLikeyList = likeyMapper.ideaLikeyList(vo.getIdea_seq());
				likeyMap.put("ideaLikeyList", ideaLikeyList);
				likeyMap.put("addLikey", true);
			} else {
				System.out.println("라이키 취소");
				likeyMapper.deleteLikey(vo);
				ideaLikeyList = likeyMapper.ideaLikeyList(vo.getIdea_seq());
				likeyMap.put("ideaLikeyList", ideaLikeyList);
				likeyMap.put("addLikey", false);
			}
			response.setStatus(HttpStatus.OK.value()); //200
		}
		System.out.println("라이키 맵 : "+likeyMap);
		return likeyMap;
	}
	/*
	@PostMapping("/unlikey")
	public void unlikey(HttpServletResponse response, HttpServletRequest request, @RequestBody LikeyVO vo) {
		System.out.println("언라이키 T.T");
		String user_id = LoginManagementService.signInCheck(request, response);
		if(user_id == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value()); //401
		} else {
			vo.setId(user_id);
			likeyMapper.deleteLikey(vo);
			response.setStatus(HttpStatus.OK.value()); //200
		}
	}
	
	@PostMapping("/doYouLike") 
	public boolean doYouLike(HttpServletResponse response, HttpServletRequest request, @RequestBody LikeyVO vo) {
		Integer likey_seq = null;
		String user_id = LoginManagementService.signInCheck(request, response);
		if(user_id == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value()); //401
			return false;
		} else {
			vo.setId(user_id);
			likey_seq = likeyMapper.doYouLike(vo);
			if(likey_seq == null) {
				return false;
			} else {
				return true;
			}
		}
	}
	*/
	
	/*
	@PostMapping("/purchase")
	public void purchase(@RequestBody List<GoodsVO> goodsList, HttpServletResponse response, HttpServletRequest request) throws Exception {
		String user_id = LoginManagementService.signInCheck(request, response);
		if(user_id == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value()); //401
		} else {
			System.out.println(user_id + "<== 이 분이 구매하실 리스트 ==> " + goodsList.toString());
			for(GoodsVO goods : goodsList) {
				PurchaseVO pvo = new PurchaseVO();
				pvo.setGoods_seq(goods.getGoods_seq());
				pvo.setBuyer_id(user_id);
				purchaseMapper.insertPurchase(pvo);
			}
		}
	}*/
	
	@GetMapping("/list/likey")
	public List<IdeaVO> yourLikeyList(HttpServletResponse response, HttpServletRequest request) {
		String user_id = LoginManagementService.signInCheck(request, response);
		List<IdeaVO> yourLikeyList = null;
		if(user_id == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value()); //401
		} else {
			yourLikeyList = ideaMapper.getYourLikeyList(user_id);
			for(int i=0;i<yourLikeyList.size();i++) {
				yourLikeyList.get(i).setLikey_count(ideaMapper.getLikey_count(yourLikeyList.get(i).getIdea_seq()));
				yourLikeyList.get(i).setTotalPriceOfIdea(ideaMapper.getTotalPriceOfIdea(yourLikeyList.get(i).getIdea_seq()));
			}
		}
		return yourLikeyList;
	}
	@GetMapping("/list/purchase")
	public List<IdeaVO> yourPurchaseList(HttpServletResponse response, HttpServletRequest request) {
		String user_id = LoginManagementService.signInCheck(request, response);
		List<IdeaVO> yourPurchaseList = null;
		if(user_id == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value()); //401
		} else {
			yourPurchaseList = ideaMapper.getYourPurchaseList(user_id);
			for(int i=0;i<yourPurchaseList.size();i++) {
				yourPurchaseList.get(i).setLikey_count(ideaMapper.getLikey_count(yourPurchaseList.get(i).getIdea_seq()));
				yourPurchaseList.get(i).setTotalPriceOfIdea(ideaMapper.getTotalPriceOfIdea(yourPurchaseList.get(i).getIdea_seq()));
			}
		}
		return yourPurchaseList;
	}
	
	@GetMapping("/list/my")
	public List<IdeaVO> myIdeaList(HttpServletResponse response, HttpServletRequest request) {
		String user_id = LoginManagementService.signInCheck(request, response);
		List<IdeaVO> myIdeaList = null;
		if(user_id == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value()); //401
		} else {
			myIdeaList = ideaMapper.getMyIdeaList(user_id);
			for(int i=0;i<myIdeaList.size();i++) {
				myIdeaList.get(i).setLikey_count(ideaMapper.getLikey_count(myIdeaList.get(i).getIdea_seq()));
				myIdeaList.get(i).setTotalPriceOfIdea(ideaMapper.getTotalPriceOfIdea(myIdeaList.get(i).getIdea_seq()));
			}
		}
		return myIdeaList;
	}
	
	@GetMapping("/list/search")
	public List<IdeaVO> searchIdeaList(SearchVO vo) {
		System.out.println(vo.getType()+"검색");
		System.out.println("검색어 : " + vo.getKeyword());
		List<IdeaVO> searchList = ideaMapper.searchTypeIdeaList(vo);
		for(int i=0;i<searchList.size();i++) {
			searchList.get(i).setLikey_count(ideaMapper.getLikey_count(searchList.get(i).getIdea_seq()));
			searchList.get(i).setTotalPriceOfIdea(ideaMapper.getTotalPriceOfIdea(searchList.get(i).getIdea_seq()));
		}
		return searchList;
	}
	/* 활성화 여부 검토
	@PostMapping("/canIDelete")
	public void canIDelete(HttpServletResponse response, HttpServletRequest request, @RequestBody IdeaVO vo) {
		String user_id = LoginManagementService.signInCheck(request, response);
		Integer deleteCheck;
		if(user_id == null) {
			response.setStatus(HttpStatus.BAD_REQUEST.value()); //400
		} else {
			deleteCheck = purchaseMapper.isThisSoldIdea(vo.getIdea_seq());
			
			if(deleteCheck == null) { // 값이 없으면 아직 판매가 이루어지지 않은 아이디어
				response.setStatus(HttpStatus.OK.value()); //200
			} else { // 있으면 판매가 이루어진 아이디어
				response.setStatus(HttpStatus.FORBIDDEN.value()); //403
			}
		}
	}*/
	@DeleteMapping("/delete")
	public void delete(@RequestBody IdeaVO vo, HttpServletResponse response, HttpServletRequest request,
	@CookieValue(name="accessKey", defaultValue="unAuth") String accessKey) throws Exception {
		System.out.println("/idea/delete ==> 아이디어 삭제 처리");

		/* 사용자 권한 체크 */
		String user_id = LoginManagementService.signInCheck(request, response);
		if(user_id == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value()); //401
		} else {
			Integer idea_seq = purchaseMapper.isThisSoldIdea(vo.getIdea_seq());
			if(idea_seq != null) {
				// 값이 있으면 ( 지우면 안됨! )
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			} else {
				// 값이 없으면 ( 지우기! )
				ideaMapper.deleteIdea(vo.getIdea_seq());
			}
		}
	}
	
	@GetMapping("/list/likeyTop10")
	public List<IdeaVO> likeyTop10() {
		List<IdeaVO> likeyTop10List = ideaMapper.likeyTop10();
		for(int i = 0; i < likeyTop10List.size(); ++i) {
			likeyTop10List.get(i).setTotalPriceOfIdea(ideaMapper.getTotalPriceOfIdea(likeyTop10List.get(i).getIdea_seq()));
		}
		System.out.println("라이키탑텐로그\n"+likeyTop10List);
		return likeyTop10List;
	}
	
	@PatchMapping("/update")
	public void update(@RequestBody IdeaVO vo) {
		System.out.println("/idea/detail ==> 아이디어 수정 처리");
		vo.setUpdate_date(new Date(System.currentTimeMillis()));
		ideaMapper.updateIdea(vo);
		for(GoodsVO goods : vo.getGoodsList()) {
			ideaMapper.updateGoods(goods);
		}
		System.out.println(vo.toString());
	}
}
