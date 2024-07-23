package telran.net;

import static telran.net.tcpConfigurationProperties.*;

import java.io.*;
import java.net.*;

import org.json.JSONObject;

public class TcpClient {
	int port;
	String hostname;

	public TcpClient(int port, String hostname) {
		this.port = port;
		this.hostname = hostname;
	}

	public Response sendAndRecieve(Request request) {
		String response = null;
		try (Socket socket = new Socket(hostname, port);
				BufferedReader reciever = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintStream sender = new PrintStream(socket.getOutputStream())) {
			sender.println(request.toString());
			response = reciever.readLine();
		} catch (Exception e) {
			System.out.println(e);
		}
		return getResponseFromJSONString(response);

	}

	private Response getResponseFromJSONString(String response) {
		JSONObject jsonObj = new JSONObject(response);
		ResponseCode responseType = ResponseCode.valueOf(jsonObj.getString(RESPONSE_CODE_FIELD));
		String responseData = jsonObj.getString(RESPONSE_DATA_FIELD);
		return new Response(responseType, responseData);
	}

}
