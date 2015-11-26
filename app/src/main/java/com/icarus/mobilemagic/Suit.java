package com.icarus.mobilemagic;

/**
 * Enumeration class for the suits of cards.
 * @author Jason Mathews
 */
public enum Suit {
    CLUBS ("c", 1),
    HEARTS ("h", 2),
    SPADES ("s", 3),
    DIAMONDS ("d", 4);

    private final String suitName;
    private final int suitValue;

    /**
     * Constructor for Suit.
     * @param name the display name for this suit
     */
    Suit(final String name, final int value) {
        this.suitName = name;
        this.suitValue = value;
    }

    public int value() {
        return this.suitValue;
    }

    @Override
    public String toString() {
        return this.suitName;
    }
}