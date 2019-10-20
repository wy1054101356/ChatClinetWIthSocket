package chat.function;

import java.io.Serializable;
import java.net.Socket;

public class ClientBean implements Serializable  {
	private static final long serialVersionUID = 1L;
	private String name;
	private Socket socket;
	private String password;
	private int flag;
	private int connectResort;	//1-成功  2-没有用户名  3-密码错误
	
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


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
}
