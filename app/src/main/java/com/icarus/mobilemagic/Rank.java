package com.icarus.mobilemagic;

/**
 * Enumeration class for the ranks of cards.
 * Integer values: Ace = 1, ..., King = 13
 */
public enum Rank {
    ACE ("a", 1),
    DEUCE ("2", 2),
    TREY ("3", 3),
    FOUR ("4", 4),
    FIVE ("5", 5),
    SIX ("6", 6),
    SEVEN ("7", 7),
    EIGHT ("8", 8),
    NINE ("9", 9),
    TEN ("t", 10),
    JACK ("j", 11),
    QUEEN ("q", 12),
    KING ("k", 13);

    private final String rankName;
    private final int rankValue;

    /**
     * Constructor takes specified name and value.
     * @param name the name of this rank
     * @param value the integer value of this rank
     */
    Rank(final String name, final int value) {
        this.rankName = name;
        this.rankValue = value;
    }

    /**
     * Accesses the integer value of this rank.
     * @return the integer value corresponding to this rank
     */
    public int value() {
        return this.rankValue;
    }

    @Override
    public String toString() {
        return this.rankName;
    }
}