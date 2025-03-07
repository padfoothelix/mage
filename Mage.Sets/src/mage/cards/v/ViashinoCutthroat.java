
package mage.cards.v;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.triggers.BeginningOfEndStepTriggeredAbility;
import mage.abilities.effects.common.ReturnToHandSourceEffect;
import mage.abilities.keyword.HasteAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.TargetController;

/**
 *
 * @author Plopman
 */
public final class ViashinoCutthroat extends CardImpl {

    public ViashinoCutthroat(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.CREATURE},"{2}{R}{R}");
        this.subtype.add(SubType.LIZARD);

        this.power = new MageInt(5);
        this.toughness = new MageInt(3);

        // Haste
        this.addAbility(HasteAbility.getInstance());
        // At the beginning of the end step, return Viashino Cutthroat to its owner's hand.
        this.addAbility(new BeginningOfEndStepTriggeredAbility(TargetController.NEXT, new ReturnToHandSourceEffect(true), false));
    }

    private ViashinoCutthroat(final ViashinoCutthroat card) {
        super(card);
    }

    @Override
    public ViashinoCutthroat copy() {
        return new ViashinoCutthroat(this);
    }
}
