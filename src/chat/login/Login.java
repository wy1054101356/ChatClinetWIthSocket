package chat.login;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;

import chat.client.Chatroom;
import chat.function.CatBean;
import chat.function.ClientBean;
import chat.util.Util;

public class Login extends JFrame {

	private static final long serialVersionUID = 1L;
	public JPanel contentPane;
	public JTextField textField;
	public JPasswordField passwordField;
	private Socket clientSocket;
	private static ObjectOutputStream oos;
	private static ObjectInputStream ois;
	public static HashMap<String, ClientBean> onlines;
	public String getServerIP = "127.0.0.1";
	int mouseAtX = 0;// ����λ��
	int mouseAtY = 0;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// �޸ķ�����Ϊ��ǰϵͳ����
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					// ������½����
					Login frame = new Login();// ��ʾ��¼����

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Login() {
		// ��panel ���в�������
		movePanel();// ���ƶ��Ĵ���
		setUndecorated(true);// ȡ��������
		setSize(531, 401);
		setTitle("��Ժ���Ⱥ�ĵ�¼");
		setLocationRelativeTo(null);
		setVisible(true);
		setIconImage(new ImageIcon("images//login_icon_2.png").getImage());// ����ͼ��

		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon("images\\login_2.jpg").getImage(), 0, 0, getWidth(), getHeight(), null);
			}
		};
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.setFocusable(true);// ʹpanelĬ�ϱ�����ѡ��

		Font infoFont = new Font("΢���ź�", Font.PLAIN, 20);
		Font RegistFont = new Font("΢���ź�", Font.PLAIN, 15);

		textField = new JTextField();
		textField.setForeground(Color.LIGHT_GRAY);
		textField.setBorder(null);
		textField.setFont(infoFont);
		textField.setBounds(150, 207, 270, 30);
		textField.setOpaque(false);
		textField.setColumns(16);
		textField.setText("�������û���");
		contentPane.add(textField);

		final JLabel textFieldChange1 = new JLabel();
		textFieldChange1.setIcon(new ImageIcon("images\\login_change_1.png"));
		textFieldChange1.setBounds(117, 210, 21, 24);
		textFieldChange1.setVisible(false);
		contentPane.add(textFieldChange1);

		// �û������봦��
		textField.addFocusListener(new TextFocusHandle() {
			public void focusGained(FocusEvent e) {// ��ý���{
				if (textField.getText().equals("�������û���")) {
					textField.setText("");
				}
				textField.setForeground(Color.BLACK);
				textFieldChange1.setVisible(true);
			}

			public void focusLost(FocusEvent e) {// ʧȥ����
				if (textField.getText().equals("")) {
					textField.setForeground(Color.LIGHT_GRAY);
					textField.setText("�������û���");
				}
				textFieldChange1.setVisible(false);
			}
		});

		// ������ʾ�ص���
		final JTextField passwordField1 = new JTextField();
		passwordField1.setBorder(null);
		passwordField1.setFont(infoFont);
		passwordField1.setForeground(Color.LIGHT_GRAY);
		passwordField1.setOpaque(false);
		passwordField1.setBounds(150, 255, 270, 30);
		passwordField1.setText("����������");
		contentPane.add(passwordField1);

		// ����ı�
		final JLabel textFieldChange2 = new JLabel();
		textFieldChange2.setIcon(new ImageIcon("images\\login_change_2.png"));
		textFieldChange2.setBounds(117, 260, 19, 20);
		textFieldChange2.setVisible(false);
		contentPane.add(textFieldChange2);

		// ��������
		passwordField = new JPasswordField();
		passwordField.setBorder(null);
		passwordField.setFont(infoFont);
		passwordField.setForeground(Color.BLACK);
		passwordField.setEchoChar('��');
		passwordField.setOpaque(false);
		passwordField.setBounds(150, 255, 270, 30);
		contentPane.add(passwordField);

		passwordField1.addFocusListener(new TextFocusHandle() {
			public void focusGained(FocusEvent e) {// ��ý���
				remove(passwordField1);
				passwordField.setFocusable(true);// ʹpanelĬ�ϱ�����ѡ��
			}
		});

		// ����򽹵㴦��
		passwordField.addFocusListener(new TextFocusHandle() {
			public void focusGained(FocusEvent e) {// ��ý���
				textFieldChange2.setVisible(true);
			}

			public void focusLost(FocusEvent e) {// ʧȥ����
				if (passwordField.getPassword().equals("")) {
					passwordField.setVisible(false);
					passwordField1.setVisible(true);
					passwordField1.setText("����������");
				}
				textFieldChange2.setVisible(false);
			}
		});

		// textField.setText("aaa");
		// passwordField.setText("123456");
		// ��¼��ť
		final JButton buttonLogin = new JButton();
		buttonLogin.setBorderPainted(false);
		buttonLogin.setIcon(new ImageIcon("images\\button_logon_1.jpg"));
		buttonLogin.setBounds(115, 300, 300, 41);
		getRootPane().setDefaultButton(buttonLogin);
		contentPane.add(buttonLogin);

		// ע�ᰴť
		final JLabel labelRegist = new JLabel();
		labelRegist.setFont(RegistFont);
		labelRegist.setText("ע�����û�");
		labelRegist.setForeground(Color.LIGHT_GRAY);
		labelRegist.setBounds(115, 350, 80, 30);
		contentPane.add(labelRegist);

		labelRegist.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				Resign frame = new Resign(getServerIP);
				frame.setVisible(true);// ��ʾע�����
				setVisible(false);// ���ص���½����
			}

			public void mouseEntered(MouseEvent e) {
				labelRegist.setForeground(Color.RED);
			}

			public void mouseExited(MouseEvent e) {
				labelRegist.setForeground(Color.LIGHT_GRAY);
			}
		});

		// ��С����ť��ʵ��
		final JLabel labeMinisize = new JLabel();
		labeMinisize.setIcon(new ImageIcon("images\\minimize.png"));
		labeMinisize.setBounds(460, 5, 30, 30);
		contentPane.add(labeMinisize);
		labeMinisize.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				setExtendedState(JFrame.ICONIFIED);
			}

			public void mouseEntered(MouseEvent e) {
				labeMinisize.setIcon(new ImageIcon("images\\minimize_2.png"));
			}

			public void mouseExited(MouseEvent e) {
				labeMinisize.setIcon(new ImageIcon("images\\minimize.png"));
			}
		});

		// ���÷�����ip��ť��ʵ��
		final JLabel labelOptionIP = new JLabel();
		labelOptionIP.setIcon(new ImageIcon("images\\login_option_1.png"));
		labelOptionIP.setBounds(420, 5, 30, 30);
		contentPane.add(labelOptionIP);
		labelOptionIP.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				getServerIP = JOptionPane.showInputDialog("�����������IP:");
			}

			public void mouseEntered(MouseEvent e) {
				labelOptionIP.setIcon(new ImageIcon("images\\login_option_2.png"));

			}

			public void mouseExited(MouseEvent e) {
				labelOptionIP.setIcon(new ImageIcon("images\\login_option_1.png"));

			}
		});

		// �˳���ť��ʵ��
		final JLabel labeExit = new JLabel();
		labeExit.setIcon(new ImageIcon("images\\close.png"));
		labeExit.setBounds(495, 5, 30, 30);
		contentPane.add(labeExit);

		labeExit.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int result = JOptionPane.showConfirmDialog(getContentPane(), "��ȷ���˳���", "��ʾ ",
						JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION)
					System.exit(0);
			}

			public void mouseEntered(MouseEvent e) {
				labeExit.setIcon(new ImageIcon("images\\close_2.png"));
			}

			public void mouseExited(MouseEvent e) {
				labeExit.setIcon(new ImageIcon("images\\close.png"));
			}
		});

		// ��½��ť
		buttonLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				InetAddress ip = null;// ����
				int localPort = 8520;
				String localIp = null;// ����IP
				try {
					ip = ip.getLocalHost();// ��ȡPC
					localIp = ip.getHostAddress();// ��ȡIP��ַ
				} catch (UnknownHostException e2) {
					e2.printStackTrace();
				}

				String userName = textField.getText();// �û���
				String u_pwd = new String(passwordField.getPassword());

				try {
					Socket client = new Socket(getServerIP, 10086);

					CatBean catBean = new CatBean();
					catBean.setType(4);
					catBean.setName(userName);
					catBean.setPassword(u_pwd);
					sendMessage(catBean, client);

					ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
					int loginResort = (int) ois.readObject();

					if (loginResort == 1) {
						Chatroom frame = new Chatroom(userName, client);
						frame.setVisible(true);// ��ʾ�������
						setVisible(false);// ���ص���½����
					}
					if (loginResort == 2) {
						JOptionPane.showMessageDialog(null, "û�д��û�");
					}
					if (loginResort == 3) {
						JOptionPane.showMessageDialog(null, "�������");
					}

				} catch (UnknownHostException e1) {
					JOptionPane.showMessageDialog(null, "��������δ�򿪣�");
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "��������δ�򿪣�");
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}

			}
		});

	}

	public void sendMessage(CatBean catBean, Socket socket) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(catBean);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	protected void errorTip(String str) {
		JOptionPane.showMessageDialog(contentPane, str, "Error Message", JOptionPane.ERROR_MESSAGE);
		textField.setText("");
		passwordField.setText("");
		textField.requestFocus();
	}

}
