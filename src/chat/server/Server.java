package chat.server;

import java.awt.AWTException;
import java.awt.Font;
import java.awt.Frame;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.omg.CORBA.INTERNAL;

import chat.client.Chatroom;
import chat.function.CatBean;
import chat.function.ClientBean;
import chat.util.Util;

public class Server {
	private static ServerSocket ss;
	private static Socket clientSocket;
	private static ObjectOutputStream oos;
	private static ObjectInputStream ois;
	public static HashMap<String, ClientBean> onlines;
	public JPanel contentPane;
	static JFrame mf = new JFrame();
	static boolean startFlag = false;
	public JButton buttonStart, buttonClose,buttonOpenFile;
	static ImageIcon trayImg1;
	static ImageIcon trayImg2;
	private static TrayIcon trayIcon = null;
	static SystemTray tray = SystemTray.getSystemTray();

	static {
		try {
			ss = new ServerSocket(10086);
			onlines = new HashMap<String, ClientBean>();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	//启动程序
	public void start() {



		mf.setTitle("服务端: 未启动");
		miniTray();
		mf.setSize(400, 250);
		mf.setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		mf.setContentPane(contentPane);
		contentPane.setLayout(null);
		mf.setIconImage(new ImageIcon("images//server_2.png").getImage());// 更改图标

		Font buttonFont = new Font("微软雅黑",Font.PLAIN,18);
		
		//打开信息目录
		buttonOpenFile = new JButton();
		buttonOpenFile.setText("打开用户记录目录");
		buttonOpenFile.setBounds(20, 130, 250, 40);
		buttonOpenFile.setFont(buttonFont);
		mf.getRootPane().setDefaultButton(buttonOpenFile);
		contentPane.add(buttonOpenFile);
		
		buttonOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				try {
					String[] cmd = new String[5]; String url = 
						"C:\\Users\\Administrator\\Desktop\\CatClient\\userInfo";
				cmd[0] = "cmd";
				cmd[1] = "/c";
				cmd[2] = "start";
				cmd[3] = " ";
				cmd[4] = url; Runtime.getRuntime().exec(cmd);
				} catch (IOException e1) {
				e1.printStackTrace();
				}
			}
		});
		
		
		//启动服务
		buttonStart = new JButton();
		buttonStart.setText("启动服务");
		buttonStart.setBounds(20, 70, 110, 40);
		buttonStart.setFont(buttonFont);
		mf.getRootPane().setDefaultButton(buttonStart);
		contentPane.add(buttonStart);

		buttonStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread connectThread = new Thread(new Runnable() {
					public void run() {
						try {
							while (true) {
								Socket client = ss.accept();
								new CatClientThread(client).start();
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				});
				connectThread.start();
				buttonStart.setEnabled(false);
				buttonClose.setEnabled(true);
				mf.setVisible(false);
				startFlag = true;
				tray.remove(trayIcon);
				miniTray();
			}
		});

