package mage.sets;

import mage.cards.ExpansionSet;
import mage.constants.Rarity;
import mage.constants.SetType;

/**
 * @author TheElk801
 */
public final class ModernHorizons1Timeshifts extends ExpansionSet {

    private static final ModernHorizons1Timeshifts instance = new ModernHorizons1Timeshifts();

    public static ModernHorizons1Timeshifts getInstance() {
        return instance;
    }

    private ModernHorizons1Timeshifts() {
        super("Modern Horizons 1 Timeshifts", "H1R", ExpansionSet.buildDate(2021, 6, 11), SetType.SUPPLEMENTAL);
        this.hasBasicLands = false;
        this.hasBoosters = false;

        cards.add(new SetCardInfo("Archmage's Charm", 7, Rarity.RARE, mage.cards.a.ArchmagesCharm.class, RETRO_ART));
        cards.add(new SetCardInfo("Ayula, Queen Among Bears", 19, Rarity.RARE, mage.cards.a.AyulaQueenAmongBears.class, RETRO_ART));
        cards.add(new SetCardInfo("Changeling Outcast", 12, Rarity.UNCOMMON, mage.cards.c.ChangelingOutcast.class, RETRO_ART));
        cards.add(new SetCardInfo("Deep Forest Hermit", 20, Rarity.RARE, mage.cards.d.DeepForestHermit.class, RETRO_ART));
        cards.add(new SetCardInfo("Defile", 13, Rarity.UNCOMMON, mage.cards.d.Defile.class, RETRO_ART));
        cards.add(new SetCardInfo("Ephemerate", 1, Rarity.UNCOMMON, mage.cards.e.Ephemerate.class, RETRO_ART));
        cards.add(new SetCardInfo("Etchings of the Chosen", 25, Rarity.UNCOMMON, mage.cards.e.EtchingsOfTheChosen.class, RETRO_ART));
        cards.add(new SetCardInfo("Faerie Seer", 8, Rarity.UNCOMMON, mage.cards.f.FaerieSeer.class, RETRO_ART));
        cards.add(new SetCardInfo("Force of Negation", 9, Rarity.RARE, mage.cards.f.ForceOfNegation.class, RETRO_ART));
        cards.add(new SetCardInfo("Force of Vigor", 21, Rarity.RARE, mage.cards.f.ForceOfVigor.class, RETRO_ART));
        cards.add(new SetCardInfo("Generous Gift", 2, Rarity.UNCOMMON, mage.cards.g.GenerousGift.class, RETRO_ART));
        cards.add(new SetCardInfo("Giver of Runes", 3, Rarity.RARE, mage.cards.g.GiverOfRunes.class, RETRO_ART));
        cards.add(new SetCardInfo("Goblin Engineer", 16, Rarity.RARE, mage.cards.g.GoblinEngineer.class, RETRO_ART));
        cards.add(new SetCardInfo("Hall of Heliod's Generosity", 39, Rarity.RARE, mage.cards.h.HallOfHeliodsGenerosity.class, RETRO_ART));
        cards.add(new SetCardInfo("Ice-Fang Coatl", 27, Rarity.RARE, mage.cards.i.IceFangCoatl.class, RETRO_ART));
        cards.add(new SetCardInfo("Ingenious Infiltrator", 28, Rarity.UNCOMMON, mage.cards.i.IngeniousInfiltrator.class, RETRO_ART));
        cards.add(new SetCardInfo("King of the Pride", 4, Rarity.UNCOMMON, mage.cards.k.KingOfThePride.class, RETRO_ART));
        cards.add(new SetCardInfo("Lavabelly Sliver", 29, Rarity.UNCOMMON, mage.cards.l.LavabellySliver.class, RETRO_ART));
        cards.add(new SetCardInfo("Llanowar Tribe", 22, Rarity.UNCOMMON, mage.cards.l.LlanowarTribe.class, RETRO_ART));
        cards.add(new SetCardInfo("Magmatic Sinkhole", 17, Rarity.UNCOMMON, mage.cards.m.MagmaticSinkhole.class, RETRO_ART));
        cards.add(new SetCardInfo("Plague Engineer", 14, Rarity.RARE, mage.cards.p.PlagueEngineer.class, RETRO_ART));
        cards.add(new SetCardInfo("Prismatic Vista", 40, Rarity.RARE, mage.cards.p.PrismaticVista.class, RETRO_ART));
        cards.add(new SetCardInfo("Ranger-Captain of Eos", 5, Rarity.MYTHIC, mage.cards.r.RangerCaptainOfEos.class, RETRO_ART));
        cards.add(new SetCardInfo("Scale Up", 23, Rarity.UNCOMMON, mage.cards.s.ScaleUp.class, RETRO_ART));
        cards.add(new SetCardInfo("Shenanigans", 18, Rarity.UNCOMMON, mage.cards.s.Shenanigans.class, RETRO_ART));
        cards.add(new SetCardInfo("Sisay, Weatherlight Captain", 6, Rarity.RARE, mage.cards.s.SisayWeatherlightCaptain.class, RETRO_ART));
        cards.add(new SetCardInfo("Soulherder", 30, Rarity.UNCOMMON, mage.cards.s.Soulherder.class, RETRO_ART));
        cards.add(new SetCardInfo("Sword of Sinew and Steel", 31, Rarity.MYTHIC, mage.cards.s.SwordOfSinewAndSteel.class, RETRO_ART));
        cards.add(new SetCardInfo("Sword of Truth and Justice", 32, Rarity.MYTHIC, mage.cards.s.SwordOfTruthAndJustice.class, RETRO_ART));
        cards.add(new SetCardInfo("Talisman of Conviction", 33, Rarity.UNCOMMON, mage.cards.t.TalismanOfConviction.class, RETRO_ART));
        cards.add(new SetCardInfo("Talisman of Creativity", 34, Rarity.UNCOMMON, mage.cards.t.TalismanOfCreativity.class, RETRO_ART));
        cards.add(new SetCardInfo("Talisman of Curiosity", 35, Rarity.UNCOMMON, mage.cards.t.TalismanOfCuriosity.class, RETRO_ART));
        cards.add(new SetCardInfo("Talisman of Hierarchy", 36, Rarity.UNCOMMON, mage.cards.t.TalismanOfHierarchy.class, RETRO_ART));
        cards.add(new SetCardInfo("Talisman of Resilience", 37, Rarity.UNCOMMON, mage.cards.t.TalismanOfResilience.class, RETRO_ART));
        cards.add(new SetCardInfo("The First Sliver", 26, Rarity.MYTHIC, mage.cards.t.TheFirstSliver.class, RETRO_ART));
        cards.add(new SetCardInfo("Tribute Mage", 10, Rarity.UNCOMMON, mage.cards.t.TributeMage.class, RETRO_ART));
        cards.add(new SetCardInfo("Undead Augur", 15, Rarity.UNCOMMON, mage.cards.u.UndeadAugur.class, RETRO_ART));
        cards.add(new SetCardInfo("Universal Automaton", 38, Rarity.UNCOMMON, mage.cards.u.UniversalAutomaton.class, RETRO_ART));
        cards.add(new SetCardInfo("Urza, Lord High Artificer", 11, Rarity.MYTHIC, mage.cards.u.UrzaLordHighArtificer.class, RETRO_ART));
        cards.add(new SetCardInfo("Weather the Storm", 24, Rarity.UNCOMMON, mage.cards.w.WeatherTheStorm.class, RETRO_ART));
    }
}
