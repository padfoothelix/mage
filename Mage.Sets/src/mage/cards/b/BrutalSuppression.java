
package mage.cards.b;

import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.common.SacrificeTargetCost;
import mage.abilities.effects.common.cost.CostModificationEffectImpl;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterControlledPermanent;
import mage.filter.predicate.permanent.TokenPredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;

import java.util.UUID;

/**
 * @author L_J
 */
public final class BrutalSuppression extends CardImpl {

    public BrutalSuppression(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{R}");

        // Activated abilities of nontoken Rebels cost an additional "Sacrifice a land" to activate.
        this.addAbility(new SimpleStaticAbility(new BrutalSuppressionAdditionalCostEffect()));
    }

    private BrutalSuppression(final BrutalSuppression card) {
        super(card);
    }

    @Override
    public BrutalSuppression copy() {
        return new BrutalSuppression(this);
    }
}

class BrutalSuppressionAdditionalCostEffect extends CostModificationEffectImpl {

    private static final FilterControlledPermanent filter = new FilterControlledPermanent("a land");

    static {
        filter.add(CardType.LAND.getPredicate());
    }

    private static final FilterPermanent filter2 = new FilterPermanent("nontoken Rebels");

    static {
        filter2.add(SubType.REBEL.getPredicate());
        filter.add(TokenPredicate.FALSE);
    }

    BrutalSuppressionAdditionalCostEffect() {
        super(Duration.WhileOnBattlefield, Outcome.Benefit, CostModificationType.INCREASE_COST);
        this.staticText = "Activated abilities of nontoken Rebels cost an additional \"Sacrifice a land\" to activate";
    }

    private BrutalSuppressionAdditionalCostEffect(final BrutalSuppressionAdditionalCostEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source, Ability abilityToModify) {
        abilityToModify.addCost(new SacrificeTargetCost(filter));
        return true;
    }

    @Override
    public boolean applies(Ability abilityToModify, Ability source, Game game) {
        if (abilityToModify.isActivatedAbility()){
            Permanent rebelPermanent = game.getPermanent(abilityToModify.getSourceId());
            if (rebelPermanent != null) {
                return filter2.match(rebelPermanent, game);
            }
        }
        return false;
    }

    @Override
    public BrutalSuppressionAdditionalCostEffect copy() {
        return new BrutalSuppressionAdditionalCostEffect(this);
    }
}
