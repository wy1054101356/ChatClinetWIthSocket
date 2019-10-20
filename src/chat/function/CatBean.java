package chat.function;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class CatBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private int type; // 1˽�� 0�����߸��� -1�������� 2�������ļ� 3.ȷ�������ļ� 4����

	private HashSet<String> clients; // ���ѡ�еĿͻ�

	private HashSet<String> to;

	public HashMap<String, ClientBean> onlines;

	private String info;

	private String timer;

	private String name;

	private String password;

	private String fileName;

	private int flag;

	private int connectResort; // 1-�ɹ� 2-û���û��� 3-�������

	private int size;

	private String ip;

	private int port;

	public int getConnectResort() {
		return connectResort;
	}

	public void setConnectResort(int connectResort) {
		this.connectResort = connectResort;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public HashSet<String> getTo() {
		return to;
	}

	public void setTo(HashSet<String> to) {
		this.to = to;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public HashSet<String> getClients() {
		return clients;
	}

	public void setClients(HashSet<String> clients) {
		this.clients = clients;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getTimer() {
		return timer;
	}

	public void setTimer(String timer) {
		this.timer = timer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public HashMap<String, ClientBean> getOnlines() {
		return onlines;
	}

	public void setOnlines(HashMap<String, ClientBean> onlines) {
		this.onlines = onlines;
	}

}