package eu.tankernn.chat.packets.filesend;

import eu.tankernn.chat.packets.Packet;

public enum FileSendStatusPacket implements Packet {
	ACCEPT, DENY, FINISHED
}
