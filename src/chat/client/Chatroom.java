package chat.client;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.sun.corba.se.spi.orbutil.fsm.Action;
import com.sun.corba.se.spi.orbutil.fsm.FSM;
import com.sun.corba.se.spi.orbutil.fsm.Input;

import chat.function.CatBean;
import chat.login.TextFocusHandle;
import chat.util.Util;

class CellRenderer extends JLabel implements ListCellRenderer {
	private static final long serialVersionUID = 1L;

	CellRenderer() {
		setOpaque(true);
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));// ������Ϊ5�Ŀհױ߿�
		// System.out.println(index);

		String iconName = "images//";
		iconName += Integer.toString((index) % 6 + 1);
		iconName += ".png";
		if (value != null) {
			setText(value.toString());
			setIcon(new ImageIcon(iconName));
		}
		if (isSelected) {
			setBackground(new Color(220, 220, 220));// ���ñ���ɫ
			setForeground(Color.black);
		} else {
			// ����ѡȡ��ȡ��ѡȡ��ǰ���뱳����ɫ.
			setBackground(Color.white); // ���ñ���ɫ
			setForeground(Color.black);
		}
		setEnabled(list.isEnabled());
		setFont(new Font("΢���ź�", Font.ROMAN_BASELINE, 20));
		setOpaque(true);
		return this;
	}
}

class UUListModel extends AbstractListModel {

	public Vector vs;

	public UUListModel(Vector vs) {
		this.vs = vs;
	}

	public Object getElementAt(int index) {
		return vs.get(index);
	}

	public int getSize() {
		return vs.size();
	}

}

public class Chatroom extends JFrame {

	private static final long serialVersionUID = 6129126482250125466L;

	private static JPanel contentPane;
	private static Socket clientSocket;
	private static ObjectOutputStream oos;
	private static ObjectInputStream ois;
	private static String name;
	private static JTextArea textArea;
	private static AbstractListModel listmodel;
	private static JList list;
	private static String filePath;
	private static JLabel lblNewLabel;
	private static JProgressBar progressBar;
	private static Vector onlines;
	private static boolean isSendFile = false;
	private static boolean isReceiveFile = false;
	private static JLabel labelUserType_1, labelUserType_2, labelUserType_3;
	int mouseAtX, mouseAtY;
	Font wordFont = new Font("΢���ź�", Font.PLAIN, 17);
	Font listFont = new Font("΢���ź�", Font.PLAIN, 19);
	int x, y = 9;

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

	public Chatroom(String u_name, Socket client) {
		// ��ֵ
		name = u_name;
		clientSocket = client;
		onlines = new Vector();

		movePanel();// ���ƶ��Ĵ���
		setUndecorated(true);// ȡ��������
		setSize(850, 630);
		setTitle("[�û�]" + name);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);

		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon("images\\chatRoom_1.jpg").getImage(), 0, 0, getWidth(), getHeight(), null);
			}

		};
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// ������Ϣ��ʾ���� ����С���� ��textareaȫ������С���� ��С������������
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 125, 610, 308);
		scrollPane.setBorder(null);
		getContentPane().add(scrollPane);
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);// �����Զ����й���
		textArea.setWrapStyleWord(true);// ������в����ֹ���
		textArea.setFont(wordFont);
		scrollPane.setViewportView(textArea);

		// �ı���ı��¼�������
