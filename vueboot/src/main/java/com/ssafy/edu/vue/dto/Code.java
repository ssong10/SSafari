package com.ssafy.edu.vue.dto;

import java.io.Serializable;

public class Code implements Serializable {
	private int id;
	private String title;
	private String body;
	private String code;
	private String created_at;
	private int memberid;
	private String username;
	private String lang;
	private int anonymous;
	private int likes;
	private int comments;
	public Code() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Code(int id, String title, String body, String code, String created_at, int memberid, String lang,
			int anonymous) {
		super();
		this.id = id;
		this.title = title;
		this.body = body;
		this.code = code;
		this.created_at = created_at;
		this.memberid = memberid;
		this.lang = lang;
		this.anonymous = anonymous;
	}

	public Code(int id, String title, String body, String code, String created_at, int memberid, String username,
			String lang) {
		super();
		this.id = id;
		this.title = title;
		this.body = body;
		this.code = code;
		this.created_at = created_at;
		this.memberid = memberid;
		this.username = username;
		this.lang = lang;
	}

	public Code(int id, String title, String body, String code, String created_at, int memberid, String username,
			String lang, int anonymous, int likes, int comments) {
		super();
		this.id = id;
		this.title = title;
		this.body = body;
		this.code = code;
		this.created_at = created_at;
		this.memberid = memberid;
		this.username = username;
		this.lang = lang;
		this.anonymous = anonymous;
		this.likes = likes;
		this.comments = comments;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public int getMemberid() {
		return memberid;
	}
	public void setMemberid(int memberid) {
		this.memberid = memberid;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public int getAnonymous() {
		return anonymous;
	}
	public void setAnonymous(int anonymous) {
		this.anonymous = anonymous;
	}
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public int getComments() {
		return comments;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "Codereview [id=" + id + ", title=" + title + ", body=" + body + ", created_at=" + created_at
				+ ", memberid=" + memberid + ", lang=" + lang + ", anonymous=" + anonymous + "]";
	}
	
	
}
