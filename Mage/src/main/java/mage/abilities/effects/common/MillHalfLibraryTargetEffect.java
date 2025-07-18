package mage.abilities.effects.common;

import mage.abilities.Ability;
import mage.abilities.Mode;
import mage.abilities.effects.OneShotEffect;
import mage.constants.Outcome;
import mage.game.Game;
import mage.players.Player;

import java.util.UUID;

/**
 * @author TheElk801
 */
public class MillHalfLibraryTargetEffect extends OneShotEffect {

    private final boolean roundUp;

    public MillHalfLibraryTargetEffect(boolean roundUp) {
        super(Outcome.Benefit);
        this.roundUp = roundUp;
    }

    private MillHalfLibraryTargetEffect(final MillHalfLibraryTargetEffect effect) {
        super(effect);
        this.roundUp = effect.roundUp;
    }

    @Override
    public MillHalfLibraryTargetEffect copy() {
        return new MillHalfLibraryTargetEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        for (UUID playerId : getTargetPointer().getTargets(game, source)) {
            Player player = game.getPlayer(playerId);
            if (player == null) {
                return false;
            }
            int count = player.getLibrary().size();
            player.millCards(count / 2 + (roundUp ? count % 2 : 0), source, game);
        }
        return true;
    }

    @Override
    public String getText(Mode mode) {
        if (staticText != null && !staticText.isEmpty()) {
            return staticText;
        }
        return getTargetPointer().describeTargets(mode.getTargets(), "that player") +
                " mills half their library, rounded " + (roundUp ? "up" : "down");
    }
}
