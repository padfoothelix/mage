package mage.cards;

import mage.MageObject;
import mage.MageObjectImpl;
import mage.Mana;
import mage.abilities.*;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.common.continuous.HasSubtypesSourceEffect;
import mage.abilities.keyword.*;
import mage.abilities.mana.ActivatedManaAbilityImpl;
import mage.cards.mock.MockableCard;
import mage.cards.repository.PluginClassloaderRegistery;
import mage.constants.*;
import mage.counters.Counter;
import mage.counters.Counters;
import mage.filter.FilterMana;
import mage.game.*;
import mage.game.command.CommandObject;
import mage.game.events.*;
import mage.game.permanent.Permanent;
import mage.game.permanent.PermanentCard;
import mage.game.stack.Spell;
import mage.game.stack.StackObject;
import mage.util.CardUtil;
import mage.util.GameLog;
import mage.util.ManaUtil;
import mage.watchers.Watcher;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class CardImpl extends MageObjectImpl implements Card {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(CardImpl.class);

    protected UUID ownerId;
    protected Rarity rarity;
    protected Class<? extends Card> secondSideCardClazz;
    protected Class<? extends Card> meldsWithClazz;
    protected Class<? extends MeldCard> meldsToClazz;
    protected MeldCard meldsToCard;
    protected Card secondSideCard;
    protected boolean nightCard;
    protected SpellAbility spellAbility;
    protected boolean flipCard;
    protected String flipCardName;
    protected boolean morphCard;
    protected List<UUID> attachments = new ArrayList<>();
    protected boolean extraDeckCard = false;

    protected CardImpl(UUID ownerId, CardSetInfo setInfo, CardType[] cardTypes, String costs) {
        this(ownerId, setInfo, cardTypes, costs, SpellAbilityType.BASE);
    }

    protected CardImpl(UUID ownerId, CardSetInfo setInfo, CardType[] cardTypes, String costs, SpellAbilityType spellAbilityType) {
        this(ownerId, setInfo.getName());

        this.rarity = setInfo.getRarity();
        this.setExpansionSetCode(setInfo.getExpansionSetCode());
        this.setUsesVariousArt(setInfo.getUsesVariousArt());
        this.setCardNumber(setInfo.getCardNumber());
        this.setImageFileName(""); // use default
        this.setImageNumber(0);
        this.cardType.addAll(Arrays.asList(cardTypes));
        this.manaCost.load(costs);
        setDefaultColor();
        if (this.isLand()) {
            Ability ability = new PlayLandAbility(name);
            ability.setSourceId(this.getId());
            abilities.add(ability);
        } else {
            SpellAbility ability = new SpellAbility(manaCost, name, Zone.HAND, spellAbilityType);
            if (!this.isInstant()) {
                ability.setTiming(TimingRule.SORCERY);
            }
            ability.setSourceId(this.getId());
            abilities.add(ability);
        }

        CardGraphicInfo graphicInfo = setInfo.getGraphicInfo();
        if (graphicInfo != null) {
            if (graphicInfo.getFrameColor() != null) {
                this.frameColor = graphicInfo.getFrameColor().copy();
            }
            if (graphicInfo.getFrameStyle() != null) {
                this.frameStyle = graphicInfo.getFrameStyle();
            }
        }

        this.morphCard = false;
    }

    private void setDefaultColor() {
        this.color.setWhite(this.manaCost.containsColor(ColoredManaSymbol.W));
        this.color.setBlue(this.manaCost.containsColor(ColoredManaSymbol.U));
        this.color.setBlack(this.manaCost.containsColor(ColoredManaSymbol.B));
        this.color.setRed(this.manaCost.containsColor(ColoredManaSymbol.R));
        this.color.setGreen(this.manaCost.containsColor(ColoredManaSymbol.G));
    }

    protected CardImpl(UUID ownerId, String name) {
        super();
        this.ownerId = ownerId;
        this.name = name;
    }

    protected CardImpl(UUID id, UUID ownerId, String name) {
        super(id);
        this.ownerId = ownerId;
        this.name = name;
    }

    protected CardImpl(final CardImpl card) {
        super(card);
        ownerId = card.ownerId;
        rarity = card.rarity;

        // TODO: wtf, do not copy card sides cause it must be re-created each time (see details in getSecondCardFace)
        //  must be reworked to normal copy and workable transform without such magic

        nightCard = card.nightCard;
        secondSideCardClazz = card.secondSideCardClazz;
        secondSideCard = null; // will be set on first getSecondCardFace call if card has one
        if (card.secondSideCard instanceof MockableCard) {
            // workaround to support gui's mock cards
            secondSideCard = card.secondSideCard.copy();
        }

        meldsWithClazz = card.meldsWithClazz;
        meldsToClazz = card.meldsToClazz;
        meldsToCard = null; // will be set on first getMeldsToCard call if card has one
        if (card.meldsToCard instanceof MockableCard) {
            // workaround to support gui's mock cards
            meldsToCard = card.meldsToCard.copy();
        }

        spellAbility = null; // will be set on first getSpellAbility call if card has one
        flipCard = card.flipCard;
        flipCardName = card.flipCardName;
        morphCard = card.morphCard;
        extraDeckCard = card.extraDeckCard;

        this.attachments.addAll(card.attachments);
    }

    @Override
    public void assignNewId() {
        this.objectId = UUID.randomUUID();
        this.abilities.newOriginalId();
        this.abilities.setSourceId(objectId);
        if (this.spellAbility != null) {
            this.spellAbility.setSourceId(objectId);
        }
    }

    public static Card createCard(String name, CardSetInfo setInfo) {
        try {
            return createCard(Class.forName(name), setInfo);
        } catch (ClassNotFoundException ex) {
            try {
                return createCard(PluginClassloaderRegistery.forName(name), setInfo);
            } catch (ClassNotFoundException ex2) {
                // ignored
            }
            logger.fatal("Error loading card: " + name, ex);
            return null;
        }
    }

    public static Card createCard(Class<?> clazz, CardSetInfo setInfo) {
        return createCard(clazz, setInfo, null);
    }

    public static Card createCard(Class<?> clazz, CardSetInfo setInfo, List<String> errorList) {
        String setCode = null;
        try {
            Card card;
            if (setInfo == null) {
                Constructor<?> con = clazz.getConstructor(UUID.class);
                card = (Card) con.newInstance(new Object[]{null});
            } else {
                setCode = setInfo.getExpansionSetCode();
                Constructor<?> con = clazz.getConstructor(UUID.class, CardSetInfo.class);
                card = (Card) con.newInstance(null, setInfo);
            }
            return card;
        } catch (Exception e) {
            String err = "Error loading card: " + clazz.getCanonicalName() + " (" + setCode + ")";
            if (errorList != null) {
                errorList.add(err);
            }

            if (e instanceof InvocationTargetException) {
                logger.fatal(err, ((InvocationTargetException) e).getTargetException());
            } else {
                logger.fatal(err, e);
            }

            return null;
        }
    }

    @Override
    public UUID getOwnerId() {
        return ownerId;
    }

    @Override
    public Rarity getRarity() {
        return rarity;
    }

    @Override
    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    @Override
    public void addInfo(String key, String value, Game game) {
        game.getState().getCardState(objectId).addInfo(key, value);
    }

    @Override
    public List<String> getRules() {
        Abilities<Ability> sourceAbilities = this.getAbilities();
        return CardUtil.getCardRulesWithAdditionalInfo(this, sourceAbilities, sourceAbilities);
    }

    @Override
    public List<String> getRules(Game game) {
        Abilities<Ability> sourceAbilities = this.getAbilities(game);
        return CardUtil.getCardRulesWithAdditionalInfo(game, this, sourceAbilities, sourceAbilities);
    }

    /**
     * Gets all base abilities - does not include additional abilities added by
     * other cards or effects
     *
     * @return A list of {@link Ability} - this collection is modifiable
     */
    @Override
    public Abilities<Ability> getAbilities() {
        return super.getAbilities();
    }

    /**
     * Gets all current abilities - includes additional abilities added by other
     * cards or effects. Warning, you can't modify that list.
     *
     * @param game
     * @return A list of {@link Ability} - this collection is not modifiable
     */
    @Override
    public Abilities<Ability> getAbilities(Game game) {
        if (game == null) {
            return abilities; // deck editor with empty game
        }

        CardState cardState = game.getState().getCardState(this.getId());
        if (cardState == null) {
            return abilities;
        }

        // collects all abilities
        Abilities<Ability> all = new AbilitiesImpl<>();

        // basic
        if (!cardState.hasLostAllAbilities()) {
            all.addAll(abilities);
        }

        // dynamic
        all.addAll(cardState.getAbilities());

        // workaround to add dynamic flashback ability from main card to all parts (example: Snapcaster Mage gives flashback to split card)
        if (!this.getId().equals(this.getMainCard().getId())) {
            CardState mainCardState = game.getState().getCardState(this.getMainCard().getId());
            if (this.getSpellAbility() != null // lands can't be casted (haven't spell ability), so ignore it
                    && mainCardState != null
                    && !mainCardState.hasLostAllAbilities()
                    && mainCardState.getAbilities().containsClass(FlashbackAbility.class)) {
                FlashbackAbility flash = new FlashbackAbility(this, this.getManaCost());
                flash.setSourceId(this.getId());
                flash.setControllerId(this.getOwnerId());
                flash.setSpellAbilityType(this.getSpellAbility().getSpellAbilityType());
                flash.setAbilityName(this.getName());
                all.add(flash);
            }
        }

        return all;
    }

    @Override
    public void looseAllAbilities(Game game) {
        CardState cardState = game.getState().getCardState(this.getId());
        cardState.setLostAllAbilities(true);
        cardState.getAbilities().clear();
    }

    @Override
    public boolean hasAbility(Ability ability, Game game) {
        // getAbilities(game) searches all abilities from base and dynamic lists (other)
        return this.getAbilities(game).contains(ability);
    }

    /**
     * Public in order to support adding abilities to SplitCardHalf's
     *
     * @param ability
     */
    @Override
    public void addAbility(Ability ability) {
        ability.setSourceId(this.getId());
        abilities.add(ability);
        abilities.addAll(ability.getSubAbilities());

        // dynamic check: you can't add ability to the PermanentCard, use permanent.addAbility(a, source, game) instead
        // reason: triggered abilities are not processing here
        if (this instanceof PermanentCard) {
            throw new IllegalArgumentException("Wrong code usage. Don't use that method for permanents, use permanent.addAbility(a, source, game) instead.");
        }

        // verify check: draw effect can't be rollback after mana usage (example: Chromatic Sphere)
        // (player can cheat with cancel button to see next card)
        // verify test will catch that errors
        if (ability instanceof ActivatedManaAbilityImpl) {
            ActivatedManaAbilityImpl manaAbility = (ActivatedManaAbilityImpl) ability;
            String rule = manaAbility.getRule().toLowerCase(Locale.ENGLISH);
            if (manaAbility.getEffects().stream().anyMatch(e -> e.getOutcome().equals(Outcome.DrawCard))
                    || rule.contains("reveal ")
                    || rule.contains("draw ")) {
                if (manaAbility.isUndoPossible()) {
                    throw new IllegalArgumentException("Ability contains draw/reveal effect, but isUndoPossible is true. Ability: "
                            + ability.getClass().getSimpleName() + "; " + ability.getRule());
                }
            }
        }

        // rules fix: workaround to fix "When {this} enters" into "When this xxx enters"
        if (EntersBattlefieldTriggeredAbility.ENABLE_TRIGGER_PHRASE_AUTO_FIX) {
            if (ability instanceof TriggeredAbility) {
                TriggeredAbility triggeredAbility = ((TriggeredAbility) ability);
                if (triggeredAbility.getTriggerPhrase() != null && triggeredAbility.getTriggerPhrase().startsWith("When {this} enters")) {
                    // there are old sets with old oracle, but it's ok for newer sets, so keep that rules fix
                    // see https://github.com/magefree/mage/issues/12791
                    String etbDescription = EntersBattlefieldTriggeredAbility.getThisObjectDescription(this);
                    triggeredAbility.setTriggerPhrase(triggeredAbility.getTriggerPhrase().replace("{this}", etbDescription));
                }
            }
        }
    }

    protected void addAbility(Ability ability, Watcher watcher) {
        addAbility(ability);
        ability.addWatcher(watcher);
    }

    public void replaceSpellAbility(SpellAbility newAbility) {
        SpellAbility oldAbility = this.getSpellAbility();
        while (oldAbility != null) {
            abilities.remove(oldAbility);
            spellAbility = null;
            oldAbility = this.getSpellAbility();
        }

        if (newAbility != null) {
            addAbility(newAbility);
        }
    }

    @Override
    public SpellAbility getSpellAbility() {
        if (spellAbility == null) {
            for (Ability ability : abilities.getActivatedAbilities(Zone.HAND)) {
                if (ability instanceof SpellAbility
                        && ((SpellAbility) ability).getSpellAbilityType() != SpellAbilityType.BASE_ALTERNATE) {
                    return spellAbility = (SpellAbility) ability;
                }
            }
        }
        return spellAbility;
    }

    @Override
    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
        this.abilities.setControllerId(ownerId);
    }

    @Override
    public UUID getControllerOrOwnerId() {
        return getOwnerId();
    }

    @Override
    public List<Mana> getMana() {
        List<Mana> mana = new ArrayList<>();
        for (ActivatedManaAbilityImpl ability : this.abilities.getActivatedManaAbilities(Zone.BATTLEFIELD)) {
            mana.addAll(ability.getNetMana(null));
        }
        return mana;
    }

    @Override
    public boolean moveToZone(Zone toZone, Ability source, Game game, boolean flag) {
        return this.moveToZone(toZone, source, game, flag, null);
    }

    @Override
    public boolean moveToZone(Zone toZone, Ability source, Game game, boolean flag, List<UUID> appliedEffects) {
        Zone fromZone = game.getState().getZone(objectId);
        ZoneChangeEvent event = new ZoneChangeEvent(this.objectId, source, ownerId, fromZone, toZone, appliedEffects);
        ZoneChangeInfo zoneChangeInfo;
        if (null != toZone) {
            switch (toZone) {
                case LIBRARY:
                    zoneChangeInfo = new ZoneChangeInfo.Library(event, flag /* put on top */);
                    break;
                case BATTLEFIELD:
                    zoneChangeInfo = new ZoneChangeInfo.Battlefield(event, flag /* comes into play tapped */, source);
                    break;
                default:
                    zoneChangeInfo = new ZoneChangeInfo(event);
                    break;
            }
            return ZonesHandler.moveCard(zoneChangeInfo, game, source);
        }
        return false;
    }

    @Override
    public boolean cast(Game game, Zone fromZone, SpellAbility ability, UUID controllerId) {
        Card mainCard = getMainCard();
        ZoneChangeEvent event = new ZoneChangeEvent(mainCard.getId(), ability, controllerId, fromZone, Zone.STACK);
        Spell spell = new Spell(this, ability.getSpellAbilityToResolve(game), controllerId, event.getFromZone(), game);
        ZoneChangeInfo.Stack info = new ZoneChangeInfo.Stack(event, spell);
        return ZonesHandler.cast(info, ability, game);
    }

    @Override
    public boolean moveToExile(UUID exileId, String name, Ability source, Game game) {
        return moveToExile(exileId, name, source, game, null);
    }

    @Override
    public boolean moveToExile(UUID exileId, String name, Ability source, Game game, List<UUID> appliedEffects) {
        Zone fromZone = game.getState().getZone(objectId);
        ZoneChangeEvent event = new ZoneChangeEvent(this.objectId, source, ownerId, fromZone, Zone.EXILED, appliedEffects);
        ZoneChangeInfo.Exile info = new ZoneChangeInfo.Exile(event, exileId, name);
        return ZonesHandler.moveCard(info, game, source);
    }

    @Override
    public boolean putOntoBattlefield(Game game, Zone fromZone, Ability source, UUID controllerId) {
        return this.putOntoBattlefield(game, fromZone, source, controllerId, false, false, null);
    }

    @Override
    public boolean putOntoBattlefield(Game game, Zone fromZone, Ability source, UUID controllerId, boolean tapped) {
        return this.putOntoBattlefield(game, fromZone, source, controllerId, tapped, false, null);
    }

    @Override
    public boolean putOntoBattlefield(Game game, Zone fromZone, Ability source, UUID controllerId, boolean tapped, boolean faceDown) {
        return this.putOntoBattlefield(game, fromZone, source, controllerId, tapped, faceDown, null);
    }

    @Override
    public boolean putOntoBattlefield(Game game, Zone fromZone, Ability source, UUID controllerId, boolean tapped, boolean faceDown, List<UUID> appliedEffects) {
        ZoneChangeEvent event = new ZoneChangeEvent(this.objectId, source, controllerId, fromZone, Zone.BATTLEFIELD, appliedEffects);
        ZoneChangeInfo.Battlefield info = new ZoneChangeInfo.Battlefield(event, faceDown, tapped, source);
        return ZonesHandler.moveCard(info, game, source);
    }

    @Override
    public boolean removeFromZone(Game game, Zone fromZone, Ability source) {
        boolean removed = false;
        MageObject lkiObject = null;
        if (isCopy()) { // copied cards have no need to be removed from a previous zone
            removed = true;
        } else {
            switch (fromZone) {
                case GRAVEYARD:
                    removed = game.getPlayer(ownerId).removeFromGraveyard(this, game);
                    break;
                case HAND:
                    removed = game.getPlayer(ownerId).removeFromHand(this, game);
                    break;
                case LIBRARY:
                    removed = game.getPlayer(ownerId).removeFromLibrary(this, game);
                    break;
                case EXILED:
                    if (game.getExile().getCard(getId(), game) != null) {
                        removed = game.getExile().removeCard(this);
                    }
                    break;
                case STACK:
                    StackObject stackObject;
                    if (getSpellAbility() != null) {
                        stackObject = game.getStack().getSpell(getSpellAbility().getId(), false);
                    } else {
                        stackObject = game.getStack().getSpell(this.getId(), false);
                    }

                    // handle half of Split Cards on stack
                    if (stackObject == null && (this instanceof SplitCard)) {
                        stackObject = game.getStack().getSpell(((SplitCard) this).getLeftHalfCard().getId(), false);
                        if (stackObject == null) {
                            stackObject = game.getStack().getSpell(((SplitCard) this).getRightHalfCard().getId(),
                                    false);
                        }
                    }

                    // handle half of Modal Double Faces Cards on stack
                    if (stackObject == null && (this instanceof ModalDoubleFacedCard)) {
                        stackObject = game.getStack().getSpell(((ModalDoubleFacedCard) this).getLeftHalfCard().getId(),
                                false);
                        if (stackObject == null) {
                            stackObject = game.getStack()
                                    .getSpell(((ModalDoubleFacedCard) this).getRightHalfCard().getId(), false);
                        }
                    }

                    if (stackObject == null && (this instanceof CardWithSpellOption)) {
                        stackObject = game.getStack().getSpell(((CardWithSpellOption) this).getSpellCard().getId(), false);
                    }

                    if (stackObject == null) {
                        stackObject = game.getStack().getSpell(getId(), false);
                    }
                    if (stackObject != null) {
                        removed = game.getStack().remove(stackObject, game);
                        lkiObject = stackObject;
                    }
                    break;
                case COMMAND:
                    for (CommandObject commandObject : game.getState().getCommand()) {
                        if (commandObject.getId().equals(objectId)) {
                            lkiObject = commandObject;
                        }
                    }
                    if (lkiObject != null) {
                        removed = game.getState().getCommand().remove(lkiObject);
                    }
                    break;
                case OUTSIDE:
                    if (game.getPlayer(ownerId).getSideboard().contains(this.getId())) {
                        game.getPlayer(ownerId).getSideboard().remove(this.getId());
                        removed = true;
                    } else if (game.getPhase() == null) {
                        // E.g. Commander of commander game
                        removed = true;
                    } else {
                        // Unstable - Summon the Pack
                        removed = true;
                    }
                    break;
                case BATTLEFIELD: // for sacrificing permanents or putting to library
                    removed = true;
                    break;
                default:
                    MageObject sourceObject = game.getObject(source);
                    logger.fatal("Invalid from zone [" + fromZone + "] for card [" + this.getIdName()
                            + "] source [" + (sourceObject != null ? sourceObject.getName() : "null") + ']');
                    break;
            }
        }
        if (removed) {
            if (fromZone != Zone.OUTSIDE) {
                game.rememberLKI(fromZone, lkiObject != null ? lkiObject : this);
            }
        } else {
            logger.warn("Couldn't find card in fromZone, card=" + getIdName() + ", fromZone=" + fromZone);
            // possible reason: you to remove card from wrong zone or card already removed,
            // e.g. you added copy card to wrong graveyard (see owner) or removed card from graveyard before moveToZone call
        }
        return removed;
    }

    @Override
    public void applyEnterWithCounters(Permanent permanent, Ability source, Game game) {
        Counters countersToAdd = game.getEnterWithCounters(permanent.getId());
        if (countersToAdd != null) {
            for (Counter counter : countersToAdd.values()) {
                permanent.addCounters(counter, source.getControllerId(), source, game);
            }
            game.setEnterWithCounters(permanent.getId(), null);
        }
    }

    @Override
    public void setFaceDown(boolean value, Game game) {
        game.getState().getCardState(objectId).setFaceDown(value);
    }

    @Override
    public boolean isFaceDown(Game game) {
        return game.getState().getCardState(objectId).isFaceDown();
    }

    @Override
    public boolean turnFaceUp(Ability source, Game game, UUID playerId) {
        GameEvent event = GameEvent.getEvent(GameEvent.EventType.TURN_FACE_UP, getId(), source, playerId);
        if (!game.replaceEvent(event)) {
            setFaceDown(false, game);
            for (Ability ability : abilities) { // abilities that were set to not visible face down must be set to visible again
                if (ability.getWorksFaceDown() && !ability.getRuleVisible()) {
                    ability.setRuleVisible(true);
                }
            }
            // The current face down implementation is just setting a boolean, so any trigger checking for a
            // permanent property once being turned face up is not seeing the right face up data.
            // For instance triggers looking for specific subtypes being turned face up (Detectives in MKM set)
            // are broken without that processAction call.
            // This is somewhat a band-aid on the special action nature of turning a permanent face up.
            // 708.8. As a face-down permanent is turned face up, its copiable values revert to its normal copiable values.
            // Any effects that have been applied to the face-down permanent still apply to the face-up permanent.
            game.processAction();
            game.fireEvent(GameEvent.getEvent(GameEvent.EventType.TURNED_FACE_UP, getId(), source, playerId));
            return true;
        }
        return false;
    }

    @Override
    public boolean turnFaceDown(Ability source, Game game, UUID playerId) {
        GameEvent event = GameEvent.getEvent(GameEvent.EventType.TURN_FACE_DOWN, getId(), source, playerId);
        if (!game.replaceEvent(event)) {
            setFaceDown(true, game);
            game.fireEvent(GameEvent.getEvent(GameEvent.EventType.TURNED_FACE_DOWN, getId(), source, playerId));
            return true;
        }
        return false;
    }

    @Override
    public boolean isTransformable() {
        // warning, not all multifaces cards can be transformable (meld, mdfc)
        // mtg rules method: here
        // GUI related method: search "transformable = true" in CardView
        // TODO: check and fix method usage in game engine, it's must be mtg rules logic, not GUI

        // 701.28c
        // If a spell or ability instructs a player to transform a permanent that
        // isn’t represented by a transforming token or a transforming double-faced
        // card, nothing happens.
        return this.secondSideCardClazz != null || this.nightCard;
    }

    @Override
    public final Card getSecondCardFace() {
        // init card side on first call
        if (secondSideCardClazz == null && secondSideCard == null) {
            return null;
        }

        if (secondSideCard == null) {
            secondSideCard = initSecondSideCard(secondSideCardClazz);
            if (secondSideCard != null && secondSideCard.getSpellAbility() != null) {
                // TODO: wtf, why it set cast mode here?! Transform tests fails without it
                //  must be reworked without that magic, also see CardImpl'constructor for copy code
                secondSideCard.getSpellAbility().setSourceId(this.getId());
                secondSideCard.getSpellAbility().setSpellAbilityType(SpellAbilityType.BASE_ALTERNATE);
                secondSideCard.getSpellAbility().setSpellAbilityCastMode(SpellAbilityCastMode.TRANSFORMED);
            }
        }

        return secondSideCard;
    }

    private Card initSecondSideCard(Class<? extends Card> cardClazz) {
        // must be non strict search in any sets, not one set
        // example: if set contains only one card side
        // method used in cards database creating, so can't use repository here
        ExpansionSet.SetCardInfo info = Sets.findCardByClass(cardClazz, this.getExpansionSetCode(), this.getCardNumber());
        if (info == null) {
            return null;
        }
        return createCard(cardClazz, new CardSetInfo(info.getName(), this.getExpansionSetCode(), info.getCardNumber(), info.getRarity(), info.getGraphicInfo()));
    }

    @Override
    public SpellAbility getSecondFaceSpellAbility() {
        Card secondFace = getSecondCardFace();
        if (secondFace == null || secondFace.getClass().equals(getClass())) {
            throw new IllegalArgumentException("Wrong code usage: getSecondFaceSpellAbility can only be used for double faced card (main side), broken card: " + this.getName());
        }
        return secondFace.getSpellAbility();
    }

    @Override
    public boolean meldsWith(Card card) {
        return this.meldsWithClazz != null && this.meldsWithClazz.isInstance(card.getMainCard());
    }

    @Override
    public Class<? extends Card> getMeldsToClazz() {
        return this.meldsToClazz;
    }

    @Override
    public MeldCard getMeldsToCard() {
        // init card on first call
        if (meldsToClazz == null && meldsToCard == null) {
            return null;
        }

        if (meldsToCard == null) {
            meldsToCard = (MeldCard) initSecondSideCard(meldsToClazz);
        }

        return meldsToCard;
    }

    @Override
    public boolean isNightCard() {
        return this.nightCard;
    }

    @Override
    public boolean isFlipCard() {
        return flipCard;
    }

    @Override
    public String getFlipCardName() {
        return flipCardName;
    }

    @Override
    public Counters getCounters(Game game) {
        return getCounters(game.getState());
    }

    @Override
    public Counters getCounters(GameState state) {
        return state.getCardState(this.objectId).getCounters();
    }

    @Override
    public boolean addCounters(Counter counter, Ability source, Game game) {
        return addCounters(counter, source.getControllerId(), source, game);
    }

    @Override
    public boolean addCounters(Counter counter, UUID playerAddingCounters, Ability source, Game game) {
        return addCounters(counter, playerAddingCounters, source, game, null, true);
    }

    @Override
    public boolean addCounters(Counter counter, UUID playerAddingCounters, Ability source, Game game, boolean isEffect) {
        return addCounters(counter, playerAddingCounters, source, game, null, isEffect);
    }

    @Override
    public boolean addCounters(Counter counter, UUID playerAddingCounters, Ability source, Game game, List<UUID> appliedEffects) {
        return addCounters(counter, playerAddingCounters, source, game, appliedEffects, true);
    }

    @Override
    public boolean addCounters(Counter counter, UUID playerAddingCounters, Ability source, Game game, List<UUID> appliedEffects, boolean isEffect) {
        return addCounters(counter, playerAddingCounters, source, game, appliedEffects, isEffect, Integer.MAX_VALUE);
    }

    public boolean addCounters(Counter counter, UUID playerAddingCounters, Ability source, Game game, List<UUID> appliedEffects, boolean isEffect, int maxCounters) {
        if (this instanceof Permanent && !((Permanent) this).isPhasedIn()) {
            return false;
        }

        boolean returnCode = true;
        GameEvent addingAllEvent = GameEvent.getEvent(GameEvent.EventType.ADD_COUNTERS, objectId, source, playerAddingCounters, counter.getName(), counter.getCount());
        addingAllEvent.setAppliedEffects(appliedEffects);
        addingAllEvent.setFlag(isEffect);
        if (!game.replaceEvent(addingAllEvent)) {
            int amount;
            if (maxCounters < Integer.MAX_VALUE) {
                amount = Integer.min(addingAllEvent.getAmount(), maxCounters - this.getCounters(game).getCount(counter.getName()));
            } else {
                amount = addingAllEvent.getAmount();
            }
            boolean isEffectFlag = addingAllEvent.getFlag();
            int finalAmount = amount;
            for (int i = 0; i < amount; i++) {
                Counter eventCounter = counter.copy();
                eventCounter.remove(eventCounter.getCount() - 1);
                GameEvent addingOneEvent = GameEvent.getEvent(GameEvent.EventType.ADD_COUNTER, objectId, source, playerAddingCounters, counter.getName(), 1);
                addingOneEvent.setAppliedEffects(appliedEffects);
                addingOneEvent.setFlag(isEffectFlag);
                if (!game.replaceEvent(addingOneEvent)) {
                    getCounters(game).addCounter(eventCounter);
                    GameEvent addedOneEvent = GameEvent.getEvent(GameEvent.EventType.COUNTER_ADDED, objectId, source, playerAddingCounters, counter.getName(), 1);
                    addedOneEvent.setFlag(addingOneEvent.getFlag());
                    game.fireEvent(addedOneEvent);
                } else {
                    finalAmount--;
                    returnCode = false; // restricted by ADD_COUNTER
                }
            }
            if (finalAmount > 0) {
                GameEvent addedAllEvent = GameEvent.getEvent(GameEvent.EventType.COUNTERS_ADDED, objectId, source, playerAddingCounters, counter.getName(), amount);
                addedAllEvent.setFlag(isEffectFlag);
                game.fireEvent(addedAllEvent);
            } else {
                // TODO: must return true, cause it's not replaced here (rework Fangs of Kalonia and Spectacular Showdown)
                // example from Devoted Druid
                // If you can put counters on it, but that is modified by an effect (such as that of Vizier of Remedies),
                // you can activate the ability even if paying the cost causes no counters to be put on Devoted Druid.
                // (2018-12-07)
                returnCode = false;
            }
        } else {
            returnCode = false; // restricted by ADD_COUNTERS
        }
        return returnCode;
    }

    @Override
    public int removeCounters(String counterName, int amount, Ability source, Game game, boolean isDamage) {

        if (amount <= 0) {
            return 0;
        }

        if (getCounters(game).getCount(counterName) <= 0) {
            return 0;
        }

        GameEvent removeCountersEvent = new RemoveCountersEvent(counterName, this, source, amount, isDamage);
        if (game.replaceEvent(removeCountersEvent)) {
            return 0;
        }

        int finalAmount = 0;
        for (int i = 0; i < removeCountersEvent.getAmount(); i++) {

            GameEvent event = new RemoveCounterEvent(counterName, this, source, isDamage);
            if (game.replaceEvent(event)) {
                continue;
            }

            if (!getCounters(game).removeCounter(counterName, 1)) {
                break;
            }

            event = new CounterRemovedEvent(counterName, this, source, isDamage);
            game.fireEvent(event);

            finalAmount++;
        }

        GameEvent event = new CountersRemovedEvent(counterName, this, source, finalAmount, isDamage);
        game.fireEvent(event);
        return finalAmount;
    }

    @Override
    public int removeCounters(Counter counter, Ability source, Game game, boolean isDamage) {
        return counter != null ? removeCounters(counter.getName(), counter.getCount(), source, game, isDamage) : 0;
    }

    @Override
    public int removeAllCounters(Ability source, Game game, boolean isDamage) {
        int amountBefore = getCounters(game).getTotalCount();
        for (Counter counter : getCounters(game).copy().values()) {
            removeCounters(counter.getName(), counter.getCount(), source, game, isDamage);
        }
        int amountAfter = getCounters(game).getTotalCount();
        return Math.max(0, amountBefore - amountAfter);
    }

    @Override
    public int removeAllCounters(String counterName, Ability source, Game game, boolean isDamage) {
        int amountBefore = getCounters(game).getCount(counterName);
        removeCounters(counterName, amountBefore, source, game, isDamage);
        int amountAfter = getCounters(game).getCount(counterName);
        return Math.max(0, amountBefore - amountAfter);
    }

    @Override
    public String getLogName() {
        if (name.isEmpty()) {
            return GameLog.getNeutralColoredText(EmptyNames.FACE_DOWN_CREATURE.getObjectName());
        } else {
            return GameLog.getColoredObjectIdName(this);
        }
    }

    @Override
    public Card getMainCard() {
        return this;
    }

    @Override
    public FilterMana getColorIdentity() {
        return ManaUtil.getColorIdentity(this);
    }

    @Override
    public void setZone(Zone zone, Game game) {
        game.setZone(getId(), zone);
    }

    @Override
    public void setSpellAbility(SpellAbility ability) {
        spellAbility = ability;
    }

    @Override
    public List<UUID> getAttachments() {
        return attachments;
    }

    @Override
    public boolean cantBeAttachedBy(MageObject attachment, Ability source, Game game, boolean silentMode) {
        boolean canAttach = true;
        for (ProtectionAbility ability : this.getAbilities(game).getProtectionAbilities()) {
            if ((!attachment.hasSubtype(SubType.AURA, game) || ability.removesAuras())
                    && (!attachment.hasSubtype(SubType.EQUIPMENT, game) || ability.removesEquipment())
                    && !attachment.getId().equals(ability.getAuraIdNotToBeRemoved())
                    && !ability.canTarget(attachment, game)) {
                canAttach &= ability.getDoesntRemoveControlled() && Objects.equals(getControllerOrOwnerId(), game.getControllerId(attachment.getId()));
            }
        }

        // If attachment is an aura, ensures this permanent can still be legally enchanted, according to the enchantment's Enchant ability
        if (attachment.hasSubtype(SubType.AURA, game)) {
            SpellAbility spellAbility = null;
            UUID controller = null;
            Permanent attachmentPermanent = game.getPermanent(attachment.getId());
            if (attachmentPermanent != null) {
                spellAbility = attachmentPermanent.getSpellAbility(); // Permanent's SpellAbility might be modified, so if possible use that one
                controller = attachmentPermanent.getControllerId();
            } else { // Used for checking if it can be attached from the graveyard, such as Unfinished Business
                Card attachmentCard = game.getCard(attachment.getId());
                if (attachmentCard != null) {
                    spellAbility = attachmentCard.getSpellAbility();
                    if (source != null) {
                        controller = source.getControllerId();
                    } else {
                        controller = attachmentCard.getControllerOrOwnerId();
                    }
                }
            }
            if (controller != null && spellAbility != null && !spellAbility.getTargets().isEmpty()){
                // Line of code below functionally gets the target of the aura's Enchant ability, then compares to this permanent. Enchant improperly implemented in XMage, see #9583
                // Note: stillLegalTarget used exclusively to account for Dream Leash. Can be made canTarget in the event that that card is rewritten (and "stillLegalTarget" removed from TargetImpl).
                canAttach &= spellAbility.getTargets().get(0).copy().withNotTarget(true).stillLegalTarget(controller, this.getId(), source, game);
            }
        }
        return !canAttach || game.getContinuousEffects().preventedByRuleModification(new StayAttachedEvent(this.getId(), attachment.getId(), source), null, game, silentMode);
    }

    @Override
    public boolean addAttachment(UUID permanentId, Ability source, Game game) {
        if (permanentId == null
                || this.attachments.contains(permanentId)
                || permanentId.equals(this.getId())) {
            return false;
        }
        Permanent attachment = game.getPermanent(permanentId);
        if (attachment == null) {
            attachment = game.getPermanentEntering(permanentId);
        }
        if (attachment == null) {
            return false;
        }
        if (attachment.hasSubtype(SubType.EQUIPMENT, game)
                && (attachment.isCreature(game)  // seems strange and perhaps someone knows why this is checked.
                && !attachment.getAbilities(game).containsClass(ReconfigureAbility.class))) {
            return false;
        }
        if (attachment.hasSubtype(SubType.FORTIFICATION, game)
                && (attachment.isCreature(game) || !this.isLand(game))) {
            return false;
        }
        if (this.cantBeAttachedBy(attachment, source, game, false)) {
            return false;
        }
        if (game.replaceEvent(new AttachEvent(objectId, attachment, source))) {
            return false;
        }
        this.attachments.add(permanentId);
        attachment.attachTo(objectId, source, game);
        game.fireEvent(new AttachedEvent(objectId, attachment, source));
        return true;
    }

    @Override
    public boolean removeAttachment(UUID permanentId, Ability source, Game game) {
        if (this.attachments.contains(permanentId)) {
            Permanent attachment = game.getPermanent(permanentId);
            if (attachment != null) {
                attachment.unattach(game);
            }
            if (!game.replaceEvent(new UnattachEvent(objectId, permanentId, attachment, source))) {
                this.attachments.remove(permanentId);
                game.fireEvent(new UnattachedEvent(objectId, permanentId, attachment, source));
                return true;
            }
        }
        return false;
    }

    @Override
    public List<CardType> getCardTypeForDeckbuilding() {
        return getCardType();
    }

    @Override
    public boolean hasCardTypeForDeckbuilding(CardType cardType) {
        return getCardTypeForDeckbuilding().contains(cardType);
    }

    @Override
    public boolean hasSubTypeForDeckbuilding(SubType subType) {
        // own subtype
        if (this.hasSubtype(subType, null)) {
            return true;
        }

        // gained subtypes from source ability
        if (this.getAbilities()
                .stream()
                .filter(SimpleStaticAbility.class::isInstance)
                .map(Ability::getAllEffects)
                .flatMap(Collection::stream)
                .filter(HasSubtypesSourceEffect.class::isInstance)
                .map(HasSubtypesSourceEffect.class::cast)
                .anyMatch(effect -> effect.hasSubtype(subType))) {
            return true;
        }

        // changeling (any subtype)
        return subType.getSubTypeSet() == SubTypeSet.CreatureType
                && this.getAbilities().containsClass(ChangelingAbility.class);
    }

    /**
     * This is used for disabling auto-payments for any any cards which care about the color
     * of the mana used to cast it beyond color requirements. E.g. Sunburst, Adamant, Flamespout.
     * <p>
     * This is <b>not</b> about which colors are in the mana costs.
     * <p>
     * E.g. "Pentad Prism" {2} will return true since it has Sunburst, but "Abbey Griffin" {3}{W} will
     * return false since the mana spent on the generic cost has no impact on the card.
     *
     * @return Whether the given spell cares about the mana color used to pay for it.
     */
    public boolean caresAboutManaColor(Game game) {
        // SunburstAbility
        if (abilities.containsClass(SunburstAbility.class)) {
            return true;
        }

        // Look at each individual ability
        //      ConditionalInterveningIfTriggeredAbility (e.g. Ogre Savant)
        //      Spellability with ManaWasSpentCondition (e.g. Firespout)
        //      Modular (only Arcbound Wanderer)
        for (Ability ability : getAbilities(game)) {
            if (((AbilityImpl) ability).caresAboutManaColor()) {
                return true;
            }
        }

        // Only way to get here is if none of the effects on the card care about mana color.
        return false;
    }

    @Override
    public boolean isExtraDeckCard() {
        return extraDeckCard;
    }
}
