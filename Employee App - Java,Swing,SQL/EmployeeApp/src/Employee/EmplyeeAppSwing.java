package Employee;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

import com.mysql.cj.xdevapi.Table;

import javax.swing.JTextPane;
import javax.swing.JComboBox;
import java.awt.Color;
import java.awt.Font;

public class EmplyeeAppSwing {

	private JFrame frmGestiuneAngajati;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private JButton btnNewButton_1;
	private JButton btnNewButton_2;
	private JLabel lblNewLabel_1;
	private JButton btnNewButton_3;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EmplyeeAppSwing window = new EmplyeeAppSwing();
					window.frmGestiuneAngajati.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public EmplyeeAppSwing() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmGestiuneAngajati = new JFrame();
		frmGestiuneAngajati.setTitle("Gestiune Angajati");
		frmGestiuneAngajati.setBounds(100, 100, 516, 334);
		frmGestiuneAngajati.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmGestiuneAngajati.getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("Afiseaza");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					showDbEmployees();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				}
			private void showDbEmployees() throws ClassNotFoundException, SQLException {
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
					
			
		});
		btnNewButton.setBounds(368, 235, 110, 23);
		frmGestiuneAngajati.getContentPane().add(btnNewButton);
		
		textField = new JTextField();
		textField.setHorizontalAlignment(SwingConstants.LEFT);
		textField.setBounds(128, 53, 150, 20);
		frmGestiuneAngajati.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Nume");
		lblNewLabel.setBounds(39, 56, 79, 14);
		frmGestiuneAngajati.getContentPane().add(lblNewLabel);
		
		JLabel lblPrenume = new JLabel("Prenume");
		lblPrenume.setBounds(39, 84, 79, 14);
		frmGestiuneAngajati.getContentPane().add(lblPrenume);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(128, 81, 150, 20);
		frmGestiuneAngajati.getContentPane().add(textField_1);
		
		JLabel lblSalariu = new JLabel("Salariu");
		lblSalariu.setBounds(39, 112, 79, 14);
		frmGestiuneAngajati.getContentPane().add(lblSalariu);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(128, 109, 150, 20);
		frmGestiuneAngajati.getContentPane().add(textField_2);
		
		JLabel lblNumeJob = new JLabel("Nume Job");
		lblNumeJob.setBounds(39, 140, 79, 14);
		frmGestiuneAngajati.getContentPane().add(lblNumeJob);
		
		textField_3 = new JTextField();
		textField_3.setColumns(10);
		textField_3.setBounds(128, 137, 150, 20);
		frmGestiuneAngajati.getContentPane().add(textField_3);
		
		JLabel lblDepartament = new JLabel("Departament");
		lblDepartament.setBounds(39, 168, 79, 14);
		frmGestiuneAngajati.getContentPane().add(lblDepartament);
		
		textField_4 = new JTextField();
		textField_4.setColumns(10);
		textField_4.setBounds(128, 165, 150, 20);
		frmGestiuneAngajati.getContentPane().add(textField_4);
		
		JLabel lblDataAngajarii = new JLabel("Data Angajarii");
		lblDataAngajarii.setBounds(39, 196, 79, 14);
		frmGestiuneAngajati.getContentPane().add(lblDataAngajarii);
		
		textField_5 = new JTextField();
		textField_5.setColumns(10);
		textField_5.setBounds(128, 193, 150, 20);
		frmGestiuneAngajati.getContentPane().add(textField_5);
		
		btnNewButton_2 = new JButton("Insereaza");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					insertEmployee();
				} catch (ClassNotFoundException e1) {
					JOptionPane.showMessageDialog(null,"SQL Exception: "+ e.toString());
					e1.printStackTrace();
				} catch (SQLException e1) {
					  JOptionPane.showMessageDialog(null,"Class Not Found Exception: "+ e.toString());
					e1.printStackTrace();
				}
			}
				
				private void insertEmployee() throws ClassNotFoundException, SQLException {
					Class.forName("com.mysql.cj.jdbc.Driver");
					Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbemployee", "root", "root");
					Statement stmt = con.createStatement();
					stmt.executeUpdate("INSERT INTO `dbemployee`.`employee` (`name`, `surname`, `salary`, `jobtitle`, `departament`, `hiredate`) "
							+ "VALUES ('" + textField.getText() + "', '" + textField_1.getText() + "', '" + textField_2.getText() + "', '" + textField_3.getText() + "' , '" + textField_4.getText() + "' , '" + textField_5.getText() + "');");		
					con.close();
			}
		});
		btnNewButton_2.setBounds(152, 235, 110, 23);
		frmGestiuneAngajati.getContentPane().add(btnNewButton_2);
		
		lblNewLabel_1 = new JLabel("Gestiune Angajati");
		lblNewLabel_1.setFont(new Font("Viner Hand ITC", Font.BOLD, 15));
		lblNewLabel_1.setForeground(Color.RED);
		lblNewLabel_1.setBounds(179, 11, 202, 31);
		frmGestiuneAngajati.getContentPane().add(lblNewLabel_1);
		
		btnNewButton_3 = new JButton("Afiseaza in alta casuta");
		btnNewButton_3.addActionListener(new ActionListener() {
			
			
			public void actionPerformed(ActionEvent e) {
				Tabel t = new Tabel();
				t.setVisible(true);
			}
			
			
			
		});
		btnNewButton_3.setBounds(368, 192, 110, 23);
		frmGestiuneAngajati.getContentPane().add(btnNewButton_3);
		
		
		
	}
}


