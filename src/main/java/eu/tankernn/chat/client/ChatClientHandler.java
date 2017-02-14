package eu.tankernn.chat.client;

import javax.swing.DefaultListModel;
import javax.swing.JScrollBar;

import eu.tankernn.chat.packets.InfoPacket;
import eu.tankernn.chat.packets.MessagePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

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
			// Scroll down
			JScrollBar s = chatWindow.scroll.getVerticalScrollBar();
			s.setValue(s.getMaximum());
		} else if (fromServer instanceof InfoPacket) {
			InfoPacket info = (InfoPacket) fromServer;

			chatWindow.infoLabel.setText("<html>" + info.toString().replace("\n", "<br>"));

			DefaultListModel<String> model = new DefaultListModel<String>();
			for (String user : info.usersOnline)
				model.addElement(user);

			chatWindow.userList.setModel(model);
		}
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			ctx.writeAndFlush("/ping");
		}
	}
}
