package chat.login;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import chat.client.Chatroom;
import chat.function.CatBean;
import chat.util.Util;

public class Resign extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;
	private JLabel lblNewLabel;

	int mouseAtX = 0;// ����λ��
	int mouseAtY = 0;

	public Resign(final String getServerIP) {
		movePanel();
		setUndecorated(true);// ȡ��������
		setTitle("���û�ע��");
		setSize(531, 401);
		setLocationRelativeTo(null);
		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon("images\\regist_1.png").getImage(), 0, 0, getWidth(), getHeight(), null);
			}
		};
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		Font infoFont = new Font("΢���ź�", Font.PLAIN, 17);
		Font RegistFont = new Font("΢���ź�", Font.PLAIN, 15);

		textField = new JTextField();
		textField.setBorder(null);
		textField.setFont(infoFont);
		textField.setBounds(150, 211, 270, 30);
		textField.setOpaque(false);
		textField.setColumns(16);
		textField.setText("������ע����Ϣ");
		contentPane.add(textField);

		// �û������봦��
		textField.addFocusListener(new TextFocusHandle() {
			public void focusGained(FocusEvent e) {// ��ý���
				textField.setForeground(Color.BLACK);
				textField.setText("");
			}

			public void focusLost(FocusEvent e) {// ʧȥ����
				if (textField.getText().equals("")) {
					textField.setForeground(Color.LIGHT_GRAY);
					textField.setText("������ע����Ϣ");
				}
			}
		});

		passwordField = new JPasswordField();
		passwordField.setBorder(null);
		passwordField.setFont(infoFont);
		passwordField.setForeground(Color.BLACK);
		passwordField.setEchoChar('��');
		passwordField.setOpaque(false);
		passwordField.setBounds(150, 248, 270, 30);
		contentPane.add(passwordField);

		passwordField_1 = new JPasswordField();
		passwordField_1.setBorder(null);
		passwordField_1.setFont(infoFont);
		passwordField_1.setForeground(Color.BLACK);
		passwordField_1.setEchoChar('��');
		passwordField_1.setOpaque(false);
		passwordField_1.setBounds(150, 284, 270, 30);
		contentPane.add(passwordField_1);

		// ע�ᰴť
		final JButton buttonRegist = new JButton();
		buttonRegist.setIcon(new ImageIcon("images\\button_regist_1.png"));
		buttonRegist.setBounds(120, 330, 293, 41);
		getRootPane().setDefaultButton(buttonRegist);
		contentPane.add(buttonRegist);

		// �˳���ť��ʵ��
		final JLabel labeExit = new JLabel();
		labeExit.setIcon(new ImageIcon("images\\close.png"));
		labeExit.setBounds(495, 5, 30, 30);
		contentPane.add(labeExit);

		labeExit.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Login frame = new Login();
				frame.setVisible(true);
				setVisible(false);
			}

			public void mouseEntered(MouseEvent e) {
				labeExit.setIcon(new ImageIcon("images\\close_2.png"));
			}

			public void mouseExited(MouseEvent e) {
				labeExit.setIcon(new ImageIcon("images\\close.png"));
			}
		});

		// ��ʾ��Ϣ
		lblNewLabel = new JLabel();
		lblNewLabel.setBounds(30, 220, 185, 20);
		lblNewLabel.setFont(RegistFont);
		lblNewLabel.setForeground(Color.red);
		contentPane.add(lblNewLabel);

		// ע�ᰴť����
		buttonRegist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String u_name = textField.getText();
				String u_pwd = new String(passwordField.getPassword());
				String u_pwd_ag = new String(passwordField_1.getPassword());

				if (u_name.equals("") || u_name.equals("������ע����Ϣ")) {
					JOptionPane.showMessageDialog(null, "�û���Ϊ�գ�");
					return;
				}
				if (!u_pwd.equals(u_pwd_ag)) {
					JOptionPane.showMessageDialog(null, "���벻һ�£�");
					return;
				}
				if (u_pwd.length() == 0 || u_pwd_ag.length() == 0 ) {
					JOptionPane.showMessageDialog(null, "����Ϊ��");
					return;
				}
				
				Socket registSocket = null;
				try {
					registSocket = new Socket(getServerIP, 10086);
				} catch (IOException e2) {
					JOptionPane.showMessageDialog(null, "��������δ�򿪣�");
					return;
				}
				
				CatBean catBean = new CatBean();
				catBean.setName(u_name);
				catBean.setPassword(u_pwd);
				catBean.setType(5);
				
				try {
					ObjectOutputStream oos = new ObjectOutputStream(registSocket.getOutputStream());
					oos.writeObject(catBean);
					oos.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			
				
				ObjectInputStream ois = null;
				try {
					ois = new ObjectInputStream(registSocket.getInputStream());
					boolean loginResort = (boolean) ois.readObject();
					if(loginResort) {
						JOptionPane.showMessageDialog(null, "ע��ɹ�");
						Login frame = new Login();
						frame.setVisible(true);
						setVisible(false);
					}else {
						JOptionPane.showMessageDialog(null, "�Ѵ�����ͬ�û���");
					}
					
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (ClassNotFoundException e2) {
					e2.printStackTrace();
				}finally {
					try {
						registSocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

			}

		});
	}

	public void movePanel() {
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// ��ȡ������ʱ������
				mouseAtX = e.getPoint().x;
				mouseAtY = e.getPoint().y;
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				setLocation((e.getXOnScreen() - mouseAtX), (e.getYOnScreen() - mouseAtY));// ������ק�󣬴��ڵ�λ��
			}
		});
	}

}
