package mage.cards.h;

import mage.abilities.common.SpellCastOpponentTriggeredAbility;
import mage.abilities.condition.common.SourceIsEnchantmentCondition;
import mage.abilities.effects.common.continuous.BecomesCreatureSourceEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.SubType;
import mage.filter.StaticFilters;
import mage.game.permanent.token.custom.CreatureToken;

import java.util.UUID;

/**
 * @author LoneFox
 */
public final class HiddenAncients extends CardImpl {

    public HiddenAncients(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{1}{G}");

        // When an opponent casts an enchantment spell, if Hidden Ancients is an enchantment, Hidden Ancients becomes a 5/5 Treefolk creature.
        this.addAbility(new SpellCastOpponentTriggeredAbility(new BecomesCreatureSourceEffect(
                new CreatureToken(
                        5, 5, "5/5 Treefolk creature", SubType.TREEFOLK
                ), null, Duration.WhileOnBattlefield
        ), StaticFilters.FILTER_SPELL_AN_ENCHANTMENT, false)
                .withInterveningIf(SourceIsEnchantmentCondition.instance)
                .withRuleTextReplacement(true)
                .setTriggerPhrase("When an opponent casts an enchantment spell, "));
    }

    private HiddenAncients(final HiddenAncients card) {
        super(card);
    }

    @Override
    public HiddenAncients copy() {
        return new HiddenAncients(this);
    }
}
