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
	private Connection conn;
	private PreparedStatement psmt;
	private DataSource dataFactory;
	
	public MemberDAO() {
		
		try {
			Context ctx=new InitialContext();
			Context envContext=(Context) ctx.lookup("java:/comp/env");
			dataFactory=(DataSource) envContext.lookup("jdbc/oracle");
		} catch (NamingException e) {
			e.printStackTrace();
		}
		
	}
	
	public List<MemberVO> listMembers() {
		List<MemberVO> list=new ArrayList<MemberVO>();
		
		try {
			conn=dataFactory.getConnection();
			String query="select * from t_member";
			System.out.println("prepareStatement: "+query);
			psmt=conn.prepareStatement(query);
			ResultSet rs=psmt.executeQuery();
			
			while(rs.next()) {
				String id=rs.getString("id");
				String pwd=rs.getString("pwd");
				String name=rs.getString("name");
				String email=rs.getString("email");
				Date joinDate=rs.getDate("joinDate");
				MemberVO vo=new MemberVO();
				vo.setId(id);
				vo.setPwd(pwd);
				vo.setName(name);
				vo.setEmail(email);
				vo.setJoinDate(joinDate);
				list.add(vo);
			}
			rs.close();
			psmt.close();
			conn.close();
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public void addMember(MemberVO memberVO) {
		try {
			conn=dataFactory.getConnection();
			String id=memberVO.getId();
			String pwd=memberVO.getPwd();
			String name=memberVO.getName();
			String email=memberVO.getEmail();
			
			String query="insert into t_member";
			query+="(id,pwd,name,email)";
			query+="values(?,?,?,?)";
			System.out.println("prepareStatement: "+query);
			psmt=conn.prepareStatement(query);
			psmt.setString(1, id);
			psmt.setString(2, pwd);
			psmt.setString(3, name);
			psmt.setString(4, email);
			psmt.executeUpdate();
			psmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public void delMember(String id) {
		try {
			conn=dataFactory.getConnection();
			String query="delete from t_member" + " where id=?";
			System.out.println("prepareStatement:"+query);
			psmt=conn.prepareStatement(query);
			psmt.setString(1, id);
			psmt.executeUpdate();
			psmt.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		
	}
	
}
