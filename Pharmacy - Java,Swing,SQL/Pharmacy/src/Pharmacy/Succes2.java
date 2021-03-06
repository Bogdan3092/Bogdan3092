package Pharmacy;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import java.awt.Font;
import java.awt.SystemColor;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Succes2 {

	private JFrame frmSucces;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Succes2 window = new Succes2();
					window.frmSucces.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Succes2() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSucces = new JFrame();
		frmSucces.setTitle("Succes");
		frmSucces.getContentPane().setBackground(SystemColor.activeCaption);
		frmSucces.setBounds(100, 100, 450, 300);
		frmSucces.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmSucces.getContentPane().setLayout(null);
		
		JTextPane txtpnMedicamentulAFost = new JTextPane();
		txtpnMedicamentulAFost.setBackground(SystemColor.activeCaption);
		txtpnMedicamentulAFost.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txtpnMedicamentulAFost.setText("Medicamentul a fost gasit cu succes! Cantitatea valabila este: ");
		txtpnMedicamentulAFost.setBounds(39, 33, 371, 180);
		frmSucces.getContentPane().add(txtpnMedicamentulAFost);
		
		JButton btnNewButton = new JButton("OK");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmSucces.setVisible(false);
				}
		});
		btnNewButton.setBackground(Color.LIGHT_GRAY);
		btnNewButton.setBounds(321, 227, 89, 23);
		frmSucces.getContentPane().add(btnNewButton);
	}

	public void setVisible(boolean b) {
		frmSucces.setVisible(true);
	}

}




