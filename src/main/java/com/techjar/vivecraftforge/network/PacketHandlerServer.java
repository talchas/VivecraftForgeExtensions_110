package com.techjar.vivecraftforge.network;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Sharable
public class PacketHandlerServer extends SimpleChannelInboundHandler<IPacket> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IPacket msg) throws Exception {
		if (ctx.channel().attr(NetworkRegistry.CHANNEL_SOURCE).get() != Side.SERVER) return;
		EntityPlayerMP player = ((NetHandlerPlayServer)ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).player;
		msg.handleServer(player);
	}
}
