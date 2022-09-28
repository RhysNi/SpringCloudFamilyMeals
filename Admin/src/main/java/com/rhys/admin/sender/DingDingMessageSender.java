package com.rhys.admin.sender;

import com.alibaba.fastjson.JSONObject;
import com.rhys.admin.entity.Content;
import com.rhys.admin.entity.Message;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/9/29 1:34 上午
 */
public class DingDingMessageSender {
    public static void sendTextMessage(String msg) {
        try {
            URL url = new URL("https://oapi.dingtalk.com/robot/send?access_token=75095c1630c2e5649f1c0c4704742d42906d4af96e90efb6e087b1ef1f406363");
            // 建立 http 连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/Json; charset=UTF-8");
            conn.connect();

            OutputStream out = conn.getOutputStream();
            String textMessage = JSONObject.toJSONString(Message.builder().msgtype("text").text(new Content(msg)).build());
            byte[] textMessageBytes = textMessage.getBytes();
            out.write(textMessageBytes);
            out.flush();
            out.close();

            InputStream in = conn.getInputStream();
            byte[] data = new byte[in.available()];
            in.read(data);
            System.out.println(new String(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
