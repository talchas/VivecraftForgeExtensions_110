package com.techjar.vivecraftforge.network;


import com.techjar.vivecraftforge.util.VRPlayerData;
import com.techjar.vivecraftforge.util.PlayerTracker;
import com.techjar.vivecraftforge.util.Quaternion;
import com.techjar.vivecraftforge.util.LogHelper;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;


import net.minecraft.util.math.Vec3d;

import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraft.network.NetHandlerPlayServer;

import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class VanillaWrapping extends ChannelInboundHandlerAdapter {
    private final NetworkManager manager;

    public VanillaWrapping(NetworkManager manager) {
        this.manager = manager;
    }
    @SubscribeEvent
	public static void onServerConnection(FMLNetworkEvent.ServerConnectionFromClientEvent evt) {
        ChannelPipeline pipeline = evt.getManager().channel().pipeline();
        if (pipeline.get("vivecraft mod aim fix") == null)
            pipeline.addBefore("packet_handler", "vivecraft mod aim fix", new VanillaWrapping(evt.getManager()));
	}


    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof CPacketPlayerTryUseItemOnBlock || msg instanceof CPacketPlayerTryUseItem || msg instanceof CPacketPlayerDigging) {

            final EntityPlayerMP player = ((NetHandlerPlayServer)manager.getNetHandler()).player;
            VRPlayerData data = PlayerTracker.getPlayerData(player, false);

            if (data == null) {
                ctx.fireChannelRead(msg);
                return;
            }

            player.getServerWorld().addScheduledTask(new Runnable() {
                    @Override
                    public void run() {
                        VRPlayerData data = PlayerTracker.getPlayerData(player, false);

                        VRPlayerData.ObjectInfo info = data.controller0;
                        Vec3d forward = new Vec3d(0, 0, -1);
                        Quaternion rot = new Quaternion(info.rotW, info.rotX, info.rotY, info.rotZ);
                        Vec3d dir = rot.multiply(forward);

                        double oldX,oldY,oldZ;
                        oldX = player.posX;
                        oldY = player.posY;
                        oldZ = player.posZ;
                        player.posX = info.posX;
                        player.posY = info.posY - player.getEyeHeight();
                        player.posZ = info.posZ;

            
                        float oldYaw = player.rotationYaw;
                        float oldPitch = player.rotationPitch;
                        player.rotationPitch = (float)Math.toDegrees(Math.asin(-dir.y));
                        player.rotationYaw = (float)Math.toDegrees(Math.atan2(-dir.x,dir.z));

                        float oldYawH = player.rotationYawHead;
                        float oldpYawH = player.prevRotationYawHead;
                        float oldpYaw = player.prevRotationYaw;
                        float oldpPitch = player.prevRotationPitch;
                        player.prevRotationPitch = player.rotationPitch;
                        player.prevRotationYaw = player.prevRotationYawHead = player.rotationYawHead = player.rotationYaw;


                        // inline packet_handler's handler
                        try {
                            if (manager.isChannelOpen())
                            {
                                try
                                {
                                    ((Packet<INetHandler>)msg).processPacket(manager.getNetHandler());
                                }
                                catch (ThreadQuickExitException e)
                                {
                                    ;
                                }
                            }
                        } finally {
                            ReferenceCountUtil.release(msg);
                        }

                        player.posX = oldX;
                        player.posY = oldY;
                        player.posZ = oldZ;

                        player.rotationYaw = oldYaw;
                        player.rotationYawHead = oldYawH;
                        player.rotationPitch = oldPitch;
                        player.prevRotationYaw = oldpYaw;
                        player.prevRotationYawHead = oldpYawH;
                        player.prevRotationPitch = oldpPitch;
                    }
                });

        } else {
            ctx.fireChannelRead(msg);
        }
    }

}
	
