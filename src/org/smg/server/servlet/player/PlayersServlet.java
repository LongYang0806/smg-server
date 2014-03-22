package org.smg.server.servlet.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.models.Player.PlayerProperty;
import org.smg.util.CORSUtil;
import org.smg.util.JSONUtil;

import com.google.appengine.labs.repackaged.org.json.JSONObject;

/**
 * Function: Insert a player Url pattern: /players
 * 
 * @author Archer
 * 
 *         TODO combine this with /players/{playerId}
 */
@SuppressWarnings("serial")
public class PlayersServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter res = resp.getWriter();
		CORSUtil.addCORSHeader(resp);

		BufferedReader br = new BufferedReader(new InputStreamReader(
				req.getInputStream()));
		String json = new String();
		String line = "";
		StringBuffer buffer = new StringBuffer();
		while ((line = br.readLine()) != null)
			buffer.append(line);
		json = buffer.toString();
		System.out.println(json);
		JSONObject returnValue = new JSONObject();
		if(json != null || json.length() != 0){
			Map<String, Object> jsonMap = JSONUtil.parse(json);
			String email = (String) jsonMap.get(PlayerProperty.EMAIL.toString());
			String firstName = (String) jsonMap.get(PlayerProperty.FIRSTNAME.toString());
			String lastName = (String) jsonMap.get(PlayerProperty.LASTNAME.toString());
			String nickName = (String) jsonMap.get(PlayerProperty.NICKNAME.toString());
			System.out.println("email: " + email);
			System.out.println("firstName: " + firstName);
			System.out.println("lastName: " + lastName);
			System.out.println("nickName: " + nickName);
			
		}
		
	}

	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

	}

	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

	}
}
