package com.icarus.mobilemagic;

/**
 * A playing card that has a rank and suit.
 */
public class Card {

    private Rank mRank;
    private Suit mSuit;

    /**
     * Default constructor creates an Ace of Spades.
     */
    public Card() {
        this(Rank.ACE, Suit.SPADES);
    }

    /**
     * Constructor that sets the rank and suit of this card.
     * @param mRank the rank for this card
     * @param suit the suit for this card
     */
    public Card (Rank mRank, Suit suit) {
        this.mRank = mRank;
        this.mSuit = suit;
    }

    /**
     * Returns the two-character string representation of this card.
     */
    @Override
    public final String toString() {
        return "" + this.mSuit + this.mRank;
    }
}