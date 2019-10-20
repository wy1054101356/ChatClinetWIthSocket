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
	int mouseAtX = 0;// 窗口位置
	int mouseAtY = 0;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// 修改风格界面为当前系统界面
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					// 启动登陆界面
					Login frame = new Login();// 显示登录界面

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Login() {
		// 对panel 进行参数设置
		movePanel();// 可移动的窗口
		setUndecorated(true);// 取消标题栏
		setSize(531, 401);
		setTitle("汽院软件群聊登录");
		setLocationRelativeTo(null);
		setVisible(true);
		setIconImage(new ImageIcon("images//login_icon_2.png").getImage());// 更改图标

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
		contentPane.setFocusable(true);// 使panel默认被焦点选中

		Font infoFont = new Font("微软雅黑", Font.PLAIN, 20);
		Font RegistFont = new Font("微软雅黑", Font.PLAIN, 15);

		textField = new JTextField();
		textField.setForeground(Color.LIGHT_GRAY);
		textField.setBorder(null);
		textField.setFont(infoFont);
		textField.setBounds(150, 207, 270, 30);
		textField.setOpaque(false);
		textField.setColumns(16);
		textField.setText("请输入用户名");
		contentPane.add(textField);

		final JLabel textFieldChange1 = new JLabel();
		textFieldChange1.setIcon(new ImageIcon("images\\login_change_1.png"));
		textFieldChange1.setBounds(117, 210, 21, 24);
		textFieldChange1.setVisible(false);
		contentPane.add(textFieldChange1);

		// 用户名输入处理
		textField.addFocusListener(new TextFocusHandle() {
			public void focusGained(FocusEvent e) {// 获得焦点{
				if (textField.getText().equals("请输入用户名")) {
					textField.setText("");
				}
				textField.setForeground(Color.BLACK);
				textFieldChange1.setVisible(true);
			}

			public void focusLost(FocusEvent e) {// 失去焦点
				if (textField.getText().equals("")) {
					textField.setForeground(Color.LIGHT_GRAY);
					textField.setText("请输入用户名");
				}
				textFieldChange1.setVisible(false);
			}
		});

		// 密码提示重叠层
		final JTextField passwordField1 = new JTextField();
		passwordField1.setBorder(null);
		passwordField1.setFont(infoFont);
		passwordField1.setForeground(Color.LIGHT_GRAY);
		passwordField1.setOpaque(false);
		passwordField1.setBounds(150, 255, 270, 30);
		passwordField1.setText("请输入密码");
		contentPane.add(passwordField1);

		// 密码改变
		final JLabel textFieldChange2 = new JLabel();
		textFieldChange2.setIcon(new ImageIcon("images\\login_change_2.png"));
		textFieldChange2.setBounds(117, 260, 19, 20);
		textFieldChange2.setVisible(false);
		contentPane.add(textFieldChange2);

		// 密码输入
		passwordField = new JPasswordField();
		passwordField.setBorder(null);
		passwordField.setFont(infoFont);
		passwordField.setForeground(Color.BLACK);
		passwordField.setEchoChar('●');
		passwordField.setOpaque(false);
		passwordField.setBounds(150, 255, 270, 30);
		contentPane.add(passwordField);

		passwordField1.addFocusListener(new TextFocusHandle() {
			public void focusGained(FocusEvent e) {// 获得焦点
				remove(passwordField1);
				passwordField.setFocusable(true);// 使panel默认被焦点选中
			}
		});

		// 输入框焦点处理
		passwordField.addFocusListener(new TextFocusHandle() {
			public void focusGained(FocusEvent e) {// 获得焦点
				textFieldChange2.setVisible(true);
			}

			public void focusLost(FocusEvent e) {// 失去焦点
				if (passwordField.getPassword().equals("")) {
					passwordField.setVisible(false);
					passwordField1.setVisible(true);
					passwordField1.setText("请输入密码");
				}
				textFieldChange2.setVisible(false);
			}
		});

		// textField.setText("aaa");
		// passwordField.setText("123456");
		// 登录按钮
		final JButton buttonLogin = new JButton();
		buttonLogin.setBorderPainted(false);
		buttonLogin.setIcon(new ImageIcon("images\\button_logon_1.jpg"));
		buttonLogin.setBounds(115, 300, 300, 41);
		getRootPane().setDefaultButton(buttonLogin);
		contentPane.add(buttonLogin);

		// 注册按钮
		final JLabel labelRegist = new JLabel();
		labelRegist.setFont(RegistFont);
		labelRegist.setText("注册新用户");
		labelRegist.setForeground(Color.LIGHT_GRAY);
		labelRegist.setBounds(115, 350, 80, 30);
		contentPane.add(labelRegist);

		labelRegist.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				Resign frame = new Resign(getServerIP);
				frame.setVisible(true);// 显示注册界面
				setVisible(false);// 隐藏掉登陆界面
			}

			public void mouseEntered(MouseEvent e) {
				labelRegist.setForeground(Color.RED);
			}

			public void mouseExited(MouseEvent e) {
				labelRegist.setForeground(Color.LIGHT_GRAY);
			}
		});

		// 最小化按钮的实现
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

		// 设置服务器ip按钮的实现
		final JLabel labelOptionIP = new JLabel();
		labelOptionIP.setIcon(new ImageIcon("images\\login_option_1.png"));
		labelOptionIP.setBounds(420, 5, 30, 30);
		contentPane.add(labelOptionIP);
		labelOptionIP.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				getServerIP = JOptionPane.showInputDialog("请输入服务器IP:");
			}

			public void mouseEntered(MouseEvent e) {
				labelOptionIP.setIcon(new ImageIcon("images\\login_option_2.png"));

			}

			public void mouseExited(MouseEvent e) {
				labelOptionIP.setIcon(new ImageIcon("images\\login_option_1.png"));

			}
		});

		// 退出按钮的实现
		final JLabel labeExit = new JLabel();
		labeExit.setIcon(new ImageIcon("images\\close.png"));
		labeExit.setBounds(495, 5, 30, 30);
		contentPane.add(labeExit);

		labeExit.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int result = JOptionPane.showConfirmDialog(getContentPane(), "您确定退出？", "提示 ",
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

		// 登陆按钮
		buttonLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				InetAddress ip = null;// 主机
				int localPort = 8520;
				String localIp = null;// 本机IP
				try {
					ip = ip.getLocalHost();// 获取PC
					localIp = ip.getHostAddress();// 获取IP地址
				} catch (UnknownHostException e2) {
					e2.printStackTrace();
				}

				String userName = textField.getText();// 用户名
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
						frame.setVisible(true);// 显示聊天界面
						setVisible(false);// 隐藏掉登陆界面
					}
					if (loginResort == 2) {
						JOptionPane.showMessageDialog(null, "没有此用户");
					}
					if (loginResort == 3) {
						JOptionPane.showMessageDialog(null, "密码错误");
					}

				} catch (UnknownHostException e1) {
					JOptionPane.showMessageDialog(null, "服务器尚未打开！");
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "服务器尚未打开！");
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
				// 获取点击鼠标时的坐标
				mouseAtX = e.getPoint().x;
				mouseAtY = e.getPoint().y;
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				setLocation((e.getXOnScreen() - mouseAtX), (e.getYOnScreen() - mouseAtY));// 设置拖拽后，窗口的位置
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
