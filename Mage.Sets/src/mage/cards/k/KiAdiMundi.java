package mage.cards.k;

import mage.MageInt;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.common.counter.AddCountersAllEffect;
import mage.abilities.keyword.MeditateAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.SuperType;
import mage.counters.CounterType;
import mage.filter.StaticFilters;

import java.util.UUID;

/**
 * @author Styxo
 */
public final class KiAdiMundi extends CardImpl {

    public KiAdiMundi(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{3}{G}{G}");
        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.CEREAN);
        this.subtype.add(SubType.JEDI);
        this.power = new MageInt(4);
        this.toughness = new MageInt(4);

        // When Ki-Adi-Mundi enters the battlefield, put a +1/+1 counter on each other creature you control.
        this.addAbility(new EntersBattlefieldTriggeredAbility(new AddCountersAllEffect(CounterType.P1P1.createInstance(), StaticFilters.FILTER_OTHER_CONTROLLED_CREATURE)));

        // Meditate {1}{G}
        this.addAbility(new MeditateAbility(new ManaCostsImpl<>("{1}{G}")));
    }

    private KiAdiMundi(final KiAdiMundi card) {
        super(card);
    }

    @Override
    public KiAdiMundi copy() {
        return new KiAdiMundi(this);
    }
}
