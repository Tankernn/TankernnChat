package eu.tankernn.chat.client;

import javax.swing.DefaultListModel;

import eu.tankernn.chat.common.InfoPacket;
import eu.tankernn.chat.common.MessagePacket;
import eu.tankernn.chat.common.MessagePacket.MessageType;
import eu.tankernn.chat.common.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ChatClientHandler extends ChannelInboundHandlerAdapter {
	private ChatWindow chatWindow;

	public ChatClientHandler(ChatWindow chatWindow) {
		this.chatWindow = chatWindow;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object fromServer) {
	    System.out.println(fromServer);
	    if (fromServer instanceof MessagePacket) {
			MessagePacket mess = ((MessagePacket) fromServer);
			chatWindow.chat.log(mess);
		} else if (fromServer instanceof InfoPacket) {
			InfoPacket info = (InfoPacket) fromServer;

			chatWindow.infoLabel.setText("<html>" + info.toString().replace("\n", "<br>"));

			DefaultListModel<String> model = new DefaultListModel<String>();
			for (String user : info.usersOnline)
				model.addElement(user);

			chatWindow.userList.setModel(chatWindow.model);
		} else if (fromServer instanceof String) {
			chatWindow.chat.log(new MessagePacket((String) fromServer, MessageType.NORMAL));
		}
	    ctx.close();
	}
}
