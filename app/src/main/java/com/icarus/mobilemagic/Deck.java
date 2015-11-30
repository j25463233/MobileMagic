package com.icarus.mobilemagic;

import java.util.*;

/**
 * Deck of playing cards.
 */
public class Deck implements Iterable<Card> {

    public static final int SHUFFLED_DECK = 1;
    public static final int ORDERED_DECK = 2;
    public static final int DECK_SIZE
            = Suit.values().length * Rank.values().length;
    private List<Card> mCardList = new ArrayList<>(DECK_SIZE);

    /**
     * Default constructor creates an ordered deck.
     */
    public Deck() {
        createDeck(ORDERED_DECK);
    }

    /**
     * Constructor creates a deck of specified order.
     */
    public Deck(int order) {
        createDeck(order);
    }

    /**
     * Creates all possible unique new cards for this deck.
     */
    public void createDeck(int order) {
        for (Suit s : Suit.values()) {
            for (Rank r : Rank.values()) {
                this.mCardList.add(new Card(r, s));
            }
        }
        if (order == SHUFFLED_DECK) {
            shuffleDeck();
        }
    }

    /**
     * Shuffles the deck.
     */
    public void shuffleDeck() {
        Collections.shuffle(this.mCardList);
    }

    /**
     * Required method for Iterable interface.
     */
    @Override
    public final Iterator<Card> iterator() {
        return this.mCardList.iterator();
    }
}