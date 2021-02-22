package com.project.wsjm.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.project.wsjm.service.BoardService;
import com.project.wsjm.vo.BoardVO;
import com.project.wsjm.vo.Criteria;
import com.project.wsjm.vo.MemberVO;
import com.project.wsjm.vo.Paging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@CrossOrigin(origins = "*")
@Controller
public class BoardController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BoardService boardService;


    //*******security
    // 어드민 페이지
    @GetMapping("/securityAdmin")
    public String dispAdmin() {
        return "/securityAdmin";
    }

    // 접근 거부 페이지
    @GetMapping("/securityDenied")
    public String dispDenied() {
        return "/securityDenied";
    }



    // JSP View Test
    @RequestMapping(value="/test")
    public String test(Model model) {
        model.addAttribute("test", "it's me");
        return "test";
    }


    // MYSQL test
    @RequestMapping(value="/now")
    public String now(Model model) throws Exception {

        model.addAttribute("now", boardService.now());
        return "now";
    }


    /** Login **/

    // login Form
    @RequestMapping(value="/auth")
    public String memberLogin() throws Exception {

        return "index";
    }

    // Login
    @RequestMapping(value="/userCheck", method={RequestMethod.GET, RequestMethod.POST})
    public ModelAndView userCheck(@RequestParam(required = false) String code, MemberVO memberVO,
                                  HttpServletRequest req, RedirectAttributes redirect) throws Exception {

        ModelAndView mav = new ModelAndView();

        // 로그인 처리

            String inputPass = memberVO.getMemberPass(); // 입력한 비밀번호
            MemberVO member = boardService.userCheck(memberVO); // 암호화된 DB비밀번호

            if(member != null) {
                HttpSession session = req.getSession();
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

                if(encoder.matches(inputPass, member.getMemberPass())) { // 암호화비교, 성공한 경우
                    session.setAttribute("member", member);

                    redirect.addAttribute("stateCode", 1); // redirect하면서 code를 넣어주는 방법
                    mav.setViewName("redirect:/boardList");
                } else { // 암호를 잘못 입력한 경우
                    mav.addObject("code", "matchesError");
                    mav.setViewName("userCheck");
                }
            } else { // 없는 아이디거나 빈공란
                mav.addObject("code", "nullError");
                mav.addObject("url", "login");
            }

        return mav;
    }

    @ResponseBody
    @RequestMapping(value="/idCheck", method=RequestMethod.POST)
    public int IdCheck(@RequestBody String memberId) throws Exception {

        logger.info("************idCheck");

        int count = 0;
        if(memberId != null) count = boardService.idCheck(memberId);

        return count;
    }


    /** board CRUD **/
    // 게시판 리스트 및 메인페이지
    @RequestMapping(value="/boardList")
    public String boardList(@RequestParam("stateCode") int stateCode, Criteria cri, Model model, MemberVO memberVO) throws Exception {

        // 전체 글 개수
        int boardListCnt = boardService.boardListCnt();

        // 페이징 객체
        Paging paging = new Paging();
        paging.setCri(cri);
        paging.setTotalCount(boardListCnt);

        List<Map<String, Object>> list = boardService.boardList(cri);
        model.addAttribute("list", list);
        model.addAttribute("paging", paging);
        model.addAttribute("stateCode", stateCode);

        return "boardList";
    }

    // 게시판 글쓰기 폼
    @RequestMapping(value="/boardWriteForm")
    public String boardWriteForm(HttpServletRequest req, Model model) throws Exception {

        HttpSession session = req.getSession();

        if(session.getAttribute("member") != null) {
            MemberVO member = (MemberVO) session.getAttribute("member"); // 로그인시 있던 세션
            MemberVO modifyMember = boardService.membermodifyGET(member.getMemberId());
            model.addAttribute("modifyId", modifyMember.getMemberId());
            model.addAttribute("stateCode", 1);

        } else if(session.getAttribute("userId") != null) {
            model.addAttribute("modifyId", session.getAttribute("userId"));
            model.addAttribute("stateCode", 2);
        }
        return "boardWriteForm";
    }


    // 게시판 글쓰기
    @RequestMapping(value="/boardWrite")
    public String boardWrite(@RequestParam("stateCode") int stateCode, BoardVO boardVO) throws Exception {

        boardService.boardWrite(boardVO);

        return "redirect:boardList?stateCode="+stateCode;
    }


    // 게시글 내용 읽기
    @RequestMapping(value="/boardRead")
    public String boardRead(@RequestParam("num") int num,
                            @RequestParam("stateCode") int stateCode, Model model,
                            HttpServletRequest req) throws Exception {

        BoardVO data = boardService.boardRead(num);
        model.addAttribute("data", data);
        model.addAttribute("stateCode", stateCode);

        return "boardRead";
    }


    // 게시글 수정폼
    @RequestMapping(value="/boardModifyForm")
    public String boardModifyForm(@RequestParam("num") int num,
                                  @RequestParam("stateCode") int stateCode,
                                  @RequestParam("writer") String writer,
                                  Model model) throws Exception {

        BoardVO data = boardService.boardRead(num);
        model.addAttribute("data", data);
        model.addAttribute("stateCode", stateCode);

        return "boardModifyForm";
    }


    // 게시글 수정
    @RequestMapping(value="/boardModify", method= RequestMethod.POST)
    public String boardModify(@RequestParam("stateCode") int stateCode, BoardVO boardVO, HttpServletRequest req) throws Exception {

        HttpSession session = req.getSession();

        if(session.getAttribute("userId") != null) {
            String userId = (String) session.getAttribute("userId");
            boardVO.setWriter(userId);
        } else if(session.getAttribute("member") != null) {
            MemberVO vo = (MemberVO) session.getAttribute("member");
            boardVO.setWriter(vo.getMemberId());
        }
        boardService.boardModify(boardVO);

        return "redirect:boardList?stateCode="+stateCode;
    }


    // 게시글 삭제
    @RequestMapping(value="/boardDelete")
    public ModelAndView boardDelete(@RequestParam("stateCode") int stateCode, @RequestParam("num") int num,
                                    @RequestParam("writer") String writer, HttpServletRequest req) throws Exception {

        HttpSession session = req.getSession();
        ModelAndView mav = new ModelAndView();

        if(session.getAttribute("member") != null) {
            logger.info("***session이 유지되는 경우");
            MemberVO member = (MemberVO)session.getAttribute("member");
            String memeberId = member.getMemberId();

            if(writer.equals(memeberId)) {
                boardService.boardDelete(num);
                mav.addObject("msg", "success");
            } else {
                mav.addObject("msg", "fail");
            }
            mav.setViewName("forward:boardList?stateCode="+stateCode);
        } else {
            logger.info("***session이 끝난 경우");
            mav.addObject("msg", "sessionFin");
            mav.setViewName("index");
        }
        return mav;
    }


    /** register **/
    @RequestMapping(value="/users", method=RequestMethod.GET)
    public void registerGet() throws Exception {

        logger.info("********GET register");
        // void 타입일 경우 접근하는 URL 경로에 해당하는 jsp를 찾아 실행한다.
    }


    @RequestMapping(value="/users", method=RequestMethod.POST)
    public String memberRegister(MemberVO memberVO) throws Exception {

        logger.info("***********register POST");

        int count = boardService.idCheck(memberVO.getMemberId());
        System.out.println(count);

        try {
            if(count == 0) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String encodePass = encoder.encode(memberVO.getMemberPass());
                memberVO.setMemberPass(encodePass);
                boardService.memberRegister(memberVO);
            }
        } catch (Exception e) {
            logger.info("*****존재 하는 아이디");
        }
        return "redirect:/";
    }
    // 가입시 다시한번 아이디의 갯수를 체크해주는 부분


    /* membeModify */
    // 회원정보 수정 화면 구현 : GET방식(회원정보 수정 페이지 진입시 해당 회원 정보를 새로운 세션과 연결하여 보여주는 역할)
    @RequestMapping(value="/memberModify", method=RequestMethod.GET)
    public String memberModifyGET(HttpServletRequest req, Model model, @RequestParam("stateCode") int stateCode) throws Exception {

        HttpSession session = req.getSession();

        MemberVO member = (MemberVO) session.getAttribute("member"); // 로그인시 있던 세션
        MemberVO modifyMember = boardService.membermodifyGET(member.getMemberId());

        model.addAttribute("modifyName", modifyMember.getMemberLanguage());
        model.addAttribute("modifyId", modifyMember.getMemberId());
        model.addAttribute("stateCode", stateCode);

        return "memberModify";
    }

    // 회원정보 수정 기능 구현 : POST방식 (회원정보 수정시 비동기 처리로 수정해주는 역할)
    @RequestMapping(value="/memberModify", method=RequestMethod.POST)
    public void memberModifyPOST(@RequestBody MemberVO memberVO) throws Exception {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodePass = encoder.encode(memberVO.getMemberPass());
        memberVO.setMemberPass(encodePass);
        boardService.memberModifyPOST(memberVO);
    }

    /** memberDelete **/
    @ResponseBody
    @RequestMapping(value="/memberDelete", method=RequestMethod.POST)
    public String memberDelete(@RequestBody MemberVO memberVO, Model model) throws Exception {

        String inputPass = memberVO.getMemberPass(); // 입력한 비밀번호
        MemberVO member = boardService.userCheck(memberVO); // 암호화된 DB비밀번호
        String result = "";

        if(memberVO.getMemberId() != null && memberVO.getMemberId() != "") {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            if(encoder.matches(inputPass, member.getMemberPass())) {
                boardService.memberDelete(memberVO);
                result = "success";
            }
        }
        return result;
    }

    /** logout **/
    @RequestMapping(value="/logout", method=RequestMethod.GET)
    public String logout() throws Exception {

        return "logout";
    }

}