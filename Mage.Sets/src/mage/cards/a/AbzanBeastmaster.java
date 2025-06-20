
package mage.cards.a;

import mage.MageInt;
import mage.abilities.condition.common.ControlsCreatureGreatestToughnessCondition;
import mage.abilities.decorator.ConditionalOneShotEffect;
import mage.abilities.effects.common.DrawCardSourceControllerEffect;
import mage.abilities.triggers.BeginningOfUpkeepTriggeredAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;

import java.util.UUID;

/**
 * @author LevelX2
 */
public final class AbzanBeastmaster extends CardImpl {

    public AbzanBeastmaster(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{2}{G}");
        this.subtype.add(SubType.DOG);
        this.subtype.add(SubType.SHAMAN);
        this.power = new MageInt(2);
        this.toughness = new MageInt(1);

        // At the beginning of your upkeep, draw a card if you control the creature with the greatest toughness or tied for the greatest toughness.
        this.addAbility(new BeginningOfUpkeepTriggeredAbility(new ConditionalOneShotEffect(
                new DrawCardSourceControllerEffect(1), ControlsCreatureGreatestToughnessCondition.instance,
                "draw a card if you control the creature with the greatest toughness or tied for the greatest toughness"
        )));
    }

    private AbzanBeastmaster(final AbzanBeastmaster card) {
        super(card);
    }

    @Override
    public AbzanBeastmaster copy() {
        return new AbzanBeastmaster(this);
    }
}
