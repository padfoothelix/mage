
package mage.cards.k;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.triggers.BeginningOfUpkeepTriggeredAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.common.SacrificeSourceUnlessPaysEffect;
import mage.abilities.keyword.MorphAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;

/**
 *
 * @author LevelX2
 */
public final class KrosanCloudscraper extends CardImpl {

    public KrosanCloudscraper(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.CREATURE},"{7}{G}{G}{G}");
        this.subtype.add(SubType.BEAST);
        this.subtype.add(SubType.MUTANT);

        this.power = new MageInt(13);
        this.toughness = new MageInt(13);

        // At the beginning of your upkeep, sacrifice Krosan Cloudscraper unless you pay {G}{G}.
        this.addAbility(new BeginningOfUpkeepTriggeredAbility(new SacrificeSourceUnlessPaysEffect(new ManaCostsImpl<>("{G}{G}"))));

        // Morph {7}{G}{G}
        this.addAbility(new MorphAbility(this, new ManaCostsImpl<>("{7}{G}{G}")));
    }

    private KrosanCloudscraper(final KrosanCloudscraper card) {
        super(card);
    }

    @Override
    public KrosanCloudscraper copy() {
        return new KrosanCloudscraper(this);
    }
}
