package com.bankerarea.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.bankerarea.vo.LikeyVO;


@Mapper
public interface LikeyMapper {
	void insertLikey(LikeyVO vo);
	void deleteLikey(LikeyVO vo);
	Integer doYouLike(LikeyVO vo);
	List<LikeyVO> ideaLikeyList(int idea_seq);
}
