/**
 * 
 */
package process;

import java.io.StringReader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import pojo.Sentence;
import utils.SystemVar;

/**
 * @author zhangchangmin
 *
 */
public class DAO {

	private DataSource dataSource;
	public void init(){
		if(dataSource == null){
			BasicDataSource basicDataSource = new BasicDataSource();
			basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
			basicDataSource.setUrl(SystemVar.JDBCUrl);
			basicDataSource.setUsername(SystemVar.JDBCUser);
			basicDataSource.setPassword(SystemVar.JDBCPass);
			dataSource = basicDataSource;
		}
	}
	final String SQL_SELECT_BY_ID = "select * from last where id = ?";
	final String SQL_INSERT = "insert into last(id,date,sentences,termWeight,energy,summary) values(?,?,?,?,?,?)";
	final String SQL_INSERT_SENTENCE="insert into sentence(sentence_id, sentence_content, docName," +
			"eventName,isSummary,total) values (?,?,?,?,?,?)";
	final String SQL_TRUNCATE="truncate last";
	//删减表
	public void doTruncate(){
		init();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL_TRUNCATE);
			pstmt.execute();
		}catch(SQLException e){
			e.printStackTrace();
		}finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public void insert(Last last){
		init();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL_INSERT,Statement.RETURN_GENERATED_KEYS);
			pstmt.setInt(1, last.getId());
			pstmt.setString(2, last.getDate());
			pstmt.setClob(3, new StringReader(last.getSentences()));
			pstmt.setClob(4, new StringReader(last.getTermWeight()));
			pstmt.setClob(5, new StringReader(last.getEnergy()));
			pstmt.setClob(6, new StringReader(last.getSummary()));
			pstmt.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public void insertSentence(List<Sentence> sentences){
		init();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL_INSERT_SENTENCE,Statement.RETURN_GENERATED_KEYS);
			for(Sentence sentence: sentences){
				pstmt.setInt(1, sentence.getSentenceId());
				pstmt.setString(2, sentence.getSentenceContent());
				pstmt.setString(3, sentence.getDocName());
				pstmt.setString(4, sentence.getEventName());
				pstmt.setString(5, "false");
				pstmt.setInt(6, sentence.getTotal());
				pstmt.executeUpdate();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public Last selectByID(int id){
		init();
		Last last = new Last();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL_SELECT_BY_ID);
			pstmt.setInt(1, id);
			if (pstmt.execute()) {
				rs = pstmt.getResultSet();
				while (rs.next()) {
					String date=rs.getString(2);
					last.setDate(date);
					Clob c=rs.getClob(3);
					last.setSentences(c.getSubString((long)1, (int)c.length()));
					c=rs.getClob(4);
					last.setTermWeight(c.getSubString((long)1, (int)c.length()));
					c=rs.getClob(5);
					last.setEnergy(c.getSubString((long)1, (int)c.length()));
					c=rs.getClob(6);
					last.setSummary(c.getSubString((long)1, (int)c.length()));
//					return last;
				}
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return last;
	}
	
	String SQL_SELECT_EVENT_BY_ID="select pages from event where id = ?";
	public String selectEventById(int id){
		init();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String pages=null;
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL_SELECT_EVENT_BY_ID);
			pstmt.setInt(1, id);
			if (pstmt.execute()) {
				rs = pstmt.getResultSet();
				while (rs.next()) {
					pages=rs.getString(1);
				}
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return pages;
	}
	String SQL_SELECT_NEWS_BY_ID="select title,body,date from news where id = ?";
	public String[] selectNewsById(int id){
		init();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String[] content=new String[3];
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL_SELECT_NEWS_BY_ID);
			pstmt.setInt(1, id);
			if (pstmt.execute()) {
				rs = pstmt.getResultSet();
				while (rs.next()) {
					content[0]=rs.getString(1);
					content[1]=rs.getString(2);
					content[2]=rs.getString(3);
//					System.out.println(content[0]+content[1]+content[2]);
				}
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return content;
	}
	String SQL_SELECT_COUNT="select count(*) from sentence where docName = ?";
	String SQL_UPDATE_TOTAL="update sentence set total=? where docName=?";
	public int updateTotal(String docName){
		init();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
//		String[] content=new String[3];
		int count=0;
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL_SELECT_COUNT);
			pstmt.setString(1, docName);
			if (pstmt.execute()) {
				rs = pstmt.getResultSet();
				while (rs.next()) {
					count=rs.getInt(1);
//					System.out.println(content[0]+content[1]+content[2]);
				}
			}
			pstmt=conn.prepareStatement(SQL_UPDATE_TOTAL);
			pstmt.setInt(1, count);
			pstmt.setString(2, docName);
			pstmt.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}
	String SQL_SET_TRUE="update sentence set isSummary=? where sentence_content=?";
	public void setTrue(List<String> sl){
		init();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL_SET_TRUE);
			for(String s: sl){
				pstmt.setString(1, "true");
				pstmt.setString(2, s);
				pstmt.executeUpdate();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	String SQL_SELECT_ALL_DOCNAME="select distinct docName from sentence";
	String SQL_UPDATE_DOCNAME="update sentence set docName=? where docName=?";
	public void toright(){
		init();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<String> sl=new ArrayList<String>();
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL_SELECT_ALL_DOCNAME);
			if (pstmt.execute()) {
				rs = pstmt.getResultSet();
				while (rs.next()) {
					String s=rs.getString(1);
					sl.add(s);
				}
			}
			pstmt=conn.prepareStatement(SQL_UPDATE_DOCNAME);
			for(String s: sl){
				pstmt.setString(1, s.trim());
				pstmt.setString(2, s);
				pstmt.executeUpdate();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
