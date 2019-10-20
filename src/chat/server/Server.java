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
	

	//��������
	public void start() {



		mf.setTitle("�����: δ����");
		miniTray();
		mf.setSize(400, 250);
		mf.setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		mf.setContentPane(contentPane);
		contentPane.setLayout(null);
		mf.setIconImage(new ImageIcon("images//server_2.png").getImage());// ����ͼ��

		Font buttonFont = new Font("΢���ź�",Font.PLAIN,18);
		
		//����ϢĿ¼
		buttonOpenFile = new JButton();
		buttonOpenFile.setText("���û���¼Ŀ¼");
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
		
		
		//��������
		buttonStart = new JButton();
		buttonStart.setText("��������");
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

		// �رշ���
		buttonClose = new JButton();
		buttonClose.setText("�رշ���");
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

		
		//��ʾ�˿ںź�IP
		InetAddress ip = null;// ����
		String localIp = "��������ַ:";// ����IP
		try {
			ip = ip.getLocalHost();// ��ȡPC
			localIp += ip.getHostAddress();// ��ȡIP��ַ
		} catch (UnknownHostException e2) {
			e2.printStackTrace();
		}

		JLabel IPtext = new JLabel();
		IPtext.setBounds(20, 10, 300, 40);
		IPtext.setFont(new Font("΢���ź�", Font.PLAIN,16));
		IPtext.setText(localIp);
		contentPane.add(IPtext);
		
		String localport = "�������˿�:";// ����IP
		int localPort = 10086;
		localport+=Integer.toString(localPort);
		
		JLabel IPtextport = new JLabel();
		IPtextport.setBounds(20, 30, 300, 40);
		IPtextport.setFont(new Font("΢���ź�", Font.PLAIN,16));
		IPtextport.setText(localport);
		contentPane.add(IPtextport);
		
		
		mf.addWindowListener(new WindowAdapter() { // ���ڹر��¼�
			public void windowClosing(WindowEvent e) {
				System.exit(mf.NORMAL);
			};

			public void windowIconified(WindowEvent e) { // ������С���¼�
				mf.setVisible(false);
				miniTray();
			}

		});

		mf.setVisible(true);
	}

	// ������С��������������
	private static void miniTray() { 

		trayImg1 = new ImageIcon("images//server_1.png");// ����ͼ��
		trayImg2 = new ImageIcon("images//server_2.png");// ����ͼ��
		mf.setIconImage(new ImageIcon("images//server_1.png").getImage());// ����ͼ��
		mf.setTitle("�����: ������");
		if (startFlag) {
			
			InetAddress ip = null;// ����
			int localPort = 10086;
			String localIp = "������״̬: ����\r\n";// ����IP
			localIp +="��������ַ: ";
			try {
				ip = ip.getLocalHost();// ��ȡPC
				localIp += ip.getHostAddress();// ��ȡIP��ַ
			} catch (UnknownHostException e2) {
				e2.printStackTrace();
			}
			localIp +="\r\n�������˿�: ";
			localIp += Integer.toString(localPort);
			localIp +="\r\n";
			
			trayIcon = new TrayIcon(trayImg1.getImage(), localIp, new PopupMenu());
		}

		if (!startFlag) {
			InetAddress ip = null;// ����
			int localPort = 10086;
			String localIp = "������״̬: ֹͣ\r\n";// ����IP
			localIp +="��������ַ: ";
			try {
				ip = ip.getLocalHost();// ��ȡPC
				localIp += ip.getHostAddress();// ��ȡIP��ַ
			} catch (UnknownHostException e2) {
				e2.printStackTrace();
			}
			localIp +="\r\n�������˿�: ";
			localIp += Integer.toString(localPort);
			localIp +="\r\n";
			mf.setIconImage(new ImageIcon("images//server_2.png").getImage());// ����ͼ��
			mf.setTitle("�����: δ����");
			trayIcon = new TrayIcon(trayImg2.getImage(), localIp, new PopupMenu());
		}

		trayIcon.setImageAutoSize(true);
		trayIcon.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				if (e.getClickCount() == 1) {// ���� 1 ˫�� 2

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
				// ��ͣ�Ĵӿͻ��˽�����Ϣ
				while (true) {
					// ��ȡ�ӿͻ��˽��յ���catbean��Ϣ
								
					ois = new ObjectInputStream(client.getInputStream());
					bean = (CatBean) ois.readObject();
					
					// ����catbean�У�type������һ������
					switch (bean.getType()) {
					// �����߸���
					case 0: { // ����
						// ��¼���߿ͻ����û����Ͷ˿���clientbean��
						ClientBean cbean = new ClientBean();
						cbean.setName(bean.getName());
						cbean.setSocket(client);
						// ��������û�
						onlines.put(bean.getName(), cbean);
						// ������������catbean�������͸��ͻ���
						CatBean serverBean = new CatBean();
						serverBean.setType(0);
						serverBean.setInfo(bean.getTimer() + "[" + bean.getName() + "]������");
						// ֪ͨ���пͻ���������
						HashSet<String> set = new HashSet<String>();
						// �ͻ��ǳ�
						set.addAll(onlines.keySet());
						serverBean.setClients(set);
						sendAll(serverBean);
						break;
					}
					case -1: { // ����
						// ������������catbean�������͸��ͻ���
						CatBean serverBean = new CatBean();
						serverBean.setType(-1);

						try {
							oos = new ObjectOutputStream(client.getOutputStream());
							oos.writeObject(serverBean);
							oos.flush();
						} catch (IOException e) {
							e.printStackTrace();
						}

						//д���ļ���Ϣ
						
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String fileName ="userInfo\\";
						fileName +=bean.getName();
						fileName+="_chatInfo.txt";
						File fileInput = new File(fileName);
						
						if (!fileInput.exists()) 
							fileInput.createNewFile();
						FileWriter fileWritter = new FileWriter(fileName, true);
						fileWritter.write("�û� ["+bean.getName()+"] "+
								sdf.format(new Date())+
								" ����\r\n");
						fileWritter.close(); 	
						
						onlines.remove(bean.getName());

						// ��ʣ�µ������û����������뿪��֪ͨ
						CatBean serverBean2 = new CatBean();
						serverBean2.setInfo(bean.getTimer() + "[" + bean.getName() + "] ������");
						serverBean2.setType(0);
						HashSet<String> set = new HashSet<String>();
						set.addAll(onlines.keySet());
						serverBean2.setClients(set);

						sendAll(serverBean2);
						return;
					}
					case 1: { // ����
						// ������������catbean�������͸��ͻ���
						CatBean serverBean = new CatBean();


						String fileName ="userInfo\\";
						fileName +=bean.getName();
						fileName+="_chatInfo.txt";
						File fileInput = new File(fileName);
						if (!fileInput.exists()) 
							fileInput.createNewFile();
						FileWriter fileWritter = new FileWriter(fileName, true);
						fileWritter.write("�û� ["+bean.getName()+"] "+
								bean.getTimer()+" �� "+
								bean.getClients()+
								" ������Ϣ \" "+
								bean.getInfo()+
								"\" \r\n");
						
						fileWritter.close(); 	
						
						serverBean.setType(1);
						serverBean.setClients(bean.getClients());
						serverBean.setInfo(bean.getInfo());
						serverBean.setName(bean.getName());
						serverBean.setTimer(bean.getTimer());
						// ��ѡ�еĿͻ���������
						sendMessage(serverBean);
						break;
					}
					case 3:{//Ӱ��
						// ������������catbean�������͸��ͻ���
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

						// ��ʣ�µ������û����������뿪��֪ͨ
						CatBean serverBean2 = new CatBean();
						serverBean2.setInfo(bean.getTimer() + "[" + bean.getName() + "] ������");
						serverBean2.setType(0);
						HashSet<String> set = new HashSet<String>();
						set.addAll(onlines.keySet());
						serverBean2.setClients(set);
						sendAll(serverBean2);
						break;
					}
					case 4:{//��������
						//�����ļ�ɸѡ
						Properties userPro = new Properties();// ������
						File file = new File("Users.properties");// �����û��ļ�
						Util.loadPro(userPro, file);// ��ȡ��д������
						
						int loginResort;

						if (file.length() != 0) {
							if (userPro.containsKey(bean.getName())) {
								String u_pwd = new String(bean.getPassword());
								if (u_pwd.equals(userPro.getProperty(bean.getName()))) {
									loginResort =1; //���ص�¼�ɹ�������Ϣ
										
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									//return sdf.format(new Date());
									String fileName ="userInfo\\";
									fileName +=bean.getName();
									fileName+="_chatInfo.txt";
									File fileInput = new File(fileName);
									if (!fileInput.exists()) 
										fileInput.createNewFile();
									FileWriter fileWritter = new FileWriter(fileName, true);
									fileWritter.write("�û� ["+bean.getName()+"] "+
											sdf.format(new Date())+
											" ��¼\r\n");
									fileWritter.close(); 
									
								} else {
									//JOptionPane.showMessageDialog(null, "�������");
									loginResort =3;
									
								}
							} else {
								loginResort =2;
								//JOptionPane.showMessageDialog(null, "û�д��û�");
								
							}
						} else {
							loginResort =2;
							//JOptionPane.showMessageDialog(null, "û�д��û�");
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

					case 5:{//ע������
						//�����ļ�ɸѡ
						Properties userPro = new Properties();// ������
						File file = new File("Users.properties");// �����û��ļ�
						Util.loadPro(userPro, file);// ��ȡ��д������
						
						boolean registResort;//�Ƿ��������Ѿ����ڵ��û���
						
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
						
						try {//д��
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

		// ��ѡ�е��û���������
		private void sendMessage(CatBean serverBean) {
			// ����ȡ�����е�values
			Set<String> cbs = onlines.keySet();
			Iterator<String> it = cbs.iterator();
			// ѡ�пͻ�
			HashSet<String> clients = serverBean.getClients();
			while (it.hasNext()) {
				// ���߿ͻ�
				String client = it.next();
				// ѡ�еĿͻ����������ߵģ��ͷ���serverbean
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

		// �����е��û���������
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
			JOptionPane.showMessageDialog(null, "������������");
			System.exit(1);
		}
		
		try {
			// �޸ķ�����Ϊ��ǰϵͳ����
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// ������½����
			new Server().start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
