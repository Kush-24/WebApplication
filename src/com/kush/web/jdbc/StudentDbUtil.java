package com.kush.web.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class StudentDbUtil {
	
	private DataSource dataSource;
	Connection conn=null;
	Statement statement=null;
	ResultSet rs=null;
	
	public StudentDbUtil(DataSource dataSource) {
		this.dataSource=dataSource;
	}
	
	public List<Student> getStudents() throws Exception{
		List<Student> students=new ArrayList<>();
		
		try {
		// get the connection.
		conn=dataSource.getConnection();
		
		// create sql statement
		String query="select * from	student order by last_name ";
		statement=conn.createStatement();
		
		//excute query
		rs=statement.executeQuery(query);
		
		//process result set
		while(rs.next()) {
			
			//retrieve data from result set row	
			int id=rs.getInt("id");
			String fn=rs.getString("first_name");
			String ln=rs.getString("last_name");
			String email=rs.getString("email");
			
			// create new student object
			Student tempStudent=new Student(id, fn, ln, email);
			
			// add student in list of Student
			students.add(tempStudent);	
		}
		
		return students;
		
		}
		
		finally {
			// close jdbc connection. Prevent from memory leak , run of connection,statement and cursor.
		  close(conn,statement,rs);	
		}
	}

	public void addStudent(Student theStudent) throws Exception {
		
		try {
			// get the connection with db
			conn=dataSource.getConnection();
		
		// create sql to insert data
			String sql="insert into student (first_name,last_name,email) values (?,?,?) ";
			
		// set the param value for the student
			PreparedStatement ps=conn.prepareStatement(sql);
			ps.setString(1,theStudent.getFirstName());
			ps.setString(2,theStudent.getLastName());
			ps.setString(3,theStudent.getEmail());
			
		// execute ps
			ps.execute();
			
		} 
		finally {
			 close(conn,statement,null); // its not closing conn object it just keep it back to object pool.
		}
		
	}

	public Student getStudent(String studentId) throws Exception{
		
		Student studentObj=null;
		PreparedStatement ps = null;
		try {
			// convert studentId in int
			int id=Integer.parseInt(studentId);
			
			// get the connection with db
			conn=dataSource.getConnection();
			
			//create sql to get selected student 
			String sql="select * from student where id=?";
			
			// create prepared statement & set param
			 ps=conn.prepareStatement(sql);
			ps.setInt(1, id);
			
			// execute statement - return resultset
			rs=ps.executeQuery(); 
			
			// retrieve data from result set row
			if(rs.next()) {
			String fn=rs.getString("first_name");
			String ln=rs.getString("last_name");
			String email=rs.getString("email");
			
			// use the studentId during contruction
			studentObj=new Student(id,fn, ln, email);
			
			}else {
				throw new Exception("Couldn't find studentId"+id);
			}
					
			return studentObj;
		}
		finally {
			close(conn,ps,rs);
		}
	}

	public void updateStudent(Student updatedStudent) throws Exception {
		PreparedStatement ps=null;
		try {
		// make a connection with the db
		conn=dataSource.getConnection();
		
		// create sql to update data in db
		String sql="Update student set first_name=?,last_name=?,email=? where id=?";
		
		// create preparedstatement & set param
		 ps=conn.prepareStatement(sql);
		 ps.setString(1, updatedStudent.getFirstName());
		 ps.setString(2,updatedStudent.getLastName());
		 ps.setString(3, updatedStudent.getEmail());
		 ps.setInt(4, updatedStudent.getId());
	
		 ps.executeUpdate();
		}
		finally {
			close(conn,ps,null);
		}
	}

	public void deleteStudent(int id) throws SQLException {
		PreparedStatement ps=null;
		try {
		// make a conn with db
		conn=dataSource.getConnection();
		
		//make a sql query to delete student
		String sql="delete from student where id=?";
		
		// prepared statement & set param
		ps=conn.prepareStatement(sql);
		ps.setInt(1, id);
		
		// execute ps
		ps.executeUpdate();
		
		}
		finally {
			close(conn,ps,null);
		}
		
	}
	
	private void close(Connection conn, Statement statement, ResultSet rs){
		try {
			if (rs!= null) {
				rs.close();
			}
			
			if (statement != null) {
				statement.close();
			}
			
			if (conn != null) {
				conn.close();   // doesn't really close it ... just puts back in connection pool
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
    }

}
