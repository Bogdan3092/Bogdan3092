package Pharmacy;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginApp1 {

	private JFrame frmLoginApp;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginApp1 window = new LoginApp1();
					window.frmLoginApp.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LoginApp1() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmLoginApp = new JFrame();
		frmLoginApp.getContentPane().setBackground(SystemColor.activeCaption);
		frmLoginApp.setTitle("LogIn App");
		frmLoginApp.setBounds(100, 100, 450, 300);
		frmLoginApp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLoginApp.getContentPane().setLayout(null);
		
		JTextPane txtpnUsername = new JTextPane();
		txtpnUsername.setEditable(false);
		txtpnUsername.setBackground(SystemColor.activeCaption);
		txtpnUsername.setText("Username");
		txtpnUsername.setBounds(31, 108, 62, 20);
		frmLoginApp.getContentPane().add(txtpnUsername);
		
		JTextPane txtpnPassword = new JTextPane();
		txtpnPassword.setBackground(SystemColor.activeCaption);
		txtpnPassword.setText("Password");
		txtpnPassword.setBounds(31, 158, 62, 20);
		frmLoginApp.getContentPane().add(txtpnPassword);
		
		JButton btnNewButton = new JButton("LogIn");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String user = passwordField.getText();
				String password = passwordField_1.getText();
				PharmaApp window = new PharmaApp();
				window.frmPharmaApp.setVisible(true);
			}
		});
		
		
		btnNewButton.setBounds(335, 227, 89, 23);
		frmLoginApp.getContentPane().add(btnNewButton);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(116, 108, 177, 20);
		frmLoginApp.getContentPane().add(passwordField);
		
		JTextPane txtpnPleaseEnterUsername = new JTextPane();
		txtpnPleaseEnterUsername.setFont(new Font("Verdana", Font.BOLD, 11));
		txtpnPleaseEnterUsername.setForeground(Color.BLUE);
		txtpnPleaseEnterUsername.setBackground(SystemColor.activeCaption);
		txtpnPleaseEnterUsername.setEditable(false);
		txtpnPleaseEnterUsername.setText("Please enter username and password:");
		txtpnPleaseEnterUsername.setBounds(91, 34, 269, 20);
		frmLoginApp.getContentPane().add(txtpnPleaseEnterUsername);
		
		passwordField_1 = new JPasswordField();
		passwordField_1.setBounds(116, 158, 177, 20);
		frmLoginApp.getContentPane().add(passwordField_1);
	}

private static void Loginpharma(String user, String pass) throws ClassNotFoundException, SQLException {
	Class.forName("com.mysql.cj.jdbc.Driver");
	Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pharma", "root", "root");
	Statement stmt = con.createStatement();
	ResultSet rs = stmt.executeQuery("SELECT * FROM pharma.login;");
	
	while(rs.next()) {
		String userFromlogin = rs.getString("user");
		String passFromlogin = rs.getString("password");
		if (user.equalsIgnoreCase(userFromlogin) && pass.equals(passFromlogin)) {
			System.out.println("Login OK");
			PharmaApp window = new PharmaApp();
			window.frmPharmaApp.setVisible(true);
						
		}		
	}
	con.close();
}

}