package chatter.common;

import java.awt.*;

/**
 * Defines a message. A message consists of a 4 character header
 * possibly followed by content.
 */
public class Message {

  public enum MessageType {
    CHAT, // A chat message
    QUIT, // A QUIT message
    AUTH, // An AUTH message
    OKAY
  }

  public MessageType type;
  public String messageContent;

  public Message(String message) throws InvalidMessageException {
    this.type = parseHeader(message);
    this.messageContent = parseContent(message);
  }

  private MessageType parseHeader(String message)
      throws InvalidMessageException {
    if (message.length() < 4) {
      throw new InvalidMessageException();
    }

    return MessageType.valueOf(message.substring(0,4));
  }

  private String parseContent(String message) throws InvalidMessageException {
    if (message.length() > 4) {
      return message.substring(4,message.length());
    }

    return "";
  }

  public static String createAuthMessage(String username, String password) {
    return MessageType.AUTH + username +
        Constants.PASSWORD_SEPARATOR + password +"\n";
  }

  public static String createQuitMessage() {
    return MessageType.QUIT + "\n";
  }

  public static String createOkayMessage() {
    return MessageType.OKAY + "\n";
  }


  public static String createChatMessage(String chatText) {
    return MessageType.CHAT + chatText + "\n";
  }
}
