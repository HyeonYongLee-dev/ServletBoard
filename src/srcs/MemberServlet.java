package srcs;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/member3") //서블릿 매핑 이름
public class MemberServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//요청과 관련된 api:HttpServletRequest 클래스
	//웹 브라우저에서 전송한 정보를 톰캣 컨테이너가 HttpServletRequest 객체를 생성 후 doget,dopost,전달할 메서드 등으로 넘겨줌
	
	//응답과 관련된 api:HttpServletResponse 클래스
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request,response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request,response);
	}

	private void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8"); //전송된 데이터를 utf-8로 인코딩(한글 사용시), 즉 인코딩 설정
		response.setContentType("text/html;charset=utf-8"); //setContentType()을 이용해 응답할 데이터 종류가 html임을 설정
		
		MemberDAO dao=new MemberDAO(); //sql문을 사용할 memberDAO 객체 생성
		PrintWriter out=response.getWriter(); //HttpServletResponse 객체의 getWriter 메서드를 이용해서 출력 스트림인 printWriter객체를 받아옴
		String command=request.getParameter("command"); //수행할 요청을 받아오는 것
		
		if(command!=null&&command.equals("addMember")) { //회원 가팁창에서 전송된 name값인 command가 addMember이면 전송된 값들을 받아온다
			String _id=request.getParameter("id"); //request 클래스의 getParameter()를 이용해 <input>태그의 name속성 값으로 전송된 value를 가져옴
			String _pwd=request.getParameter("pwd");
			String _name=request.getParameter("name");
			String _email=request.getParameter("email");
			
			MemberVO vo=new MemberVO(); //회원 가입창에서 전송된 값들을 가져와서 vo에 저장한 후 sql문을 이용해서 전달한다
			vo.setId(_id);
			vo.setPwd(_pwd);
			vo.setName(_name);
			vo.setEmail(_email);
			
			dao.addMember(vo); //vo객체를 다시 arrayList에 저장한다
		
		}else if(command!=null&&command.equals("delMember")) //command값이 delMember이면, 
		{
			String id=request.getParameter("id"); // id를 가져와서 sql문으로 전달하여 삭제한다
			dao.delMember(id);//delMember 메서드에 id값을 전달해서삭제
		}
		List<MemberVO> list=dao.listMembers(); //listMembers() 메서드로 회원 정보를 조회한다
		out.print("<html><body>");
		out.print("<table border=1><tr align='center' bgcolor='blue'>");
		out.print("<td>아이디</td><td>비밀번호</td><td>이름</td><td>이메일</td><td>가입일</td><td>삭제</td></tr>");
		
		for(int i=0; i<list.size(); i++) { 
			MemberVO memberVO=(MemberVO)list.get(i); //조회한 회원 정보를 for문과 <tr> 태그를 이용해 리스트로 출력한다
			String id=memberVO.getId();
			String pwd=memberVO.getPwd();
			String name=memberVO.getName();
			String email=memberVO.getEmail();
			Date joinDate=memberVO.getJoinDate();
			
			out.print("<tr><td>"+id+"</td><td>"+pwd+"</td><td>"+name+"</td><td>"+email+"</td><td>"
					+joinDate+"</td><td>"+"<a href='/ServletBoard/member3?command=delMember&id="+id+"'>삭제</a></td></tr>");
															//삭제 클릭시 command값과 id를 서블릿으로 전송한다
		}
		out.print("</table></body></html>");
		out.print("<a href='/ServletBoard/memberForm.html'>새 회원 등록하기</a>");
		
	}
}
