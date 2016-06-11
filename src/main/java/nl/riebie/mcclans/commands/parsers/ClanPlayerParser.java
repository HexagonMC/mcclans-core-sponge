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

package nl.riebie.mcclans.commands.parsers;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.api.ClanPlayer;
import nl.riebie.mcclans.commands.filledparameters.NormalFilledParameter;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.utils.UUIDUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.LocatedSource;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.selector.Selector;

import java.util.*;

/**
 * Created by riebie on 12/03/2016.
 */
public class ClanPlayerParser implements ParameterParser<ClanPlayer> {
    @Override
    public ParseResult<ClanPlayer> parseValue(CommandSource commandSource, String value, NormalFilledParameter parameter) {
        ClanPlayerImpl clanPlayer = null;
        if (value.startsWith("@p")) {
            clanPlayer = tempParseNearestPlayer(commandSource);
            if (clanPlayer == null) {
                return ParseResult.newErrorResult("Selector produced no results");
            }
        } else if (value.startsWith("@")) {
            Set<Entity> entities = Selector.parse(value).resolve(commandSource);
            if (entities.size() > 0 && entities.toArray()[0] instanceof Player) {
                UUID uuid = ((Player) entities.toArray()[0]).getUniqueId();
                clanPlayer = ClansImpl.getInstance().getClanPlayer(uuid);
            } else {
                return ParseResult.newErrorResult("Selector produced no results");
            }

        } else {
            clanPlayer = ClansImpl.getInstance().getClanPlayer(value);
        }


        if (clanPlayer != null) {
            return ParseResult.newSuccessResult((ClanPlayer) clanPlayer);
        } else {
            UUID playerUUID = UUIDUtils.getUUID(value);
            Optional<Player> playerOp = playerUUID == null ? Optional.empty() : Sponge.getServer().getPlayer(playerUUID);
            if (playerOp.isPresent()) {
                return ParseResult.newSuccessResult(ClansImpl.getInstance().createClanPlayer(playerUUID, value));
            } else {
                return ParseResult.newErrorResult(Messages.PLAYER_DOES_NOT_EXIST);
            }
        }
    }

    /**
     * TODO remove once https://github.com/SpongePowered/SpongeCommon/issues/791 is fixed
     */
    private ClanPlayerImpl tempParseNearestPlayer(CommandSource commandSource) {
        if (!(commandSource instanceof LocatedSource)) {
            return null;
        }

        Vector3d commandSourcePosition = ((LocatedSource) commandSource).getLocation().getPosition();
        Collection<Player> onlinePlayers = Sponge.getServer().getOnlinePlayers();
        List<Player> sortedPlayers = Lists.newArrayList(onlinePlayers);
        Collections.sort(sortedPlayers, Comparator.comparing(player -> commandSourcePosition.sub(player.getLocation().getPosition()).abs()));
        if (!onlinePlayers.isEmpty()) {
            UUID uuid = sortedPlayers.get(0).getUniqueId();
            return ClansImpl.getInstance().getClanPlayer(uuid);
        } else {
            return null;
        }
    }
}
