package com.ssafy.edu.vue.controller;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ssafy.edu.vue.dto.AuthRequest;
import com.ssafy.edu.vue.dto.CheckSignUp;
import com.ssafy.edu.vue.dto.Facebook;
import com.ssafy.edu.vue.dto.LikePost;
import com.ssafy.edu.vue.dto.Member;
import com.ssafy.edu.vue.dto.Message;
import com.ssafy.edu.vue.dto.Portfolio;
import com.ssafy.edu.vue.help.BoolResult;
import com.ssafy.edu.vue.service.IJwtService;
import com.ssafy.edu.vue.service.IMemberService;
import com.ssafy.edu.vue.service.IMessageService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

//http://localhost:8197/humans/swagger-ui.html
@CrossOrigin(origins = { "*" }, maxAge = 6000, exposedHeaders = "access-token", allowedHeaders = "*")
@RestController
@RequestMapping("/api")
@Api(value = "SSAFY", description = "A5 Resouces Management 2020")
public class MemberController {

	public static final Logger logger = LoggerFactory.getLogger(MemberController.class);

	@Autowired
	private IMemberService memberservice;
	@Autowired
	private IJwtService jwtService;
	@Autowired
	private IMessageService messageservice;

	@ApiOperation(value = "member 전체 목록 보기", response = List.class)
	@RequestMapping(value = "/memberlist", method = RequestMethod.GET)
	public ResponseEntity<List<Member>> getMemberList() throws Exception {
		logger.info("1-------------getMemberList-----------------------------" + new Date());
		List<Member> members = memberservice.getMemberList();

		return new ResponseEntity<List<Member>>(members, HttpStatus.OK);
	}

	@ApiOperation(value = "member 내 정보", response = List.class)
	@RequestMapping(value = "/member/{memberid}", method = RequestMethod.GET)
	public ResponseEntity<Member> getMember(@PathVariable int memberid) throws Exception {
		logger.info("1-------------getMember-----------------------------" + new Date());
		Member member = memberservice.getMember(memberid);
		return new ResponseEntity<Member>(member, HttpStatus.OK);
	}

	@ApiOperation(value = "member 로그인", response = List.class)
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> login(@RequestBody Member member) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		logger.info("1-------------login-----------------------------" + new Date());
		int delflag = memberservice.checkDelflag(member.getEmail());
		HttpHeaders headers = new HttpHeaders();
		if(delflag==0) {
			Member login = memberservice.checkLogin(member);
			if (login == null) {
				return new ResponseEntity(HttpStatus.NO_CONTENT);
			}
			
			String token = jwtService.signin(login);
			
			headers.set("access-token", token);
			System.out.println(token);
			resultMap.put("status", true);
			resultMap.put("data", login);
		}else {
			resultMap.put("status", true);
			resultMap.put("delflag", delflag);
		}

		return new ResponseEntity<Map<String, Object>>(resultMap, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "member 회원가입 (eamil or username 중복 시 false)", response = List.class)
	@RequestMapping(value = "/member", method = RequestMethod.POST)
	public ResponseEntity<CheckSignUp> addMember(@RequestBody Member member) throws Exception {
		logger.info("1-------------addMember-----------------------------" + new Date());
		int email = memberservice.checkEmail(member.getEmail());
		int username = memberservice.checkUsername(member.getUsername());
		CheckSignUp result = new CheckSignUp();
		if (email == 0 && username == 0) {
			result.setSignup(true);
			result.setMessage("사용 가능");
			member.setSocial(0);
			memberservice.addMember(member);
		} else if (email >= 1 && username == 0) {
			result.setSignup(false);
			result.setMessage("이미 존재하는 email 입니다.");
		} else if (email == 0 && username >= 1) {
			result.setSignup(false);
			result.setMessage("이미 존재하는 username 입니다.");
		} else {
			result.setSignup(false);
			result.setMessage("이미 존재하는 email, username 입니다.");
		}
		return new ResponseEntity<CheckSignUp>(result, HttpStatus.OK);
	}

