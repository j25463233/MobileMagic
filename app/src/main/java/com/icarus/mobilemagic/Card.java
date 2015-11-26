package com.icarus.mobilemagic;

/**
 * A playing card that has a rank and suit.
 * @author Jason Mathews
 */
public class Card {

    private Rank rank;
    private Suit suit;

    /**
     * Default constructor creates an Ace of Spades.
     */
    public Card() {
        this(Rank.ACE, Suit.SPADES);
    }

    /**
     * Constructor that sets the rank and suit of this card.
     * @param rank the rank for this card
     * @param suit the suit for this card
     */
    public Card (Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    /**
     * Returns the string representation of this card.
     */
    @Override
    public final String toString() {
        return "" + this.suit + this.rank;
    }
}