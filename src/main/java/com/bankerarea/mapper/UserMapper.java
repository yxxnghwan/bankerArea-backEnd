package com.bankerarea.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.bankerarea.vo.UserVO;


@Mapper
public interface UserMapper {
	List <UserVO> getUserList();
	UserVO getUser(String id);
	UserVO signInUser(UserVO vo);
	void updateUser(UserVO vo);
	void insertUser(UserVO vo);
	void deleteUser(UserVO vo);
}
