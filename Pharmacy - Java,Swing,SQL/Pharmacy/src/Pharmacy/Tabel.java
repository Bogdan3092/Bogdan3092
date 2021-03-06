package Pharmacy;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Tabel {
	private JFrame frmAfisareAngajati;
	private JTable table;
	private JFrame frame;

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
		frmAfisareAngajati.getContentPane().addContainerListener((ContainerListener) new ContainerAdapter() {
			public void componentAdded(ContainerEvent e) {
				try {
					showPharma();
				} catch (ClassNotFoundException e1) {
					
					e1.printStackTrace();
				} catch (SQLException e1) {
				
					e1.printStackTrace();
				}
			}
				public void showPharma() throws ClassNotFoundException, SQLException {
					Class.forName("com.mysql.cj.jdbc.Driver");
					Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pharma", "root", "root");
					Statement stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery("SELECT * FROM pharma.pharma;");
					 String columns[] = { "medicament", "cantitate", "pret" };
				     String data[][] = new String[10][10];
				      int i = 0;
				      while (rs.next()) {				        
				        String med = rs.getString("medicament");
				        String can = rs.getString("cantitate");
				        String pre = rs.getString("pret");
				        data[i][0] = med;
				        data[i][1] = can;
				        data[i][2] = pre;
				        
				        i++;
				      }
				      DefaultTableModel model = new DefaultTableModel(data, columns);
				      JTable table = new JTable(model);
				      table.setShowGrid(true);
				      table.setShowVerticalLines(true);
				      JScrollPane pane = new JScrollPane(table);
				      JFrame f = new JFrame("Afisare Stoc Pharma");
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