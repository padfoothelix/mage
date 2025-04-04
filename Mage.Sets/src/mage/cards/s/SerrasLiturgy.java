package mage.cards.s;

import mage.abilities.Ability;
import mage.abilities.triggers.BeginningOfUpkeepTriggeredAbility;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.common.SacrificeSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.dynamicvalue.common.CountersSourceCount;
import mage.abilities.effects.Effect;
import mage.abilities.effects.common.DestroyTargetEffect;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Zone;
import mage.counters.CounterType;
import mage.filter.FilterPermanent;
import mage.filter.predicate.Predicates;
import mage.target.TargetPermanent;
import mage.target.targetadjustment.TargetsCountAdjuster;

import java.util.UUID;

/**
 *
 * @author TheElk801
 */
public final class SerrasLiturgy extends CardImpl {

    private static final FilterPermanent filter = new FilterPermanent("up to X target artifacts and/or enchantments, where X is the number of verse counters on {this}");

    static {
        filter.add(Predicates.or(
                CardType.ARTIFACT.getPredicate(),
                CardType.ENCHANTMENT.getPredicate()
        ));
    }

    public SerrasLiturgy(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{2}{W}{W}");

        // At the beginning of your upkeep, you may put a verse counter on Serra's Liturgy.
        this.addAbility(new BeginningOfUpkeepTriggeredAbility(
                new AddCountersSourceEffect(CounterType.VERSE.createInstance(), true), true));

        // {W}, Sacrifice Serra's Liturgy: Destroy up to X target artifacts and/or enchantments, where X is the number of verse counters on Serra's Liturgy.
        Effect effect = new DestroyTargetEffect(true);
        effect.setText("Destroy up to X target artifacts and/or enchantments, where X is the number of verse counters on {this}.");
        Ability ability = new SimpleActivatedAbility(effect, new ManaCostsImpl<>("{W}"));
        ability.addCost(new SacrificeSourceCost());
        ability.addTarget(new TargetPermanent(0, 0, filter, false));
        ability.setTargetAdjuster(new TargetsCountAdjuster(new CountersSourceCount(CounterType.VERSE)));
        this.addAbility(ability);
    }

    private SerrasLiturgy(final SerrasLiturgy card) {
        super(card);
    }

    @Override
    public SerrasLiturgy copy() {
        return new SerrasLiturgy(this);
    }
}
