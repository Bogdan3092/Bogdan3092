package Pharmacy;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JTextPane;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Font;

public class PharmaApp {

	
	JFrame frmPharmaApp;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PharmaApp window = new PharmaApp();
					window.frmPharmaApp.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public PharmaApp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmPharmaApp = new JFrame();
		frmPharmaApp.getContentPane().setBackground(SystemColor.activeCaption);
		frmPharmaApp.setTitle("Pharma App");
		frmPharmaApp.setBounds(100, 100, 450, 300);
		frmPharmaApp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmPharmaApp.getContentPane().setLayout(null);
		
		JTextPane textPane = new JTextPane();
		textPane.setBounds(33, 65, 73, 20);
		frmPharmaApp.getContentPane().add(textPane);
		
		JTextPane textPane_3 = new JTextPane();
		textPane_3.setBounds(126, 65, 73, 20);
		frmPharmaApp.getContentPane().add(textPane_3);
		
		JTextPane textPane_4 = new JTextPane();
		textPane_4.setBounds(216, 65, 73, 20);
		frmPharmaApp.getContentPane().add(textPane_4);
		
		JButton btnNewButton = new JButton("Afisare tot Stocul");
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Tabel t = new Tabel();
				t.setVisible(true);
			}
		});
		btnNewButton.setBounds(309, 190, 115, 23);
		frmPharmaApp.getContentPane().add(btnNewButton);
		
		JTextPane textPane_6 = new JTextPane();
		textPane_6.setBounds(33, 141, 256, 20);
		frmPharmaApp.getContentPane().add(textPane_6);
			
		JButton btnCautareStoc = new JButton("Cautare in Stoc");
		btnCautareStoc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					searchPharma();
				} catch (ClassNotFoundException e1) {
					JOptionPane.showMessageDialog(null,"SQL Exception: "+ e.toString());
					e1.printStackTrace();
				} catch (SQLException e1) {
					  JOptionPane.showMessageDialog(null,"Class Not Found Exception: ");
					e1.printStackTrace();
				}
			}
			private void searchPharma() throws ClassNotFoundException, SQLException {
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pharma", "root", "root");
				String sql = "SELECT medicament FROM pharma WHERE medicament LIKE ?";
				PreparedStatement stmt = con.prepareStatement(sql);
				stmt.setString(1,textPane_6.getText());
				ResultSet rs = stmt.executeQuery();		
				while(rs.next()) {
					String numeGasit = rs.getString("medicament");
					Succes2 s = new Succes2();
					s.setVisible(true);
					System.out.println(numeGasit);
				}
				}
			
			public void cantitategasit() throws ClassNotFoundException, SQLException {
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pharma", "root", "root");
				String sql = "SELECT medicament FROM pharma WHERE medicament LIKE ?";
				PreparedStatement stmt = con.prepareStatement(sql);
				stmt.setString(1,textPane_6.getText());
				ResultSet rs = stmt.executeQuery();		
				while(rs.next()) {
					String cantitateGasit = rs.getString("cantitate");
					Succes2 s = new Succes2();
					s.setVisible(true);
					System.out.println(cantitateGasit);
				}
			}
		});
	
		
		
		btnCautareStoc.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnCautareStoc.setBounds(309, 138, 115, 23);
		frmPharmaApp.getContentPane().add(btnCautareStoc);
		
		JButton btnInserareStoc = new JButton("Inserare in Stoc");
		btnInserareStoc.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnInserareStoc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					insertPharma();
				} catch (ClassNotFoundException e1) {
					JOptionPane.showMessageDialog(null,"SQL Exception: "+ e.toString());
					e1.printStackTrace();
				} catch (SQLException e1) {
					  JOptionPane.showMessageDialog(null,"Class Not Found Exception: ");
					e1.printStackTrace();
				}
			}
				
				private void insertPharma() throws ClassNotFoundException, SQLException {
					Class.forName("com.mysql.cj.jdbc.Driver");
					Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pharma", "root", "root");
					Statement stmt = con.createStatement();
					stmt.executeUpdate("INSERT INTO `pharma`.`pharma` (`medicament`, `cantitate`, `pret`) "
							+ "VALUES ('" + textPane.getText() + "', '" + textPane_3.getText() + "', '" + textPane_4.getText() + "');");		
					con.close();
				
					Succes s = new Succes();
					s.setVisible(true);
			}
		});
		btnInserareStoc.setBounds(309, 38, 115, 23);
		frmPharmaApp.getContentPane().add(btnInserareStoc);
		
		JButton btnStergereStocStoc = new JButton("Stergere din Stoc");
		btnStergereStocStoc.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnStergereStocStoc.setBounds(309, 84, 115, 23);
		frmPharmaApp.getContentPane().add(btnStergereStocStoc);
		
	
		JTextPane txtpnMedicament = new JTextPane();
		txtpnMedicament.setBackground(SystemColor.activeCaption);
		txtpnMedicament.setEditable(false);
		txtpnMedicament.setText("Medicament");
		txtpnMedicament.setBounds(33, 34, 73, 20);
		frmPharmaApp.getContentPane().add(txtpnMedicament);
		
		JTextPane txtpnCantitate = new JTextPane();
		txtpnCantitate.setBackground(SystemColor.activeCaption);
		txtpnCantitate.setForeground(Color.BLACK);
		txtpnCantitate.setText("Cantitate");
		txtpnCantitate.setEditable(false);
		txtpnCantitate.setBounds(126, 34, 73, 20);
		frmPharmaApp.getContentPane().add(txtpnCantitate);
		
		JTextPane txtpnPret = new JTextPane();
		txtpnPret.setBackground(SystemColor.activeCaption);
		txtpnPret.setText("Pret");
		txtpnPret.setEditable(false);
		txtpnPret.setBounds(216, 34, 73, 20);
		frmPharmaApp.getContentPane().add(txtpnPret);
		
		
	}

}
