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
import nl.riebie.mcclans.api.Rank;
import nl.riebie.mcclans.api.channels.ClanMessageChannel;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Kippers on 14/02/2016.
 */
public class ClanMessageChannelImpl extends AbstractMutableMessageChannel implements ClanMessageChannel {

    private ClanPlayerImpl clanPlayer;
    private boolean spy;

    private ClanMessageChannelImpl(@Nonnull ClanPlayerImpl clanPlayer, boolean spy) {
        this(new HashSet<>(), clanPlayer, spy);
    }

    private ClanMessageChannelImpl(Set<MessageReceiver> receivers, @Nonnull ClanPlayerImpl clanPlayer, boolean spy) {
        super(receivers);
        this.clanPlayer = clanPlayer;
        this.spy = spy;
    }

    public static MessageChannel getFor(@Nonnull ClanPlayerImpl clanPlayer) {
        ClanImpl clan = clanPlayer.getClan();
        if (clan == null) {
            return new ClanMessageChannelImpl(clanPlayer, false);
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
            if (playerOpt.isPresent() && !clanMember.getIgnoreClanChat()) {
                Player player = playerOpt.get();
                if (player.isOnline()) {
                    normalReceivers.add(player);
                }
            }
        }

        return MessageChannel.combined(
                new ClanMessageChannelImpl(spyReceivers, clanPlayer, true),
                new ClanMessageChannelImpl(normalReceivers, clanPlayer, false)
        );
    }

    @Override
    public Optional<Text> transformMessage(@Nullable Object sender, MessageReceiver recipient, Text original, ChatType type) {
        Rank rank = clanPlayer.getRank();
        Text rankText = null;
        if (rank != null) {
            rankText = Text.join(
                    Text.builder("[").color(TextColors.GRAY).build(),
                    Text.builder(rank.getName()).color(TextColors.BLUE).build(),
                    Text.builder("] ").color(TextColors.GRAY).build()
            );
        }

        Text newMessage = Text.join(
                Text.builder("[").color(TextColors.GRAY).build(),
                Text.builder("CC").color(TextColors.YELLOW).build(),
                Text.builder("] ").color(TextColors.GRAY).build(),
                (rankText == null) ? Text.of("") : rankText,
                Text.of(clanPlayer.getName() + ": "),
                original.toBuilder().color(TextColors.YELLOW).build()
        );

        if (spy) {
            newMessage = newMessage.toBuilder().style(TextStyles.ITALIC).build();
        }

        return Optional.of(newMessage);
    }

    @Override
    public Clan getClan() {
        return clanPlayer.getClan();
    }
}
