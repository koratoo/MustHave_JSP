package model1.board;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletContext;

import common.JDBConnect;

public class BoardDAO extends JDBConnect{

	public  BoardDAO(ServletContext application) {
		super(application);
	}
	
	//검색 조건에 맞는 게시물 수를 얻어오는 메소드
	public int selectCount(Map<String, Object> map) {
		int totalCount = 0;
		
		String query = "SELECT COUNT(*) FROM board";
		if(map.get("serachWord") != null) {
			query += " WHERE " + map.get("searchField") + " "
				    	+ " LIKE '%" + map.get("searchWord") +"%'";
		}
		
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			rs.next();
			totalCount = rs.getInt(1);
		}
		catch(Exception e) {
			System.out.println("게시물 수 구하는 중 예외");
			e.printStackTrace();
		}
		
		return totalCount;
	}
	
	// 검색 조건에 맞는 게시물 목록을 반환합니다.
    public List<BoardDTO> selectList(Map<String, Object> map) { 
        List<BoardDTO> bbs = new Vector<BoardDTO>();  // 결과(게시물 목록)를 담을 변수

        String query = "SELECT * FROM board "; 
        if (map.get("searchWord") != null) {
            query += " WHERE " + map.get("searchField") + " "
                   + " LIKE '%" + map.get("searchWord") + "%' ";
        }
        query += " ORDER BY num DESC "; 

        try {
            stmt = con.createStatement();   // 쿼리문 생성
            rs = stmt.executeQuery(query);  // 쿼리 실행

            while (rs.next()) {  // 결과를 순화하며...
                // 한 행(게시물 하나)의 내용을 DTO에 저장
                BoardDTO dto = new BoardDTO(); 

                dto.setNum(rs.getString("num"));          // 일련번호
                dto.setTitle(rs.getString("title"));      // 제목
                dto.setContent(rs.getString("content"));  // 내용
                dto.setPostdate(rs.getDate("postdate"));  // 작성일
                dto.setId(rs.getString("id"));            // 작성자 아이디
                dto.setVisitcount(rs.getString("visitcount"));  // 조회수

                bbs.add(dto);  // 결과 목록에 저장
            }
        } 
        catch (Exception e) {
            System.out.println("게시물 조회 중 예외 발생");
            e.printStackTrace();
        }

        return bbs;
    }
    
    // 게시글 데이터를 받아 DB에 저장
    public int insertWrite(BoardDTO dto) {
    	int result = 0;
    	try {
    		String query = "INSERT INTO board ( "
    							  + " num,title,content,id,visitcount) "
    				              + " VALUES ( "
    							  + " seq_board_num.NEXTVAL, ? , ? , ? , 0)";
    		
    		psmt = con.prepareStatement(query);
    		psmt.setString(1, dto.getTitle());
    		psmt.setString(2, dto.getContent());
    		psmt.setString(3, dto.getId());
    		
    		result = psmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("게시물 입력 중 예외 발생");
			e.printStackTrace();
			// TODO: handle exception
		}
    	return result;
    }
    
    //지정한 게시물을 찾아 내용을 반환한다.
    public BoardDTO selectView(String num) {
    	BoardDTO dto = new BoardDTO();
    	
    	String query = "SELECT B.*, M.name"
    			              + " FROM member M INNER JOIN board B "
    			              + " ON M.id=B.id "
    			              + " WHERE num=?";
    	
    	try {
    		psmt = con.prepareStatement(query);
    		psmt.setString(1,  num);
    		rs = psmt.executeQuery();
    		
    		//결과
    		if(rs.next()) {
    			dto.setNum(rs.getString(1));
    			dto.setTitle(rs.getString(2));
    			dto.setContent(rs.getString(3));
    			dto.setId(rs.getString(4));
    			dto.setPostdate(rs.getDate(5));
    			dto.setVisitcount(rs.getString(6));
    			dto.setName(rs.getString(7));
    		}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    	return dto;
    }
    
    //조회수 1씩 증가
    public void updateVisitCount(String num) {
    	String query = "UPDATE board SET "
    			   			  + " visitcount=visitcount+1 "
    			   			  + " WHERE num=?";
    	try {
			psmt = con.prepareStatement(query);
			psmt.setString(1, num);
			psmt.executeQuery();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    		
    }
    
    
    //지정한 게시물 수정
    public int updateEdit(BoardDTO dto) {
    	int result =0;
    	
    	try {
    		String query = "UPDATE board SET "
    							  + " title=? , content =? "
    				              + " WHERE num=?";
			
    		psmt = con.prepareStatement(query);
    		psmt.setString(1, dto.getTitle());
    		psmt.setString(2, dto.getContent());
    		psmt.setString(3, dto.getNum());
    		
    		result = psmt.executeUpdate();
    		
    		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    	return result;
    }

    public int deletePost(BoardDTO dto) {
    	int result = 0;
    	try {
			String query = "DELETE FROM board WHERE num=?";
			
			psmt = con.prepareStatement(query);
			psmt.setString(1, dto.getNum());
			
			result = psmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("삭제 중 예외 발생");
			// TODO: handle exception
			e.printStackTrace();
		}
    	return result;
    }
}
