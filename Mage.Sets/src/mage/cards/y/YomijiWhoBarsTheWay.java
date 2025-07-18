
package mage.cards.y;

import mage.MageInt;
import mage.abilities.common.PutIntoGraveFromBattlefieldAllTriggeredAbility;
import mage.abilities.effects.Effect;
import mage.abilities.effects.common.ReturnToHandTargetEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.SuperType;
import mage.filter.FilterPermanent;
import mage.filter.predicate.mageobject.AnotherPredicate;

import java.util.UUID;

/**
 * @author LevelX2
 */
public final class YomijiWhoBarsTheWay extends CardImpl {
    private static final FilterPermanent filter = new FilterPermanent("a legendary permanent other than {this}");

    static {
        filter.add(AnotherPredicate.instance);
        filter.add(SuperType.LEGENDARY.getPredicate());
    }

    public YomijiWhoBarsTheWay(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{5}{W}{W}");
        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.SPIRIT);

        this.power = new MageInt(4);
        this.toughness = new MageInt(4);

        // Whenever a legendary permanent other than Yomiji, Who Bars the Way is put into a graveyard from the battlefield, return that card to its owner's hand.
        Effect effect = new ReturnToHandTargetEffect().setText("return that card to its owner's hand");
        this.addAbility(new PutIntoGraveFromBattlefieldAllTriggeredAbility(effect, false, filter, true));
    }

    private YomijiWhoBarsTheWay(final YomijiWhoBarsTheWay card) {
        super(card);
    }

    @Override
    public YomijiWhoBarsTheWay copy() {
        return new YomijiWhoBarsTheWay(this);
    }
}
