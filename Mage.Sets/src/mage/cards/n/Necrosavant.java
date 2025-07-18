package mage.cards.n;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.condition.common.IsStepCondition;
import mage.abilities.costs.common.SacrificeTargetCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.common.ActivateIfConditionActivatedAbility;
import mage.abilities.effects.common.ReturnSourceFromGraveyardToBattlefieldEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.Zone;
import mage.filter.StaticFilters;

import java.util.UUID;

/**
 * @author Quercitron
 */
public final class Necrosavant extends CardImpl {

    public Necrosavant(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{3}{B}{B}{B}");
        this.subtype.add(SubType.ZOMBIE);
        this.subtype.add(SubType.GIANT);

        this.power = new MageInt(5);
        this.toughness = new MageInt(5);

        // {3}{B}{B}, Sacrifice a creature: Return Necrosavant from your graveyard to the battlefield. Activate this ability only during your upkeep.
        Ability ability = new ActivateIfConditionActivatedAbility(
                Zone.GRAVEYARD,
                new ReturnSourceFromGraveyardToBattlefieldEffect(false, false),
                new ManaCostsImpl<>("{3}{B}{B}"), IsStepCondition.getMyUpkeep()
        );
        ability.addCost(new SacrificeTargetCost(StaticFilters.FILTER_PERMANENT_CREATURE));
        this.addAbility(ability);
    }

    private Necrosavant(final Necrosavant card) {
        super(card);
    }

    @Override
    public Necrosavant copy() {
        return new Necrosavant(this);
    }
}
