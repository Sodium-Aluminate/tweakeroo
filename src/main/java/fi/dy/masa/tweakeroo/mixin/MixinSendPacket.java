package fi.dy.masa.tweakeroo.mixin;

import fi.dy.masa.tweakeroo.util.PendingPackets;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ClientCommonNetworkHandler.class)
public class MixinSendPacket {
    @Shadow
    protected ClientConnection connection;


    @Inject(method = "sendPacket(Lnet/minecraft/network/packet/Packet;)V", at = @At("TAIL"))
    private void b(Packet<?> packet, CallbackInfo ci) {
        List<Packet<?>> pendingPackets = PendingPackets.getPackets(packet.getClass());
        if (pendingPackets == null) return;
        pendingPackets.forEach(p -> this.connection.send(p));
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/packet/Packet;)V", at = @At("TAIL"))
    private void a(Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof KeepAliveC2SPacket || packet instanceof PlayerMoveC2SPacket || packet instanceof AcknowledgeChunksC2SPacket)
            return;
        System.out.println(packet.getClass());

        if (packet instanceof UpdateSelectedSlotC2SPacket p) {
            System.out.println(p.getSelectedSlot());
            printTrace();
        } else if (packet instanceof PlayerInteractBlockC2SPacket p || packet instanceof PlayerInteractItemC2SPacket p2) {
            printTrace();
        }
    }

    private void printTrace() {
        try {
            throw new Exception();
        } catch (Exception e) {e.printStackTrace();}
    }
}


//[22:05:47] [Render thread/INFO] (Minecraft) [STDOUT]: class net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket
//[22:05:47] [Render thread/INFO] (Minecraft) [STDOUT]: class net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
//[22:05:47] [Render thread/INFO] (Minecraft) [STDOUT]: class net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket 
// we can found that there are the 2nd Interact packet sent after the 1st swap packet
// which means we switch the hotbar first, then use the water bucket.
// so we should switch it later.
//[22:05:47] [Render thread/INFO] (Minecraft) [STDOUT]: class net.minecraft.network.packet.c2s.play.HandSwingC2SPacket
//[22:05:47] [Render thread/INFO] (Minecraft) [STDOUT]: class net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
//[22:05:53] [Render thread/INFO] (Minecraft) [STDOUT]: class net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket

//[22:06:05] [Render thread/INFO] (Minecraft) [STDOUT]: class net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket
//[22:06:05] [Render thread/INFO] (Minecraft) [STDOUT]: class net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
//[22:06:05] [Render thread/INFO] (Minecraft) [STDOUT]: class net.minecraft.network.packet.c2s.play.HandSwingC2SPacket
//[22:06:05] [Render thread/INFO] (Minecraft) [STDOUT]: class net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
//[22:06:09] [Render thread/INFO] (Minecraft) [STDOUT]: class net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket