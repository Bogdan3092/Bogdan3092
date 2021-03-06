package Employee;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.Color;

public class Tabel {

	private JFrame frmAfisareAngajati;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Tabel window = new Tabel();
					window.frmAfisareAngajati.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Tabel() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		frmAfisareAngajati = new JFrame();
		frmAfisareAngajati.getContentPane().setBackground(Color.GRAY);
		frmAfisareAngajati.getContentPane().addContainerListener(new ContainerAdapter() {
			@Override
			public void componentAdded(ContainerEvent e) {
				try {
					showDbEmployees();
				} catch (ClassNotFoundException e1) {
					
					e1.printStackTrace();
				} catch (SQLException e1) {
				
					e1.printStackTrace();
				}
			}
				public void showDbEmployees() throws ClassNotFoundException, SQLException {
					Class.forName("com.mysql.cj.jdbc.Driver");
					Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbemployee", "root", "root");
					Statement stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery("SELECT * FROM dbemployee.employee;");
					 String columns[] = { "Name", "Surname", "Salary", "JobTitle", "Departament", "HireDate" };
				     String data[][] = new String[10][10];
				      int i = 0;
				      while (rs.next()) {				        
				        String nom = rs.getString("name");
				        String sur = rs.getString("surname");
				        String sal = rs.getString("salary");
				        String job = rs.getString("jobtitle");
				        String dep = rs.getString("departament");
				        String dat = rs.getString("hiredate");
				        data[i][0] = nom;
				        data[i][1] = sur;
				        data[i][2] = sal;
				        data[i][3] = job;
				        data[i][4] = dep;
				        data[i][5] = dat;
				        i++;
				      }
				      DefaultTableModel model = new DefaultTableModel(data, columns);
				      JTable table = new JTable(model);
				      table.setShowGrid(true);
				      table.setShowVerticalLines(true);
				      JScrollPane pane = new JScrollPane(table);
				      JFrame f = new JFrame("Afisare Lista Angajati");
				      JPanel panel = new JPanel();
				      panel.add(pane);
				      f.add(panel);
				      f.setSize(500, 250);
				      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				      f.setVisible(true);
				    
				    ;};
				  });
		table = new JTable();
		table.setBounds(0, 244, 434, -243);
		frmAfisareAngajati.getContentPane().add(table);		
	}

	public void setVisible(boolean b) {
		// TODO Auto-generated method stub
		
	};
}		
			
		
		
			
		
		


