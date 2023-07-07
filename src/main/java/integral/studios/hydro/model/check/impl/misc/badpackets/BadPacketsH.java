package integral.studios.hydro.model.check.impl.misc.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientKeepAlive;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.check.type.PacketCheck;
import integral.studios.hydro.model.check.violation.category.Category;
import integral.studios.hydro.model.check.violation.category.SubCategory;
import integral.studios.hydro.model.check.violation.handler.ViolationHandler;
import integral.studios.hydro.model.check.violation.impl.PlayerViolation;

import java.net.InetAddress;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 5/16/2023
 */
public class BadPacketsH extends PacketCheck {

    public BadPacketsH(PlayerData playerData) {
        super(playerData, "Bad Packets H", "Spoofed KeepAlive packet", new ViolationHandler(2, 60L), Category.MISC, SubCategory.BAD_PACKETS);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.KEEP_ALIVE) {
            WrapperPlayClientKeepAlive keepAlive = new WrapperPlayClientKeepAlive(event);

            if (keepAlive.getId() == 0) {
                handleViolation(new PlayerViolation(this));
            }
        }
    }
}