//		textArea.getDocument().addDocumentListener(new DocumentListener(){
//			public void changedUpdate(DocumentEvent e) {
//				String data = textArea.getText();
//				String chatFileName = "";
//				Date date = new Date();
//				chatFileName +=Integer.toString(date.getYear());
//				File file = new File("javaio-appendfile.txt");
//				//����ı��������ݵı仯���ͽ��ı������ݽ����޸�
//				if (!file.exists()) {
//					try {
//						file.createNewFile();
//						FileWriter fileWritter = new FileWriter(file.getName(), true);
//						fileWritter.write(data);
//						fileWritter.close();
//					} catch (IOException e1) {
//						e1.printStackTrace();
//					}
//				}
//			}
//			public void removeUpdate(DocumentEvent e) {
//			}
//			public void insertUpdate(DocumentEvent e) {
//			}
//		});
//		

		// ��������
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 480, 610, 90);
		scrollPane_1.setBorder(null);
		getContentPane().add(scrollPane_1);
		final JTextArea textArea_1 = new JTextArea();
		textArea_1.setLineWrap(true);// �����Զ����й���
		textArea_1.setWrapStyleWord(true);// ������в����ֹ���
		textArea_1.setFont(wordFont);
		scrollPane_1.setViewportView(textArea_1);

		// �ı��ֺŰ�ť��ʵ��
		final JLabel labeFontChange = new JLabel();
		labeFontChange.setIcon(new ImageIcon("images\\chatRoom_changeWord_1.jpg"));
		labeFontChange.setBounds(48, 445, 35, 35);
		contentPane.add(labeFontChange);
		labeFontChange.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JOptionPane.showMessageDialog(null, "������δ���");
			}
		});

		// �����ļ���ť��ʵ��
		final JLabel labelSendFile = new JLabel();
		labelSendFile.setIcon(new ImageIcon("images\\chatRoom_sendFile_1.jpg"));
		labelSendFile.setBounds(130, 448, 25, 25);
		contentPane.add(labelSendFile);
		labelSendFile.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JOptionPane.showMessageDialog(null, "�ļ���δ���");
			}
		});

		// �û�����״̬��ť��ʵ��
		labelUserType_1 = new JLabel();
		labelUserType_1.setIcon(new ImageIcon("images\\user_type_2.jpg"));
		labelUserType_1.setBounds(699, 61, 48, 48);
		contentPane.add(labelUserType_1);
		final JTextArea onLine = new JTextArea();
		onLine.setText("����޸�����״̬");
		onLine.setFont(new Font("΢���ź�",Font.PLAIN,18));
		onLine.setBounds(550, 67, 150, 48);
		onLine.setVisible(false);
		contentPane.add(onLine);
		labelUserType_1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				labelUserType_2.setVisible(true);
				labelUserType_3.setVisible(true);

			}
			
			public void mouseEntered(MouseEvent e) {
				onLine.setVisible(true);
			}

			public void mouseExited(MouseEvent e) {
				onLine.setVisible(false);
			}
		});

		labelUserType_2 = new JLabel();
		labelUserType_2.setIcon(new ImageIcon("images\\user_type_2.jpg"));
		labelUserType_2.setBounds(650, 61, 48, 48);
		labelUserType_2.setVisible(false);
		contentPane.add(labelUserType_2);

		labelUserType_2.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				labelUserType_1.setIcon(new ImageIcon("images\\user_type_2.jpg"));
				labelUserType_2.setVisible(false);
				labelUserType_3.setVisible(false);
				
				CatBean clientBean = new CatBean();
				clientBean.setType(0);
				clientBean.setName(name);
				clientBean.setTimer(Util.getTimer());
				sendMessage(clientBean);
				
			}
		});

		labelUserType_3 = new JLabel();
		labelUserType_3.setIcon(new ImageIcon("images\\user_type_3.jpg"));
		labelUserType_3.setBounds(600, 61, 48, 48);
		labelUserType_3.setVisible(false);
		contentPane.add(labelUserType_3);

		labelUserType_3.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				labelUserType_2.setVisible(false);
				labelUserType_3.setVisible(false);
				labelUserType_1.setIcon(new ImageIcon("images\\user_type_3.jpg"));
				CatBean clientBean = new CatBean();
				clientBean.setType(3);
				clientBean.setName(name);
				clientBean.setTimer(Util.getTimer());
				sendMessage(clientBean);
			}
		});

		// �˳���ť��ʵ��
		final JLabel labeExit = new JLabel();
		labeExit.setIcon(new ImageIcon("images\\close_3.png"));
		labeExit.setBounds(815, 10, 30, 20);
		contentPane.add(labeExit);
		labeExit.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				CatBean clientBean = new CatBean();
				clientBean.setType(-1);
				clientBean.setName(name);
				clientBean.setTimer(Util.getTimer());
				sendMessage(clientBean);
			}

			public void mouseEntered(MouseEvent e) {
				labeExit.setIcon(new ImageIcon("images\\close_2.png"));
			}

			public void mouseExited(MouseEvent e) {
				labeExit.setIcon(new ImageIcon("images\\close_3.png"));
			}
		});

		// ��С����ť��ʵ��
		final JLabel labeMinisize = new JLabel();
		labeMinisize.setIcon(new ImageIcon("images\\minimize_3.png"));
		labeMinisize.setBounds(780, 5, 30, 30);
		contentPane.add(labeMinisize);
		labeMinisize.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				setExtendedState(JFrame.ICONIFIED);
			}

			public void mouseEntered(MouseEvent e) {
				labeMinisize.setIcon(new ImageIcon("images\\minimize_2.png"));
			}

			public void mouseExited(MouseEvent e) {
				labeMinisize.setIcon(new ImageIcon("images\\minimize_3.png"));
			}
		});

		// ���ͱ��鰴ť��ʵ��
		final JLabel labelEmoji = new JLabel();
		labelEmoji.setIcon(new ImageIcon("images\\emoj_1.jpg"));
		labelEmoji.setBounds(10, 445, 35, 35);
		contentPane.add(labelEmoji);
		final JPopupMenu popMenuEmoji = new JPopupMenu();// �����˵�
		String[] Emoji = { "(�V_�V)", "(�i�s^�t�i)", "(�s3�t)", "(�i�n�i)", "(�Уߩ�)�K ��", "(�t_�s)#", "(���ש� _ ���ש�)", "{{{(>_<)}}}",
				"~(��0��)/", "__��(����;)" };
		for (int i = 0; i < Emoji.length; i++) {
			String nametemp = "item_1_";
			nametemp += Integer.toString(i);
			JMenuItem nameEmoji = new JMenuItem(Emoji[i]);// �����˵���
			nameEmoji.setIcon(new ImageIcon("images\\chatRoom_sendFile_1.jpg"));
			nameEmoji.addActionListener(new ItemListener());
			popMenuEmoji.add(nameEmoji);
		}
		labelEmoji.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				popMenuEmoji.show(labelEmoji, e.getX(), e.getY() + labelEmoji.getHeight());
			}
		});

		// �޸�Ⱥ��������
		final JLabel labelChatRoomName = new JLabel();
		labelChatRoomName.setText("��Ժ�������Ⱥ");
		x = 850 / 2 - (int) labelChatRoomName.getText().length() / 2 * 30;
		labelChatRoomName.setBounds(x, y, (int) labelChatRoomName.getText().length() * 40, 30);
		labelChatRoomName.setEnabled(false);
		labelChatRoomName.setFont(new Font("����", Font.BOLD, 23));
		contentPane.add(labelChatRoomName);

		// �޸�
		final JTextField labelChatRoomName_1 = new JTextField();
		labelChatRoomName_1.setFont(new Font("����", Font.BOLD, 23));
		labelChatRoomName_1.setBounds(150, y, 550, 30);
		labelChatRoomName_1.setVisible(false);
		labelChatRoomName_1.setHorizontalAlignment(JTextField.CENTER);
		contentPane.add(labelChatRoomName_1);

		// ����Ⱥ����ʱ��
		labelChatRoomName.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				labelChatRoomName.setVisible(false);
				labelChatRoomName_1.setVisible(true);
				labelChatRoomName_1.setText(labelChatRoomName.getText());
			}
		});
		// ��ʼ�޸�Ⱥ��
		labelChatRoomName_1.addFocusListener(new TextFocusHandle() {
			public void focusGained(FocusEvent e) {// ��ý���{
				labelChatRoomName.setText("");
			}

			public void focusLost(FocusEvent e) {// ʧȥ����
				if (labelChatRoomName_1.getText().equals("")) {
					labelChatRoomName.setText("��Ժ�������Ⱥ");
				}
				x = 850 / 2 - (int) labelChatRoomName_1.getText().length() / 2 * 30;

				labelChatRoomName.setText(labelChatRoomName_1.getText());
				labelChatRoomName.setVisible(true);
				labelChatRoomName_1.setVisible(false);
				labelChatRoomName.setBounds(x, y, (int) labelChatRoomName_1.getText().length() * 30, 30);

			}
		});

		// �رհ�ť
		final JButton btnNewButton = new JButton("�� ��");
		btnNewButton.setBounds(400, 580, 75, 31);

		btnNewButton.setFont(wordFont);
		getContentPane().add(btnNewButton);

		// ���Ͱ�ť
		final JButton btnNewButton_1 = new JButton("�� ��");
		btnNewButton_1.setFont(wordFont);
		btnNewButton_1.setBounds(495, 580, 75, 31);
		getContentPane().add(btnNewButton_1);

		// ����Ĭ����Ϣ
		final JButton btnNewButton_2 = new JButton("��");
		btnNewButton_2.setFont(new Font("����", Font.PLAIN, 10));
		btnNewButton_2.setBounds(567, 580, 45, 31);
		getContentPane().add(btnNewButton_2);

		final JPopupMenu popMenuInfo = new JPopupMenu();// ��ĵ����˵�
		JMenuItem item_1 = new JMenuItem("  ��ã�����");// �����˵���
		JMenuItem item_2 = new JMenuItem("  �õģ��ټ�");
		JMenuItem item_3 = new JMenuItem("  ����æ�����¸���ظ�");
		JMenuItem item_4 = new JMenuItem("  �˲��ڣ�������");
		popMenuInfo.add(item_1);
		popMenuInfo.add(item_2);
		popMenuInfo.add(item_3);
		popMenuInfo.add(item_4);

		btnNewButton_2.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				popMenuInfo.show(btnNewButton_2, e.getX(), e.getY() + btnNewButton_2.getHeight());
			}
		});

		item_1.addActionListener(new ItemListener());
		item_2.addActionListener(new ItemListener());
		item_3.addActionListener(new ItemListener());
		item_4.addActionListener(new ItemListener());

		// ���߿ͻ��б�
		listmodel = new UUListModel(onlines);
		list = new JList(listmodel);
		list.setCellRenderer(new CellRenderer());
		list.setOpaque(false);
		list.setBorder(null);

		String iconName = "images//";
		// iconName += Integer.toString(onlines.size()%6);
		iconName += "1.png";
		setIconImage(new ImageIcon(iconName).getImage());// ����ͼ��

		JTextArea infoText = new JTextArea("Ⱥ��Ա");
		infoText.setFont(listFont);
		infoText.setBounds(628, 120, 223, 35);
		infoText.setEnabled(false);
		getContentPane().add(infoText);

		JScrollPane scrollPane_2 = new JScrollPane(list);
		scrollPane_2.setBounds(628, 150, 223, 460);
		scrollPane_2.setOpaque(false);
		scrollPane_2.getViewport().setOpaque(false);
		// scrollPane_2.setFont(listFont);
		scrollPane_2.setBorder(null);
		getContentPane().add(scrollPane_2);

		try {
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			// ��¼���߿ͻ�����Ϣ��catbean�У������͸�������
			CatBean bean = new CatBean();
			bean.setType(0);
			bean.setName(name);
			bean.setTimer(Util.getTimer());
			oos.writeObject(bean);
			oos.flush();

			// �����ͻ������߳�
			new ClientInputThread().start();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ���Ͱ�ť
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String info = textArea_1.getText();
				List to = list.getSelectedValuesList();

				if (to.size() < 1) {
					JOptionPane.showMessageDialog(getContentPane(), "��ѡ���������");
					return;
				}

				if (info.equals("")) {
					JOptionPane.showMessageDialog(getContentPane(), "���ܷ��Ϳ���Ϣ");
					return;
				}

				CatBean clientBean = new CatBean();
				clientBean.setType(1);
				clientBean.setName(name);
				String time = Util.getTimer();
				clientBean.setTimer(time);
				clientBean.setInfo(info);
				HashSet set = new HashSet();
				set.addAll(to);
				clientBean.setClients(set);

				// �Լ���������ҲҪ��ʵ���Լ�����Ļ����
				textArea.append(time + "[" + name + "]��" + to + "˵:\r\n" + info + "\r\n");
				sendMessage(clientBean);
				textArea_1.setText(null);
				textArea_1.requestFocus();
			}
		});

		// �رհ�ť
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				btnNewButton.setEnabled(false);
				CatBean clientBean = new CatBean();
				clientBean.setType(-1);
				clientBean.setName(name);
				clientBean.setTimer(Util.getTimer());
				sendMessage(clientBean);

			}
		});

		// �뿪
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				CatBean clientBean = new CatBean();
				clientBean.setType(-1);
				clientBean.setName(name);
				clientBean.setTimer(Util.getTimer());
				sendMessage(clientBean);

			}
		});

		// �б����
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				List to = list.getSelectedValuesList();
			}
		});

	}

	// ����Ĭ����Ϣ
	private class ItemListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// ����Ĭ����Ϣ
			JMenuItem menuItem = (JMenuItem) e.getSource();// ��ȡ������Ĳ˵���

			String info = menuItem.getText();
			List to = list.getSelectedValuesList();

			if (to.size() < 1) {
				JOptionPane.showMessageDialog(getContentPane(), "��ѡ���������");
				return;
			}
			CatBean clientBean = new CatBean();
			clientBean.setType(1);
			clientBean.setName(name);
			String time = Util.getTimer();
			clientBean.setTimer(time);
			clientBean.setInfo(info);
			HashSet set = new HashSet();
			set.addAll(to);
			clientBean.setClients(set);

			// �Լ���������ҲҪ��ʵ���Լ�����Ļ����
			textArea.append(time + "[" + name + "]��" + to + "˵:\r\n" + info + "\r\n");
			sendMessage(clientBean);

		}
	}

	// ���ʹ����߳�
	class ClientInputThread extends Thread {

		public void run() {
			try {
				// ��ͣ�Ĵӷ�����������Ϣ
				while (true) {

					ois = new ObjectInputStream(clientSocket.getInputStream());
					final CatBean bean = (CatBean) ois.readObject();
					switch (bean.getType()) {
					case 0: {
						// �����б�
						onlines.clear();
						HashSet<String> clients = bean.getClients();
						Iterator<String> it = clients.iterator();
						while (it.hasNext()) {
							String ele = it.next();
							if (name.equals(ele)) {
								onlines.add(ele + "[�û�]");
							} else {
								onlines.add(ele);
							}
						}

						listmodel = new UUListModel(onlines);
						list.setModel(listmodel);

						textArea.append(bean.getInfo() + "\r\n");
						textArea.selectAll();
						break;
					}
					case -1: {
						return;// ����ֱ���˳�
					}
					case 1: {
						//������Ϣ
						String info = bean.getTimer() + bean.getName() + "��[" + bean.getClients() + "]˵:\r\n";
						textArea.append(info + bean.getInfo() + "\r\n");
						textArea.selectAll();
						break;
					}
					case 2: {
						
						break;
					}
					case 3: {
						textArea.append("�û� [" + name + "]Ӱ����\r\n");
						break;
					}
					case 4: {

						textArea.append(bean.getInfo() + "\r\n");
						break;
					}
					default: {

						break;
					}
					}

				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (clientSocket != null) {
					try {
						clientSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.exit(0);
			}
		}
	}

	private void sendMessage(CatBean clientBean) {
		try {
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			oos.writeObject(clientBean);
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
