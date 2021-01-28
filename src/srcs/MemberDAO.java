package srcs;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class MemberDAO {
	private Connection conn; //connection 인터페이스: sql 문장을 실행하기 전에 필요, statement 객체 생성 위해 필요 
	private PreparedStatement psmt; //DB와 연동하게 하는 인터페이스(쿼리 전송), SQL을 미리 컴파일해서 재사용 한다, 특정 sql 문장을 정의 및 실행 시킬 수 있음
	//각각의 인수에 대해 위치 홀더를 사용해서 sql 문장을 정의한다 위치 홀더란('?')로 표현되며, sql 문장이 실행되기 전 실제 값으로 대체됨
	private DataSource dataFactory; //커넥션 풀(웹 앱과 동시에 연동할 db와의 연결을 미리 설정해 놓는것)	
	
	
	
	public MemberDAO() { //memberDAO 객체 생성
		
		try {
			Context ctx=new InitialContext();
			//new InitialContext()는 웹 어플리케이션이 처음으로 배치시 설정된다 즉 자원 저장통 생성
			Context envContext=(Context) ctx.lookup("java:/comp/env");
			//모든 설정된 엔트리와 자원은 JNDI namespace의 java:comp/env 부분에 놓인다
			//java:/comp/env: JNDI 서비스에 접근하기 위한 기본 이름이다
			//lookup 메서드를 이용해서 저장된 자원을 찾으러 감 즉 등록된 naming서비스(context.xml)에서 자원을 찾고자 할 때 사용하는 메서드
			dataFactory=(DataSource) envContext.lookup("jdbc/oracle");
			//톰캣 context.xml에 설정한 name값인 jdbc/oracle을 이용해 톰캣이 미리 연결한 datasource를 받아온다
		} catch (NamingException e) {
			e.printStackTrace();
		}
		
	}
	
	public List<MemberVO> listMembers() { //vo클래스에 저장된 멤버 필드를 가져와서 저장하는 리스트(listMembers)
		List<MemberVO> list=new ArrayList<MemberVO>(); //리스트 생성
		
		try {
			conn=dataFactory.getConnection();//datasource를 이용해서 db에 연결한다
			String query="select * from t_member"; //db연결 후 query문 실행
			System.out.println("prepareStatement: "+query); //console창에 select문 돌렸다고 표시
			psmt=conn.prepareStatement(query);//connection인터페이스에서 제공하는 preparestatement 메서드에 sql문을 전달해서 preparestatement 객체 생성
			ResultSet rs=psmt.executeQuery();//preparestatment 인터페이스에서 제공하는 executeQuery()메서드를 호출해  미리 설정한 sql문 실행(select문 실행시 사용 ->ResultSet 객체 반환)
			//resultset이란 statement 또는 prestatement 객체로 select문을 사용해서 받아온 레코드 값을 테이블의 형태로 갖게 되는 객체
			
			while(rs.next()) { //rs객체에 저장된 값을 반복해서 저장(next()메서드:다음 레코드 행으로 이동)
				String id=rs.getString("id"); //rs에 저장된 String형 id라는 데이터를 가져와서 변수 id에 저장
				String pwd=rs.getString("pwd");//rs에 저장된 String형 pwd라는 데이터를 가져와서 변수 pwd에 저장
				String name=rs.getString("name");//rs에 저장된 String형 name라는 데이터를 가져와서 변수 name에 저장
				String email=rs.getString("email");//rs에 저장된 String형 email라는 데이터를 가져와서 변수 email에 저장
				Date joinDate=rs.getDate("joinDate");//rs에 저장된 String형 joinDate라는 데이터를 가져와서 변수 joinDate에 저장
				
				MemberVO vo=new MemberVO(); //각 칼럼 값을 다시 memberVO 객체의 속성에 설정한다
				vo.setId(id); //id값을 vo의 ID로 다시 설정
				vo.setPwd(pwd);
				vo.setName(name);
				vo.setEmail(email);
				vo.setJoinDate(joinDate);
				list.add(vo); //설정된 MemberVO객체를 다시 Arraylist에 저장한다
			}
			
			//resource를 줄이기 위해 close 메서드 이용해서 생성했던 객체들을 닫아준다(jdbc 이용시)
			rs.close();
			psmt.close();
			conn.close();
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list; //조회한 레코드의 개수만큼  MemberVO객체를 저장한 ArrayList를 반환
	}

	public void addMember(MemberVO memberVO) {
		try {
			conn=dataFactory.getConnection(); //Datasource를 이용해 데이터베이스와 연결한다
			String id=memberVO.getId(); // 테이블에 저장된 회원 정보를 받아온다(select문 실행시 vo에 새로 저장된)
			String pwd=memberVO.getPwd(); 
			String name=memberVO.getName();
			String email=memberVO.getEmail();
			
			String query="insert into t_member"; //insert문 문자열로 생성
			query+="(id,pwd,name,email)"; //테이블에 저장된 컬럼명
			query+="values(?,?,?,?)"; //query에 들어갈 값
			System.out.println("prepareStatement: "+query); 
			psmt=conn.prepareStatement(query); //connection인터페이스에서 제공하는 preparestatement 메서드에 sql문을 전달해서 preparestatement 객체 생성
			psmt.setString(1, id); //insert문의 각 '?'에 순서대로 회원 정보 세팅한다
			psmt.setString(2, pwd);
			psmt.setString(3, name);
			psmt.setString(4, email);
			psmt.executeUpdate(); //executeUpdate 메서드는 데이터베이스에서 데이터를 추가(Insert), 삭제(Delete), 수정(Update)하는 SQL 문을 실행

			psmt.close(); //jdbc 사용후 리소스 절약을 위해 닫기
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public void delMember(String id) {
		try {
			conn=dataFactory.getConnection(); //Datasource를 이용해 데이터베이스와 연결한다
			String query="delete from t_member" + " where id=?"; // delete문을 문자열로 만든다, id값만 적용되게 조건(where)
			System.out.println("prepareStatement:"+query);
			psmt=conn.prepareStatement(query); //connection인터페이스에서 제공하는 preparestatement 메서드에 sql문을 전달해서 preparestatement 객체 생성
			psmt.setString(1, id); // 첫 번째 ?에 전달된 id를 인자로 넣는다
			psmt.executeUpdate();//executeUpdate 메서드는 데이터베이스에서 데이터를 추가(Insert), 삭제(Delete), 수정(Update)하는 SQL 문을 실행
			psmt.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		
	}
	
}