		// 关闭服务
		buttonClose = new JButton();
		buttonClose.setText("关闭服务");
		mf.getRootPane().setDefaultButton(buttonClose);
		buttonClose.setBounds(160, 70, 110, 40);
		buttonClose.setFont(buttonFont);
		contentPane.add(buttonClose);
		buttonClose.setEnabled(false);
		buttonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonStart.setEnabled(true);
				buttonClose.setEnabled(false);
				System.exit(mf.NORMAL);
			}
		});

		
		//显示端口号和IP
		InetAddress ip = null;// 主机
		String localIp = "服务器地址:";// 本机IP
		try {
			ip = ip.getLocalHost();// 获取PC
			localIp += ip.getHostAddress();// 获取IP地址
		} catch (UnknownHostException e2) {
			e2.printStackTrace();
		}

		JLabel IPtext = new JLabel();
		IPtext.setBounds(20, 10, 300, 40);
		IPtext.setFont(new Font("微软雅黑", Font.PLAIN,16));
		IPtext.setText(localIp);
		contentPane.add(IPtext);
		
		String localport = "服务器端口:";// 本机IP
		int localPort = 10086;
		localport+=Integer.toString(localPort);
		
		JLabel IPtextport = new JLabel();
		IPtextport.setBounds(20, 30, 300, 40);
		IPtextport.setFont(new Font("微软雅黑", Font.PLAIN,16));
		IPtextport.setText(localport);
		contentPane.add(IPtextport);
		
		
		mf.addWindowListener(new WindowAdapter() { // 窗口关闭事件
			public void windowClosing(WindowEvent e) {
				System.exit(mf.NORMAL);
			};

			public void windowIconified(WindowEvent e) { // 窗口最小化事件
				mf.setVisible(false);
				miniTray();
			}

		});

		mf.setVisible(true);
	}

	// 窗口最小化到任务栏托盘
	private static void miniTray() { 

		trayImg1 = new ImageIcon("images//server_1.png");// 托盘图标
		trayImg2 = new ImageIcon("images//server_2.png");// 托盘图标
		mf.setIconImage(new ImageIcon("images//server_1.png").getImage());// 更改图标
		mf.setTitle("服务端: 已启动");
		if (startFlag) {
			
			InetAddress ip = null;// 主机
			int localPort = 10086;
			String localIp = "服务器状态: 启动\r\n";// 本机IP
			localIp +="服务器地址: ";
			try {
				ip = ip.getLocalHost();// 获取PC
				localIp += ip.getHostAddress();// 获取IP地址
			} catch (UnknownHostException e2) {
				e2.printStackTrace();
			}
			localIp +="\r\n服务器端口: ";
			localIp += Integer.toString(localPort);
			localIp +="\r\n";
			
			trayIcon = new TrayIcon(trayImg1.getImage(), localIp, new PopupMenu());
		}

		if (!startFlag) {
			InetAddress ip = null;// 主机
			int localPort = 10086;
			String localIp = "服务器状态: 停止\r\n";// 本机IP
			localIp +="服务器地址: ";
			try {
				ip = ip.getLocalHost();// 获取PC
				localIp += ip.getHostAddress();// 获取IP地址
			} catch (UnknownHostException e2) {
				e2.printStackTrace();
			}
			localIp +="\r\n服务器端口: ";
			localIp += Integer.toString(localPort);
			localIp +="\r\n";
			mf.setIconImage(new ImageIcon("images//server_2.png").getImage());// 更改图标
			mf.setTitle("服务端: 未启动");
			trayIcon = new TrayIcon(trayImg2.getImage(), localIp, new PopupMenu());
		}

		trayIcon.setImageAutoSize(true);
		trayIcon.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				if (e.getClickCount() == 1) {// 单击 1 双击 2

					tray.remove(trayIcon);
					mf.setVisible(true);
					mf.setExtendedState(JFrame.NORMAL);
					mf.toFront();
				}

			}

		});

		try {

			tray.add(trayIcon);

		} catch (AWTException e1) {
			e1.printStackTrace();
		}

	}

	class CatClientThread extends Thread {
		private Socket client;
		private CatBean bean;
		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		public CatClientThread(Socket client) {
			this.client = client;
		}

		@Override
		public void run() {
			try {
				// 不停的从客户端接收信息
				while (true) {
					// 读取从客户端接收到的catbean信息
								
					ois = new ObjectInputStream(client.getInputStream());
					bean = (CatBean) ois.readObject();
					
					// 分析catbean中，type是那样一种类型
					switch (bean.getType()) {
					// 上下线更新
					case 0: { // 上线
						// 记录上线客户的用户名和端口在clientbean中
						ClientBean cbean = new ClientBean();
						cbean.setName(bean.getName());
						cbean.setSocket(client);
						// 添加在线用户
						onlines.put(bean.getName(), cbean);
						// 创建服务器的catbean，并发送给客户端
						CatBean serverBean = new CatBean();
						serverBean.setType(0);
						serverBean.setInfo(bean.getTimer() + "[" + bean.getName() + "]上线了");
						// 通知所有客户有人上线
						HashSet<String> set = new HashSet<String>();
						// 客户昵称
						set.addAll(onlines.keySet());
						serverBean.setClients(set);
						sendAll(serverBean);
						break;
					}
					case -1: { // 下线
						// 创建服务器的catbean，并发送给客户端
						CatBean serverBean = new CatBean();
						serverBean.setType(-1);

						try {
							oos = new ObjectOutputStream(client.getOutputStream());
							oos.writeObject(serverBean);
							oos.flush();
						} catch (IOException e) {
							e.printStackTrace();
						}

						//写入文件信息
						
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String fileName ="userInfo\\";
						fileName +=bean.getName();
						fileName+="_chatInfo.txt";
						File fileInput = new File(fileName);
						
						if (!fileInput.exists()) 
							fileInput.createNewFile();
						FileWriter fileWritter = new FileWriter(fileName, true);
						fileWritter.write("用户 ["+bean.getName()+"] "+
								sdf.format(new Date())+
								" 下线\r\n");
						fileWritter.close(); 	
						
						onlines.remove(bean.getName());

						// 向剩下的在线用户发送有人离开的通知
						CatBean serverBean2 = new CatBean();
						serverBean2.setInfo(bean.getTimer() + "[" + bean.getName() + "] 下线了");
						serverBean2.setType(0);
						HashSet<String> set = new HashSet<String>();
						set.addAll(onlines.keySet());
						serverBean2.setClients(set);

						sendAll(serverBean2);
						return;
					}
					case 1: { // 聊天
						// 创建服务器的catbean，并发送给客户端
						CatBean serverBean = new CatBean();


						String fileName ="userInfo\\";
						fileName +=bean.getName();
						fileName+="_chatInfo.txt";
						File fileInput = new File(fileName);
						if (!fileInput.exists()) 
							fileInput.createNewFile();
						FileWriter fileWritter = new FileWriter(fileName, true);
						fileWritter.write("用户 ["+bean.getName()+"] "+
								bean.getTimer()+" 对 "+
								bean.getClients()+
								" 发送消息 \" "+
								bean.getInfo()+
								"\" \r\n");
						
						fileWritter.close(); 	
						
						serverBean.setType(1);
						serverBean.setClients(bean.getClients());
						serverBean.setInfo(bean.getInfo());
						serverBean.setName(bean.getName());
						serverBean.setTimer(bean.getTimer());
						// 向选中的客户发送数据
						sendMessage(serverBean);
						break;
					}
					case 3:{//影身
						// 创建服务器的catbean，并发送给客户端
						CatBean serverBean = new CatBean();
						serverBean.setType(3);

						try {
							oos = new ObjectOutputStream(client.getOutputStream());
							oos.writeObject(serverBean);
							oos.flush();
						} catch (IOException e) {
							e.printStackTrace();
						}

						onlines.remove(bean.getName());

						// 向剩下的在线用户发送有人离开的通知
						CatBean serverBean2 = new CatBean();
						serverBean2.setInfo(bean.getTimer() + "[" + bean.getName() + "] 下线了");
						serverBean2.setType(0);
						HashSet<String> set = new HashSet<String>();
						set.addAll(onlines.keySet());
						serverBean2.setClients(set);
						sendAll(serverBean2);
						break;
					}
					case 4:{//连接请求
						//建立文件筛选
						Properties userPro = new Properties();// 传输流
						File file = new File("Users.properties");// 密码用户文件
						Util.loadPro(userPro, file);// 读取并写入容器
						
						int loginResort;

						if (file.length() != 0) {
							if (userPro.containsKey(bean.getName())) {
								String u_pwd = new String(bean.getPassword());
								if (u_pwd.equals(userPro.getProperty(bean.getName()))) {
									loginResort =1; //返回登录成功的呃消息
										
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									//return sdf.format(new Date());
									String fileName ="userInfo\\";
									fileName +=bean.getName();
									fileName+="_chatInfo.txt";
									File fileInput = new File(fileName);
									if (!fileInput.exists()) 
										fileInput.createNewFile();
									FileWriter fileWritter = new FileWriter(fileName, true);
									fileWritter.write("用户 ["+bean.getName()+"] "+
											sdf.format(new Date())+
											" 登录\r\n");
									fileWritter.close(); 
									
								} else {
									//JOptionPane.showMessageDialog(null, "密码错误");
									loginResort =3;
									
								}
							} else {
								loginResort =2;
								//JOptionPane.showMessageDialog(null, "没有此用户");
								
							}
						} else {
							loginResort =2;
							//JOptionPane.showMessageDialog(null, "没有此用户");
						}
					
					
						try {
							ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
							oos.writeObject(loginResort);
							oos.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						break;
					}

					case 5:{//注册申请
						//建立文件筛选
						Properties userPro = new Properties();// 传输流
						File file = new File("Users.properties");// 密码用户文件
						Util.loadPro(userPro, file);// 读取并写入容器
						
						boolean registResort;//是否申请了已经存在的用户名
						
						if (userPro.containsKey(bean.getName())) {
							registResort = false;
						} else {
							userPro.setProperty(bean.getName(), bean.getPassword());
							try {
								userPro.store(new FileOutputStream(file),
										"Copyright (c) Boxcode Studio");
							} catch (FileNotFoundException e1) {
								e1.printStackTrace();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							registResort = true;
						}
						
						try {//写入
							ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
							oos.writeObject(registResort);
							oos.flush();
						} catch (IOException e) {
							e.printStackTrace();
						}

						break;
					}
					default: {
						break;
					}
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				close();
			}
		}

		// 向选中的用户发送数据
		private void sendMessage(CatBean serverBean) {
			// 首先取得所有的values
			Set<String> cbs = onlines.keySet();
			Iterator<String> it = cbs.iterator();
			// 选中客户
			HashSet<String> clients = serverBean.getClients();
			while (it.hasNext()) {
				// 在线客户
				String client = it.next();
				// 选中的客户中若是在线的，就发送serverbean
				if (clients.contains(client)) {
					Socket c = onlines.get(client).getSocket();
					ObjectOutputStream oos;
					try {
						oos = new ObjectOutputStream(c.getOutputStream());
						oos.writeObject(serverBean);
						oos.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		}

		// 向所有的用户发送数据
		public void sendAll(CatBean serverBean) {
			Collection<ClientBean> clients = onlines.values();
			Iterator<ClientBean> it = clients.iterator();
			ObjectOutputStream oos;
			while (it.hasNext()) {
				Socket c = it.next().getSocket();
				try {
					oos = new ObjectOutputStream(c.getOutputStream());
					oos.writeObject(serverBean);
					oos.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void close() {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {

		FileLock lck = null;
		try {
			lck = new FileOutputStream("flagFile").getChannel().tryLock();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (lck == null) {
			JOptionPane.showMessageDialog(null, "服务器已启动");
			System.exit(1);
		}
		
		try {
			// 修改风格界面为当前系统界面
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// 启动登陆界面
			new Server().start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
