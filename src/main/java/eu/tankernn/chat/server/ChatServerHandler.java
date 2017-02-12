package eu.tankernn.chat.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ChatServerHandler extends ChannelInboundHandlerAdapter {
	private Client c;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if (c != null) {
			// Existing client
			c.handleMessage((String) msg);
		} else {
			// New client
			c = new Client(ctx.channel(), (String) msg);
			if (!c.validateUser()) {
				ctx.close();
				return;
			} else
				Server.addClient(c);
		}
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		c.disconnect();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}
}
