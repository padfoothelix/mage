package mage.cards.l;

import mage.abilities.effects.Effect;
import mage.abilities.effects.common.continuous.GainAbilityControlledEffect;
import mage.abilities.effects.common.continuous.GainAbilityControllerEffect;
import mage.abilities.effects.keyword.AmassEffect;
import mage.abilities.keyword.HexproofAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.SubType;
import mage.filter.StaticFilters;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class LazotepPlating extends CardImpl {

    public LazotepPlating(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{1}{U}");

        // Amass 1.
        this.getSpellAbility().addEffect(new AmassEffect(1, SubType.ZOMBIE));

        // You and permanents you control gain hexproof until end of turn.
        Effect effect = new GainAbilityControllerEffect(
                HexproofAbility.getInstance(), Duration.EndOfTurn
        );
        Effect effect2 = new GainAbilityControlledEffect(
                HexproofAbility.getInstance(), Duration.EndOfTurn, StaticFilters.FILTER_PERMANENTS
        );
        effect.setText("You");
        effect2.setText("and permanents you control gain hexproof until end of turn");
        this.getSpellAbility().addEffect(effect.concatBy("<br>"));
        this.getSpellAbility().addEffect(effect2);
    }

    private LazotepPlating(final LazotepPlating card) {
        super(card);
    }

    @Override
    public LazotepPlating copy() {
        return new LazotepPlating(this);
    }
}
