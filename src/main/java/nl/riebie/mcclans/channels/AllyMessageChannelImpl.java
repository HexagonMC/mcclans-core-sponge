/*
 * Copyright (c) 2016 riebie, Kippers <https://bitbucket.org/Kippers/mcclans-core-sponge>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package nl.riebie.mcclans.channels;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.api.Clan;
import nl.riebie.mcclans.api.channels.AllyMessageChannel;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.AbstractMutableMessageChannel;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by Kippers on 14/02/2016.
 */
public class AllyMessageChannelImpl extends AbstractMutableMessageChannel implements AllyMessageChannel {

    private ClanPlayerImpl clanPlayer;
    private boolean spy;

    private AllyMessageChannelImpl(@Nonnull ClanPlayerImpl clanPlayer, boolean spy) {
        this(new HashSet<>(), clanPlayer, spy);
    }

    private AllyMessageChannelImpl(Set<MessageReceiver> receivers, @Nonnull ClanPlayerImpl clanPlayer, boolean spy) {
        super(receivers);
        this.clanPlayer = clanPlayer;
        this.spy = spy;
    }

    public static MessageChannel getFor(@Nonnull ClanPlayerImpl clanPlayer) {
        ClanImpl clan = clanPlayer.getClan();
        if (clan == null) {
            return new AllyMessageChannelImpl(clanPlayer, false);
        }

        Set<MessageReceiver> spyReceivers = new HashSet<>();
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            ClanPlayerImpl clanPlayerSpy = ClansImpl.getInstance().getClanPlayer(player.getUniqueId());
            if (clanPlayerSpy.isSpy()) {
                spyReceivers.add(player);
            }
        }

        Set<MessageReceiver> normalReceivers = new HashSet<>();
        for (ClanPlayerImpl clanMember : clan.getMembersImpl()) {
            Optional<Player> playerOpt = Sponge.getServer().getPlayer(clanMember.getUUID());
            if (playerOpt.isPresent() && playerOpt.get().isOnline() && !clanMember.getIgnoreAllyChat()) {
                normalReceivers.add(playerOpt.get());
            }
        }
        for (ClanImpl ally : clan.getAlliesImpl()) {
            for (ClanPlayerImpl allyMember : ally.getMembersImpl()) {
                Optional<Player> playerOpt = Sponge.getServer().getPlayer(allyMember.getUUID());
                if (playerOpt.isPresent() && playerOpt.get().isOnline() && !allyMember.getIgnoreAllyChat()) {
                    normalReceivers.add(playerOpt.get());
                }
            }
        }

        return MessageChannel.combined(
                new AllyMessageChannelImpl(spyReceivers, clanPlayer, true),
                new AllyMessageChannelImpl(normalReceivers, clanPlayer, false)
        );
    }

    @Override
    public Optional<Text> transformMessage(@Nullable Object sender, MessageReceiver recipient, Text original, ChatType type) {
        Clan clan = clanPlayer.getClan();
        Text clanTagText = null;
        if (clan != null) {
            clanTagText = Text.join(
                    clan.getTagColored(),
                    Text.of(" ")
            );
        }

        Text newMessage = Text.join(
                Text.builder("[").color(TextColors.GRAY).build(),
                Text.builder("AC").color(TextColors.GOLD).build(),
                Text.builder("] ").color(TextColors.GRAY).build(),
                (clanTagText == null) ? Text.of("") : clanTagText,
                Text.of(clanPlayer.getName() + ": "),
                original.toBuilder().color(TextColors.GOLD).build()
        );

        if (spy) {
            newMessage = newMessage.toBuilder().style(TextStyles.ITALIC).build();
        }

        return Optional.of(newMessage);
    }

    @Override
    public List<Clan> getClans() {
        List<Clan> clans = new ArrayList<>();
        Clan clan = clanPlayer.getClan();
        if (clan == null) {
            return clans;
        }

        clans.add(clan);
        clans.addAll(clan.getAllies());
        return clans;
    }
}
