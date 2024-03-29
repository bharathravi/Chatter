package chatter.common;

/**
 * Defines a message. A message consists of a 4 character header
 * possibly followed by content.
 */
public class Message {
  public enum MessageType {
    CHAT, // A chat message, containing actual chat text.
    QUIT, // A QUIT message, sent by either party when it terminates its end of the connection.
    AUTH, // An AUTH message, used by the chatter.client to send its user name and password.
    OKAY, // An OKAY message, sent by the server to let the chatter.client know that things are fine.
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
      throw new InvalidMessageException(message);
    }

    try {
     MessageType type = MessageType.valueOf(message.substring(0,4));
     return type;
    } catch (Exception e) {
      throw new InvalidMessageException(message);
    }
  }

  private String parseContent(String message) throws InvalidMessageException {
    if (message.length() > 4) {
      return message.substring(4,message.length());
    }

    return "";
  }

  public static String createAuthMessage(String username, String password) {
    return MessageType.AUTH + username +
        Constants.PASSWORD_SEPARATOR + password;
  }

  public static String createQuitMessage() {
    return MessageType.QUIT.toString();
  }

  public static String createOkayMessage() {
    return MessageType.OKAY.toString();
  }


  public static String createChatMessage(String chatText) {
    return MessageType.CHAT + chatText;
  }
}
