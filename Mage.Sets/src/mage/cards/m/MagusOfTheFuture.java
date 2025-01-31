package mage.cards.m;

import mage.MageInt;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.common.continuous.PlayFromTopOfLibraryEffect;
import mage.abilities.effects.common.continuous.PlayWithTheTopCardRevealedEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.Zone;

import java.util.UUID;

/**
 * @author emerald000
 */
public final class MagusOfTheFuture extends CardImpl {

    public MagusOfTheFuture(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{2}{U}{U}{U}");
        this.subtype.add(SubType.HUMAN);
        this.subtype.add(SubType.WIZARD);

        this.power = new MageInt(2);
        this.toughness = new MageInt(3);

        // Play with the top card of your library revealed.
        this.addAbility(new SimpleStaticAbility(new PlayWithTheTopCardRevealedEffect()));

        // You may play lands and cast spells from the top of your library.
        this.addAbility(new SimpleStaticAbility(new PlayFromTopOfLibraryEffect()));
    }

    private MagusOfTheFuture(final MagusOfTheFuture card) {
        super(card);
    }

    @Override
    public MagusOfTheFuture copy() {
        return new MagusOfTheFuture(this);
    }
}