	@ApiOperation(value = "member 회원정보 수정", response = BoolResult.class)
	@RequestMapping(value = "/member", method = RequestMethod.PUT)
	public ResponseEntity<Map<String, Object>> updateMember(@RequestBody Member member, HttpServletRequest rs) throws Exception {
		logger.info("1-------------updateMember-----------------------------" + new Date());
		Map<String, Object> resultMap = new HashMap<>();
		logger.info("2------"+member);
//		int loginid = 0;
//		if (rs.getAttribute("loginMember") != null) {
//			Member login = (Member) rs.getAttribute("loginMember");
//			member.setMemberid(login.getMemberid());
//			memberservice.updateMember(member);
//			BoolResult nr = new BoolResult();
//			nr.setName("updateMember");
//			nr.setState("succ");
//			return new ResponseEntity<BoolResult>(nr, HttpStatus.OK);
//		}else {
//			return new ResponseEntity(HttpStatus.BAD_REQUEST);
//		}
		Member origin = memberservice.getMember(member.getMemberid());
		if(member.getUsername().equals("") || member.getUsername()==null) {
			member.setUsername(origin.getUsername());
		}else if(memberservice.checkUsername(member.getUsername())>0) {
			
			resultMap.put("state", "이미 존재하는 username 입니다.");
			return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK); 
		}
		if(member.getGithubid()==null || member.getGithubid().equals("") ) {
			member.setGithubid(origin.getGithubid());
		}
		if(member.getImg()==null || member.getImg().equals("") ) {
			member.setImg(origin.getImg());
		}
		memberservice.updateMember(member);
		Member login = memberservice.getMember(member.getMemberid());		
		String token = jwtService.signin(login);
		HttpHeaders headers = new HttpHeaders();
		headers.set("access-token", token);
		resultMap.put("state", "사용 가능");
		return new ResponseEntity<Map<String, Object>>(resultMap, headers, HttpStatus.OK);
		
	}
	
	@ApiOperation(value = "member 비밀번호 수정", response = BoolResult.class)
	@RequestMapping(value = "/member/password", method = RequestMethod.PUT)
	public ResponseEntity<BoolResult> updatePassword(@RequestBody Member member, HttpServletRequest rs) throws Exception {
		logger.info("1-------------updatePassword-----------------------------" + new Date());
		logger.info("2------"+member);
//		int loginid = 0;
//		if (rs.getAttribute("loginMember") != null) {
//			Member login = (Member) rs.getAttribute("loginMember");
//			member.setMemberid(login.getMemberid());
//			memberservice.updateMember(member);
//			BoolResult nr = new BoolResult();
//			nr.setName("updateMember");
//			nr.setState("succ");
//			return new ResponseEntity<BoolResult>(nr, HttpStatus.OK);
//		}else {
//			return new ResponseEntity(HttpStatus.BAD_REQUEST);
//		}

		memberservice.updatePassword(member);
		BoolResult nr = new BoolResult();
		nr.setName("updatePassword");
		nr.setState("succ");
		return new ResponseEntity<BoolResult>(nr, HttpStatus.OK);
		
	}

	@ApiOperation(value = "member 회원 탈퇴", response = BoolResult.class)
	@RequestMapping(value = "/member/{memberid}", method = RequestMethod.DELETE)
	public ResponseEntity<BoolResult> deleteMember(@PathVariable int memberid, HttpServletRequest rs) throws Exception {
		logger.info("1-------------deleteMember-----------------------------" + new Date());
		int loginid = 0;
		if (rs.getAttribute("loginMember") != null) {
			Member member = (Member) rs.getAttribute("loginMember");
			loginid = member.getMemberid();
		}

		logger.info("2-----"+loginid+"////"+memberid);
		if (loginid == memberid) {
			memberservice.deleteMember(memberid);
			BoolResult nr = new BoolResult();
			nr.setName("deleteMember");
			nr.setState("succ");
			return new ResponseEntity<BoolResult>(nr, HttpStatus.OK);
		} else {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@ApiOperation(value = "가입 회원 수 출력", response = BoolResult.class)
	@RequestMapping(value = "/member", method = RequestMethod.GET)
	public ResponseEntity<Integer> checkUsers() throws Exception {
		logger.info("1-------------checkUsers-----------------------------" + new Date());
		int cnt = memberservice.checkUsers();

		return new ResponseEntity<Integer>(cnt, HttpStatus.OK);
	}

	@ApiOperation(value = "member 회원 권한 수정", response = BoolResult.class)
	@RequestMapping(value = "/memberauth", method = RequestMethod.PUT)
	public ResponseEntity<BoolResult> updateMemberAuth(@RequestBody Member member) throws Exception {
		logger.info("1-------------updateMemberAuth-----------------------------" + new Date());
		memberservice.updateMemberAuth(member);
		BoolResult nr = new BoolResult();
		nr.setName("updateMemberAuth");
		nr.setState("succ");
		return new ResponseEntity<BoolResult>(nr, HttpStatus.OK);
	}

	@ApiOperation(value = "member 회원가입 email 중복 검사", response = BoolResult.class)
	@RequestMapping(value = "/member/email", method = RequestMethod.POST)
	public ResponseEntity<Integer> emailCheck(@RequestBody String email) throws Exception {
		logger.info("1-------------emailCheck-----------------------------" + new Date());
		int cnt = memberservice.checkEmail(email);

		return new ResponseEntity<Integer>(cnt, HttpStatus.OK);
	}

	@ApiOperation(value = "member 회원가입 username 중복 검사", response = BoolResult.class)
	@RequestMapping(value = "/member/username", method = RequestMethod.POST)
	public ResponseEntity<Integer> usernameCheck(@RequestBody String username) throws Exception {
		logger.info("1-------------emailCheck-----------------------------" + new Date());
		int cnt = memberservice.checkUsername(username);

		return new ResponseEntity<Integer>(cnt, HttpStatus.OK);
	}

	@ApiOperation(value = "member facebook 로그인", response = BoolResult.class)
	@RequestMapping(value = "/member/facebook", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> facebookLogin(@RequestBody Facebook member) throws Exception {
		logger.info("1-------------facebookLogin-----------------------------" + new Date());
		int email = memberservice.checkEmail(member.getEmail());
		if (email == 0) {
			memberservice.addMember(new Member(member.getEmail(), member.getId(), member.getName(), member.getName(),1));
		}

		Map<String, Object> resultMap = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();

		Member login = memberservice.checkLogin(new Member(member.getEmail(), member.getId()));
		logger.info("2-------------facebookLogin-----------------------------" + login);
		String token = jwtService.signin(login);
		logger.info("3-------------token-----------------------------" + token);
		headers.set("access-token", token);
		resultMap.put("status", true);
		resultMap.put("data", login);

		return new ResponseEntity<Map<String, Object>>(resultMap, headers, HttpStatus.OK);
	}
	
	@ApiOperation(value = "member ssafy 인증 요청", response = List.class)
	@RequestMapping(value = "/member/authrequest", method = RequestMethod.POST)
	public ResponseEntity<BoolResult> authRequest(@RequestBody AuthRequest request, HttpServletRequest rs) throws Exception {
		logger.info("1-------------authRequest-----------------------------" + new Date());
		logger.info("2-----------"+request);
		BoolResult nr=new BoolResult();
		int memberid = 0;
		if(rs.getAttribute("loginMember")!=null) {
			Member member = (Member) rs.getAttribute("loginMember");
			memberid = member.getMemberid();
		}else {
			nr.setName("로그인이 필요한 기능입니다.");
			return new ResponseEntity<BoolResult>(nr, HttpStatus.BAD_REQUEST);
		}
		request.setMemberid(memberid);
		memberservice.authRequest(request);
		memberservice.updateMemberAuth(new Member(memberid,3));
		
   		nr.setName("인증 요청");
   		nr.setState("succ");
		return new ResponseEntity<BoolResult>(nr, HttpStatus.OK);
	}
	
	@ApiOperation(value = "member ssafy 인증 승인", response = List.class)
	@RequestMapping(value = "/member/authrequest", method = RequestMethod.PUT)
	public ResponseEntity<BoolResult> checkAuthRequest(@RequestBody AuthRequest request, HttpServletRequest rs) throws Exception {
		logger.info("1-------------checkAuthRequest-----------------------------" + new Date());
		BoolResult nr=new BoolResult();
//		int loginid = 0;
//		if(rs.getAttribute("loginMember")!=null) {
//			Member member = (Member) rs.getAttribute("loginMember");
//			loginid = member.getMemberid();
//		}else {
//			nr.setName("로그인이 필요한 기능입니다.");
//			return new ResponseEntity<BoolResult>(nr, HttpStatus.BAD_REQUEST);
//		}
		logger.info("2-----------"+request);
		if(request.getFlag()==1) {
			memberservice.updateMemberAuth(new Member(request.getMemberid(),2));
			AuthRequest ar = memberservice.getAuthRequest(request.getMemberid());
			memberservice.updateLocationUnit(ar);
			messageservice.addMessage(new Message(1,request.getMemberid(),"SSAFY 인증 승인","SSAFY 인증 요청이 승인되었습니다."));
			nr.setName("인증 승인");
		}else {
			memberservice.updateMemberAuth(new Member(request.getMemberid(),4));
			messageservice.addMessage(new Message(1,request.getMemberid(),"SSAFY 인증 거절","SSAFY 인증 요청이 거절되었습니다."));
			nr.setName("인증 거절");
		}
		memberservice.deleteAuthRequest(request.getMemberid());
	
   		nr.setState("succ");
		return new ResponseEntity<BoolResult>(nr, HttpStatus.OK);
	}
	
	@ApiOperation(value = "member ssafy 인증 요청 전체 목록 보기", response = List.class)
	@RequestMapping(value = "/member/authrequest", method = RequestMethod.GET)
	public ResponseEntity<List<AuthRequest>> getAuthRequestList() throws Exception {
		logger.info("1-------------getAuthRequestList-----------------------------" + new Date());
		List<AuthRequest> members = memberservice.getAuthRequestList();

		return new ResponseEntity<List<AuthRequest>>(members, HttpStatus.OK);
	}
	
	@ApiOperation(value = "member 좋아요 누른 게시글", response = List.class)
	@RequestMapping(value = "/member/likepost", method = RequestMethod.GET)
	public ResponseEntity<List<LikePost>> getMemberLikePost(HttpServletRequest rs) throws Exception {
		logger.info("1-------------getMemberLikePost-----------------------------" + new Date());
		int memberid = 0;
		if(rs.getAttribute("loginMember")!=null) {
			Member member = (Member) rs.getAttribute("loginMember");
			memberid = member.getMemberid();
		}else {
			return new ResponseEntity<List<LikePost>>(HttpStatus.BAD_REQUEST);
		}
		List<LikePost> likepost = memberservice.getMemberLikePost(memberid);

		return new ResponseEntity<List<LikePost>>(likepost, HttpStatus.OK);
	}
}
