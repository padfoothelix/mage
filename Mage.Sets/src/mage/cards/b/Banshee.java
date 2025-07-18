
package mage.cards.b;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.dynamicvalue.common.GetXValue;
import mage.abilities.dynamicvalue.common.HalfValue;
import mage.abilities.effects.common.DamageControllerEffect;
import mage.abilities.effects.common.DamageTargetEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.target.common.TargetAnyTarget;

import java.util.UUID;

/**
 *
 * @author L_J
 */
public final class Banshee extends CardImpl {

    public Banshee(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.CREATURE},"{2}{B}{B}");
        this.subtype.add(SubType.SPIRIT);
        this.power = new MageInt(0);
        this.toughness = new MageInt(1);

        // {X}, {T}: Banshee deals half X damage, rounded down, to any target, and half X damage, rounded up, to you.
        Ability ability = new SimpleActivatedAbility(new DamageTargetEffect(new HalfValue(GetXValue.instance, false)).setText("{this} deals half X damage, rounded down, to any target,"), new ManaCostsImpl<>("{X}"));
        ability.addCost(new TapSourceCost());
        ability.addEffect(new DamageControllerEffect(new HalfValue(GetXValue.instance, true)).setText(" and half X damage, rounded up, to you"));
        ability.addTarget(new TargetAnyTarget());
        this.addAbility(ability);
    }

    private Banshee(final Banshee card) {
        super(card);
    }

    @Override
    public Banshee copy() {
        return new Banshee(this);
    }
}
