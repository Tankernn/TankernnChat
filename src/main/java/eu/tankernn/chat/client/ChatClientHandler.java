package eu.tankernn.chat.client;

import eu.tankernn.chat.packets.InfoPacket;
import eu.tankernn.chat.packets.MessagePacket;
import eu.tankernn.chat.packets.StringPacket;
import eu.tankernn.chat.packets.filesend.FileSendDataPacket;
import eu.tankernn.chat.packets.filesend.FileSendInfoPacket;
import eu.tankernn.chat.packets.filesend.FileSendStatusPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public class ChatClientHandler extends ChannelInboundHandlerAdapter {
	private ChatClient client;

	public ChatClientHandler(ChatClient client) {
		this.client = client;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object fromServer) {
	    System.out.println(fromServer);
	    if (fromServer instanceof MessagePacket) {
			MessagePacket mess = ((MessagePacket) fromServer);
			client.getChatWindow().log(mess);
			
		} else if (fromServer instanceof InfoPacket) {
			InfoPacket info = (InfoPacket) fromServer;
			client.getChatWindow().setInfo(info);
			client.getFileWindow().updateComboBox(info.usersOnline);
			
		} else if (fromServer instanceof FileSendInfoPacket) {
			FileSendInfoPacket pack = (FileSendInfoPacket) fromServer;
			client.getFileWindow().addDownload(pack);
			
		} else if (fromServer instanceof FileSendStatusPacket) {
			FileSendStatusPacket s = ((FileSendStatusPacket) fromServer);
			switch (s) {
			case ACCEPT:
				client.getFileWindow().startUpload();
				break;
			case DENY:
				client.getFileWindow().cancelUpload();
				break;
			case FINISHED:
				client.getFileWindow().getDownload().finish();
				break;
			default:
				break;
			}
		} else if (fromServer instanceof FileSendDataPacket) {
			client.getFileWindow().getDownload().handlePacket((FileSendDataPacket) fromServer);
		}
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			ctx.writeAndFlush(new StringPacket(""));
		}
	}
}
