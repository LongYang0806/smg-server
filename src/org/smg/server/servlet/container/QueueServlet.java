
package org.smg.server.servlet.container;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smg.server.database.ContainerDatabaseDriver;
import org.smg.server.util.CORSUtil;
import org.smg.server.util.IDUtil;
import org.smg.server.util.JSONUtil;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@SuppressWarnings("serial")
public class QueueServlet extends HttpServlet {
  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CORSUtil.addCORSHeader(resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {

    CORSUtil.addCORSHeader(resp);
    BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
    String json = "";
    if (br != null) {
      json = br.readLine();
    }
    JSONObject returnValue = new JSONObject();
    if (json != null) {
      // parse json message to jsonMap
      Map<String, Object> jsonMap = null;
      try {
        jsonMap = JSONUtil.parse(json);
      } catch (IOException e) {
        ContainerVerification.sendErrorMessage(
            resp, returnValue, ContainerConstants.JSON_PARSE_ERROR);
        return;
      }
      // check if missing info
      if (!jsonMap.containsKey(ContainerConstants.PLAYER_ID)
          || !jsonMap.containsKey(ContainerConstants.ACCESS_SIGNATURE)
          || !jsonMap.containsKey(ContainerConstants.GAME_ID)) {
        ContainerVerification.sendErrorMessage(
            resp, returnValue, ContainerConstants.MISSING_INFO);
        return;
      }
      // verify playerId
      String pId = String.valueOf(jsonMap.get(ContainerConstants.PLAYER_ID));
      long playerId = 0;
      try {
        playerId = IDUtil.stringToLong(pId);
      } catch (Exception e) {
        ContainerVerification.sendErrorMessage(
            resp, returnValue, ContainerConstants.WRONG_PLAYER_ID);
        return;
      }
      if (!ContainerVerification.playerIdVerify(playerId)) {
        ContainerVerification.sendErrorMessage(
            resp, returnValue, ContainerConstants.WRONG_PLAYER_ID);
        return;
      }
      // verify accessSignature
      String accessSignature = String.valueOf(jsonMap.get(ContainerConstants.ACCESS_SIGNATURE));
      if (!ContainerVerification.accessSignatureVerify(accessSignature, playerId)) {
        ContainerVerification.sendErrorMessage(
            resp, returnValue, ContainerConstants.WRONG_ACCESS_SIGNATURE);
        return;
      }
      // parse gameID and verify gameId existed
      String gId = String.valueOf(jsonMap.get(ContainerConstants.GAME_ID));
      long gameId = 0;
      try {
        gameId = IDUtil.stringToLong(gId);
      } catch (Exception e) {
        ContainerVerification.sendErrorMessage(
            resp, returnValue, ContainerConstants.WRONG_GAME_ID);
        return;
      }
      if (!ContainerVerification.gameIdVerify(gameId)) {
        ContainerVerification.sendErrorMessage(
            resp, returnValue, ContainerConstants.WRONG_GAME_ID);
        return;
      }
      // verify if player already in queue
      if (ContainerVerification.playerAlreadyInQueue(playerId)) {
        ContainerVerification.sendErrorMessage(
            resp, returnValue, ContainerConstants.ENQUEUE_FAILED);
        return;
      }

      // Token Generating.
      String userId = String.valueOf(playerId);
      ChannelService channelService = ChannelServiceFactory.getChannelService();
      String channelToken = Utils.getClientId(userId);
      String clientToken = channelService.createChannel(channelToken);

      Map<String, Object> entityMap = ImmutableMap.<String, Object> of(
          ContainerConstants.GAME_ID, gameId,
          ContainerConstants.PLAYER_ID, playerId,
          ContainerConstants.CHANNEL_TOKEN, channelToken);

      /*
       * Try making a match.
       */
      int playerCount = ContainerDatabaseDriver.getPlayerNumberInQueue(gameId);
      List<String> playerIdsToBeSent = Lists.newArrayList();

      // Currently we will start a match for exact 2 players.
      if (playerCount >= 1) {
        List<Entity> playerIdsEntity = ContainerDatabaseDriver.getPlayersInQueue(gameId, 1);
        for (Entity playerEntity : playerIdsEntity) {
          Map<String, Object> props = playerEntity.getProperties();
          String pid = (String) props.get(ContainerConstants.CHANNEL_TOKEN);
          playerIdsToBeSent.add(pid);
        }

        // Delete the players who has been selected in a Match.
        for (String id : playerIdsToBeSent) {
          ContainerDatabaseDriver.deleteQueueEntity(Long.parseLong(id));
        }

        playerIdsToBeSent.add(String.valueOf(playerId));
        try {
          returnValue.put(ContainerConstants.PLAYER_IDS, playerIdsToBeSent);
        } catch (JSONException e) {
        }
      } else {
        ContainerDatabaseDriver.insertQueueEntity(entityMap);
      }

      // Send token back to client.
      try {
        returnValue.put(ContainerConstants.CHANNEL_TOKEN, clientToken);
      } catch (JSONException e) {
      }

    } else {
      try {
        returnValue.put(ContainerConstants.ERROR, ContainerConstants.NO_DATA_RECEIVED);
      } catch (JSONException e) {
      }
    }
    try {
      returnValue.write(resp.getWriter());
    } catch (JSONException e) {
    }
  }
}
