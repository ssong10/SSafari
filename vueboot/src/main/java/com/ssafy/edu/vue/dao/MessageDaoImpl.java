package com.ssafy.edu.vue.dao;

import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.ssafy.edu.vue.dto.Message;

@Repository
public class MessageDaoImpl {

	String ns = "ssafy.message.";
	@Autowired
	private SqlSession sqlSession;
	
	public List<Message> getMessages(int memberid) {
		return sqlSession.selectList(ns+"getMessages",memberid);
	}
	
	public List<Message> getSendMessages(int memberid) {
		return sqlSession.selectList(ns+"getSendMessages",memberid);
	}

	public Message getMessage(int id) {
		return sqlSession.selectOne(ns+"getMessage",id);
	}

	public void addMessage(Message message) {
		sqlSession.insert(ns+"addMessage",message);
	}

	public void readMessage(int id) {
		sqlSession.update(ns+"readMessage",id);
	}

	public int checkUnread(int memberid) {
		return sqlSession.selectOne(ns+"checkUnread",memberid);
	}

}
