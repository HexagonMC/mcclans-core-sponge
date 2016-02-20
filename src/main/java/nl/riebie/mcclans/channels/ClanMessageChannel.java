package nl.riebie.mcclans.channels;

import nl.riebie.mcclans.api.ClanPlayer;
import nl.riebie.mcclans.api.Rank;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.AbstractMutableMessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Koen on 14/02/2016.
 */
public class ClanMessageChannel extends AbstractMutableMessageChannel {

    private ClanPlayerImpl clanPlayer;

    protected ClanMessageChannel(@Nonnull ClanPlayerImpl clanPlayer) {
        this(new HashSet<>(), clanPlayer);
    }

    protected ClanMessageChannel(Set<MessageReceiver> receivers, @Nonnull ClanPlayerImpl clanPlayer) {
        super(receivers);
        this.clanPlayer = clanPlayer;
    }

    public static ClanMessageChannel getFor(@Nonnull ClanPlayerImpl clanPlayer) {
        ClanImpl clan = clanPlayer.getClan();
        if (clan == null) {
            return new ClanMessageChannel(clanPlayer);
        }

        Set<MessageReceiver> receivers = new HashSet<>();
        for (ClanPlayer clanMember : clan.getMembers()) {
            Optional<Player> playerOpt = Sponge.getServer().getPlayer(clanMember.getUUID());
            if (playerOpt.isPresent()) {
                Player player = playerOpt.get();
                if (player.isOnline()) {
                    receivers.add(player);
                }
            }
        }

        return new ClanMessageChannel(receivers, clanPlayer);
    }

    @Override
    public Optional<Text> transformMessage(@Nullable Object sender, MessageReceiver recipient, Text original) {
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

        return Optional.of(newMessage);
    }
}
