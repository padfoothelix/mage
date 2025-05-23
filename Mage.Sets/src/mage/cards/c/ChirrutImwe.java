package mage.cards.c;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.abilities.effects.Effect;
import mage.abilities.effects.common.PreventCombatDamageToSourceEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.game.Game;
import mage.game.permanent.Permanent;

import java.util.UUID;

/**
 * @author NinthWorld
 */
public final class ChirrutImwe extends CardImpl {

    public ChirrutImwe(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{G}{W}{U}");

        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.HUMAN);
        this.subtype.add(SubType.MONK);
        this.power = new MageInt(3);
        this.toughness = new MageInt(3);

        // Chirrut Imwe can block up to two additional creatures.
        this.addAbility(new SimpleStaticAbility(new ChirrutImweEffect()));

        // {1}{W}: Prevent all combat damage that would be dealt to Chirrut Imwe until end of turn.
        Effect effect = new PreventCombatDamageToSourceEffect(Duration.EndOfTurn);
        effect.setText("Prevent all combat damage that would be dealt to {this} until end of turn");
        this.addAbility(new SimpleActivatedAbility(effect, new ManaCostsImpl<>("{1}{W}")));
    }

    private ChirrutImwe(final ChirrutImwe card) {
        super(card);
    }

    @Override
    public ChirrutImwe copy() {
        return new ChirrutImwe(this);
    }
}

class ChirrutImweEffect extends ContinuousEffectImpl {

    public ChirrutImweEffect() {
        super(Duration.WhileOnBattlefield, Outcome.Benefit);
        staticText = "{this} can block up to two additional creatures";
    }

    private ChirrutImweEffect(final ChirrutImweEffect effect) {
        super(effect);
    }

    @Override
    public ChirrutImweEffect copy() {
        return new ChirrutImweEffect(this);
    }

    @Override
    public boolean apply(Layer layer, SubLayer sublayer, Ability source, Game game) {
        Permanent perm = game.getPermanent(source.getSourceId());
        if (perm != null) {
            switch (layer) {
                case RulesEffects:
                    // maxBlocks = 0 equals to "can block any number of creatures"
                    if (perm.getMaxBlocks() > 0) {
                        perm.setMaxBlocks(perm.getMaxBlocks() + 2);
                    }
                    break;
            }
        }
        return false;
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return false;
    }

    @Override
    public boolean hasLayer(Layer layer) {
        return layer == Layer.RulesEffects;
    }
}
