package eu.tankernn.chat.server;

import java.util.Optional;

import eu.tankernn.chat.packets.InfoPacket;
import eu.tankernn.chat.packets.MessagePacket;
import eu.tankernn.chat.packets.MessagePacket.MessageType;
import eu.tankernn.chat.packets.StringPacket;
import eu.tankernn.chat.packets.filesend.FileSendDataPacket;
import eu.tankernn.chat.packets.filesend.FileSendInfoPacket;
import eu.tankernn.chat.packets.filesend.FileSendStatusPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ChatServerHandler extends ChannelInboundHandlerAdapter {
	private Client c;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof StringPacket) {
			if (msg.toString().equals("")) {
				ctx.writeAndFlush(InfoPacket.of(c));
				return;
			}
			if (c != null) {
				// Existing client
				c.handleMessage(msg.toString());
			} else {
				// New client
				c = new Client(ctx.channel(), msg.toString());
				if (!c.validateUser()) {
					ctx.close();
					return;
				} else
					Server.addClient(c);
			}
		} else if (msg instanceof FileSendInfoPacket) {
			if (!c.getFilePartner().isPresent()) {
				FileSendInfoPacket fileInfo = (FileSendInfoPacket) msg;
				Optional<Client> opt = Server.getUserByName(
						((FileSendInfoPacket) msg).destinationUser);
				if (opt.isPresent()) {
					c.setFilePartner(opt.get());
					opt.get().send(fileInfo);
				} else {
					ctx.writeAndFlush(new MessagePacket(
							"Unable to find user " + fileInfo.destinationUser + ".",
							MessageType.ERROR));
				}
			} else {
				ctx.writeAndFlush(new MessagePacket(
						"You are already transferring a file.",
						MessageType.ERROR));
			}
		} else if (msg instanceof FileSendStatusPacket) {
			FileSendStatusPacket s = ((FileSendStatusPacket) msg);
			switch (s) {
			case ACCEPT:
				c.getFilePartner().ifPresent((r) -> r.send((FileSendStatusPacket) msg));
				break;
			case DENY:
			case FINISHED:
				c.setFilePartner(null);
				break;
			default:
				break;
			}
		} else if (msg instanceof FileSendDataPacket) {
			// Just forward the packet
			c.getFilePartner().orElseThrow(IllegalStateException::new)
					.send((FileSendDataPacket) msg);
		}
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Server.wideBroadcast(
				new MessagePacket(c.username + " has disconnected."));
		Server.cleanUp();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}
}
