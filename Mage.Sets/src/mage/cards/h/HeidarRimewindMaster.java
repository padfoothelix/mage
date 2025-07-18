package mage.cards.h;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.ActivateIfConditionActivatedAbility;
import mage.abilities.condition.Condition;
import mage.abilities.condition.common.PermanentsOnTheBattlefieldCondition;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.effects.common.ReturnToHandTargetEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.ComparisonType;
import mage.constants.SubType;
import mage.constants.SuperType;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterControlledPermanent;
import mage.target.TargetPermanent;

import java.util.UUID;

/**
 * @author fireshoes
 */
public final class HeidarRimewindMaster extends CardImpl {

    private static final FilterPermanent filter = new FilterControlledPermanent("you control four or more snow permanents");

    static {
        filter.add(SuperType.SNOW.getPredicate());
    }

    private static final Condition condition = new PermanentsOnTheBattlefieldCondition(filter, ComparisonType.MORE_THAN, 3);

    public HeidarRimewindMaster(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{4}{U}");
        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.HUMAN);
        this.subtype.add(SubType.WIZARD);
        this.power = new MageInt(3);
        this.toughness = new MageInt(3);

        // {2}, {tap}: Return target permanent to its owner's hand. Activate this ability only if you control four or more snow permanents.
        Ability ability = new ActivateIfConditionActivatedAbility(
                new ReturnToHandTargetEffect(), new GenericManaCost(2), condition
        );
        ability.addCost(new TapSourceCost());
        ability.addTarget(new TargetPermanent());
        this.addAbility(ability);
    }

    private HeidarRimewindMaster(final HeidarRimewindMaster card) {
        super(card);
    }

    @Override
    public HeidarRimewindMaster copy() {
        return new HeidarRimewindMaster(this);
    }
}
