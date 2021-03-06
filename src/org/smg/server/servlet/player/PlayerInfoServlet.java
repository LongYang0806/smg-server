package org.smg.server.servlet.player;

import static org.smg.server.servlet.game.GameConstants.EMAIL;
import static org.smg.server.servlet.game.GameConstants.FIRST_NAME;
import static org.smg.server.servlet.game.GameConstants.LAST_NAME;
import static org.smg.server.servlet.game.GameConstants.NICKNAME;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.DatabaseDriverPlayer;
import org.smg.server.database.models.Player;
import org.smg.server.database.models.Player.PlayerProperty;
import org.smg.server.util.CORSUtil;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class PlayerInfoServlet extends HttpServlet {
  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
  }
  

  /**
   *    Get player Info:
   *    method: GET
   *    url:/playerInfo?playerId=...&targetId=...&accessSignature=...
   *    return: {  ��email�� : ��,  ��firstname�� : ��,  ��lastname�� : ��,  ��nickname�� : ��.}
   *            {  ��firstname�� : ��,  ��nickname�� : ��.}
   *            {"error": "WRONG_ACCESS_SIGNATURE"}
   *            {"error": "WRONG_PLAYER_ID"}
   *            {"error": "WRONG_TARGET_ID"}
   */
  @SuppressWarnings("unchecked")
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    CORSUtil.addCORSHeader(resp);
    resp.setContentType("text/plain");
    JSONObject returnValue = new JSONObject();
    Map<String, Object> map = req.getParameterMap();
    String playerId = null;
    if (!map.containsKey("playerId")){
      addErrorMessage(returnValue,"WRONG_PLAYER_ID",resp);
      return;
    }
    playerId = req.getParameter("playerId");
    long playerIdLong;
    try {
      playerIdLong = Long.parseLong(playerId);
    } catch (NumberFormatException e) {
      addErrorMessage(returnValue,"WRONG_PLAYER_ID",resp);
      return;
    }
    if (!map.containsKey("accessSignature")){
      addErrorMessage(returnValue,"WRONG_ACCESS_SIGNATURE",resp);
      return;
    }
    String accessSignature = req.getParameter("accessSignature");
    try {
      boolean valid = DatabaseDriverPlayer
          .validatePlayerAccessSignature(playerIdLong, accessSignature);
      if (!valid){
        addErrorMessage(returnValue,"WRONG_ACCESS_SIGNATURE",resp);
        return;
      } else {
        String targetId = null;
        if (!map.containsKey("targetId")){
          addErrorMessage(returnValue,"WRONG_TARGET_ID",resp);
          return;
        }
        targetId = req.getParameter("targetId");
        long targetIdLong;
        try {
          targetIdLong = Long.parseLong(targetId);
        } catch (NumberFormatException e) {
          addErrorMessage(returnValue,"WRONG_TARGET_ID",resp);
          return;
        }
        Player target = DatabaseDriverPlayer.getPlayerById(targetIdLong);
        if (targetIdLong == playerIdLong) {
          try {
            returnValue.put(EMAIL, target.getProperty(PlayerProperty.EMAIL));
            returnValue.put(LAST_NAME, target.getProperty(PlayerProperty.LASTNAME));
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
        try {
          returnValue.put(FIRST_NAME, target.getProperty(PlayerProperty.FIRSTNAME));
          returnValue.put(NICKNAME, target.getProperty(PlayerProperty.NICKNAME));
          returnValue.write(resp.getWriter());
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
      return;
    } catch (EntityNotFoundException e) {
      addErrorMessage(returnValue,"WRONG_TARGET_ID",resp);
      return;
    }
  }
  
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    CORSUtil.addCORSHeader(resp);
    JSONObject returnValue = new JSONObject();
    addErrorMessage(returnValue,"NOT SUPPORT METHOD",resp);
    return;
  }

  @Override
  public void doDelete(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    CORSUtil.addCORSHeader(resp);
    JSONObject returnValue = new JSONObject();
    addErrorMessage(returnValue,"NOT SUPPORT METHOD",resp);
    return;
  }

  @Override
  public void doPut(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    CORSUtil.addCORSHeader(resp);
    JSONObject returnValue = new JSONObject();
    addErrorMessage(returnValue,"NOT SUPPORT METHOD",resp);
    return;
  }
  
  private void addErrorMessage(JSONObject returnValue, 
      String errorMessage, HttpServletResponse resp) throws IOException {
    try {
      returnValue.put("error", errorMessage);
      returnValue.write(resp.getWriter());
    } catch (JSONException e2) {
      e2.printStackTrace();
    }
  }
}
