public class Hand 
{
    int[] cnt = new int[15];
    int pairCnt = 0, straight = -1, type;
    boolean flush = false, three = false, four = false;

    Hand(Card[] cards)
    {
        for (Card c : cards) cnt[c.value]++;
        cnt[14] = cnt[1];
        
        for (int i = 1; i <= 13; i++)
        {
            if (cnt[i] == 2) pairCnt++;
            three |= cnt[i] == 3;
            four |= cnt[i] == 4;
        }

        for (int i = 1; i+4 <= 14; i++)
        {
            if (cnt[i] == 0) continue;
            int j = 0;
            while (j <= 4 && cnt[i+j] > 0) j++;
            if (j == 5) { straight = i; break; }
        }

        flush = true;
        for (int i = 0; i < 5; i++) flush &= cards[i].suitIdx == cards[0].suitIdx;

        if (flush && straight == 10) type = Const.ROYAL_FLUSH;
        else if (flush && straight != -1) type = Const.STRAIGHT_FLUSH;
        else if (four) type = Const.FOUR_OF_KIND;
        else if (three && pairCnt > 0) type = Const.FULL_HOUSE;
        else if (flush) type = Const.FLUSH;
        else if (straight != -1) type = Const.STRAIGHT;
        else if (three) type = Const.THREE_OF_KIND;
        else if (pairCnt == 2) type = Const.TWO_PAIR;
        else if (pairCnt == 1) type = Const.PAIR;
        else type = Const.HIGH_CARD;
    }
}
