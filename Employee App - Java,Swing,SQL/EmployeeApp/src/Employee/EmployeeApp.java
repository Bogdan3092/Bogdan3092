package Employee;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class EmployeeApp {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		showDbEmployees();
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Please insert the name:");
		String name = sc.nextLine();
		System.out.println("Please insert the surname:");
		String surname = sc.nextLine();
		System.out.println("Please insert the salary:");
		double salary = sc.nextDouble();
		System.out.println("Please insert the jobtitle:");
		String jobtitle = sc.next();
		System.out.println("Please insert the departament:");
		String departament = sc.next();
		System.out.println("Please insert the hiredate:");
		String hiredate = sc.next();
		insertEmployee(name, surname, salary, jobtitle, departament, hiredate);
		showDbEmployees();
	}

	private static void insertEmployee(String name, String surname, double salary, String jobtitle, String departament, String hiredate) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbemployee", "root", "root");
		Statement stmt = con.createStatement();
		stmt.executeUpdate("INSERT INTO `dbemployee`.`employee` (`name`, `surname`, `salary`, `jobtitle`, `departament`, `hiredate`) "
				+ "VALUES ('" + name + "', '" + surname + "', '" + salary + "', '" + jobtitle + "' , '" + departament + "' , '" + hiredate + "');");		
		con.close();
	}
	
	
	private static void showDbEmployees() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbemployee", "root", "root");
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM dbemployee.employee;");
		
		while(rs.next()) {
			System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " 
					+ rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5) + " " 
					+ rs.getString(6) + " " + rs.getString(7) );
		}
		
		con.close();
	}

}


