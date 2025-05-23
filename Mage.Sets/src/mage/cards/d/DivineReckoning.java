
package mage.cards.d;

import mage.abilities.Ability;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.keyword.FlashbackAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.filter.StaticFilters;
import mage.filter.common.FilterControlledCreaturePermanent;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.Target;
import mage.target.common.TargetControlledPermanent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author nantuko
 */
public final class DivineReckoning extends CardImpl {

    public DivineReckoning(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.SORCERY}, "{2}{W}{W}");

        // Each player chooses a creature they control. Destroy the rest.
        this.getSpellAbility().addEffect(new DivineReckoningEffect());

        // Flashback {5}{W}{W}
        this.addAbility(new FlashbackAbility(this, new ManaCostsImpl<>("{5}{W}{W}")));
    }

    private DivineReckoning(final DivineReckoning card) {
        super(card);
    }

    @Override
    public DivineReckoning copy() {
        return new DivineReckoning(this);
    }
}

class DivineReckoningEffect extends OneShotEffect {

    DivineReckoningEffect() {
        super(Outcome.DestroyPermanent);
        staticText = "Each player chooses a creature they control. Destroy the rest";
    }

    private DivineReckoningEffect(final DivineReckoningEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        List<Card> chosen = new ArrayList<>();
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            for (UUID playerId : game.getState().getPlayersInRange(controller.getId(), game)) {
                Player player = game.getPlayer(playerId);
                if (player != null) {
                    Target target = new TargetControlledPermanent(1, 1, new FilterControlledCreaturePermanent(), true);
                    if (player.chooseTarget(Outcome.Benefit, target, source, game)) {
                        Permanent permanent = game.getPermanent(target.getFirstTarget());
                        if (permanent != null) {
                            chosen.add(permanent);
                        }
                    }
                }
            }

            for (Permanent permanent : game.getBattlefield().getActivePermanents(StaticFilters.FILTER_PERMANENT_CREATURES, source.getControllerId(), source, game)) {
                if (!chosen.contains(permanent)) {
                    permanent.destroy(source, game, false);
                }
            }
            return true;
        }
        return false;

    }

    @Override
    public DivineReckoningEffect copy() {
        return new DivineReckoningEffect(this);
    }
}
