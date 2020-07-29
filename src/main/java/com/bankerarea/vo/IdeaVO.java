package com.bankerarea.vo;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class IdeaVO {
	private int idea_seq;
	private String project_name;
	private String short_description;
	private String category;
	private int read_count;
	private String banker_id;
	private GoodsVO[] goodsList;
	private int totalPriceOfIdea;
	private Date req_date;
	private Date update_date;
	private List<LikeyVO> ideaLikeyList;
	private boolean liked;
	/*
	private GoodsVO motivation;
	private GoodsVO need;
	private GoodsVO strategy;
	private GoodsVO market_analysis;
	private GoodsVO competitiveness;
	*/
	private int likey_count;
	public int getIdea_seq() {
		return idea_seq;
	}
	public void setIdea_seq(int idea_seq) {
		this.idea_seq = idea_seq;
	}
	public String getProject_name() {
		return project_name;
	}
	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}
	public String getShort_description() {
		return short_description;
	}
	public void setShort_description(String short_description) {
		this.short_description = short_description;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public int getRead_count() {
		return read_count;
	}
	public void setRead_count(int read_count) {
		this.read_count = read_count;
	}
	public String getBanker_id() {
		return banker_id;
	}
	public void setBanker_id(String banker_id) {
		this.banker_id = banker_id;
	}
	public GoodsVO[] getGoodsList() {
		return goodsList;
	}
	public void setGoodsList(GoodsVO[] goodsList) {
		this.goodsList = goodsList;
	}
	public int getLikey_count() {
		return likey_count;
	}
	public void setLikey_count(int likey_count) {
		this.likey_count = likey_count;
	}
	
	public int getTotalPriceOfIdea() {
		return totalPriceOfIdea;
	}
	public void setTotalPriceOfIdea(int totalPriceOfIdea) {
		this.totalPriceOfIdea = totalPriceOfIdea;
	}
	public Date getReq_date() {
		return req_date;
	}
	public void setReq_date(Date req_date) {
		this.req_date = req_date;
	}
	public Date getUpdate_date() {
		return update_date;
	}
	public void setUpdate_date(Date update_date) {
		this.update_date = update_date;
	}
	public List<LikeyVO> getIdeaLikeyList() {
		return ideaLikeyList;
	}
	public void setIdeaLikeyList(List<LikeyVO> ideaLikeyList) {
		this.ideaLikeyList = ideaLikeyList;
	}
	public boolean isLiked() {
		return liked;
	}
	public void setLiked(boolean liked) {
		this.liked = liked;
	}
	@Override
	public String toString() {
		return "IdeaVO [idea_seq=" + idea_seq + ", project_name=" + project_name + ", short_description="
				+ short_description + ", category=" + category + ", read_count=" + read_count + ", banker_id="
				+ banker_id + ", goodsList=" + Arrays.toString(goodsList) + ", totalPriceOfIdea=" + totalPriceOfIdea
				+ ", req_date=" + req_date + ", update_date=" + update_date + ", ideaLikeyList=" + ideaLikeyList
				+ ", liked=" + liked + ", likey_count=" + likey_count + "]";
	}
	
	
	
	
	
}
