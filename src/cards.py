from enum import Enum
import random
import time
from itertools import combinations
import json
import pickle
import csv

val_strings = ['X','X',2,3,4,5,6,7,8,9,'T','J','Q','K','A']

def get_val_str(i):
    if i<2 or i>14:
        return 'X'
    else:
        return val_strings[i]

def get_suit_str(i):
    if i==0:
        return 'c'
    elif i==1:
        return 'd'
    elif i==2:
        return 'h'
    elif i==3:
        return 's'
    else:
        return 'x'

def make_hand(string):
    cardstrs = string.split(" ")
    cards = [make_card(cardstr) for cardstr in cardstrs]
    return Hand(cards)

def make_card(string):
    num = 0
    if string[1].upper() == 'C':
        num += 0
    elif string[1].upper() == 'D':
        num += 13
    elif string[1].upper() == 'H':
        num += 26
    elif string[1].upper() == 'S':
        num += 39
    else:
        raise ValueError
    if string[0] == '2':
        num += 0
    elif string[0] == '3':
        num += 1
    elif string[0] == '4':
        num += 2
    elif string[0] == '5':
        num += 3
    elif string[0] == '6':
        num += 4
    elif string[0] == '7':
        num += 5
    elif string[0] == '8':
        num += 6
    elif string[0] == '9':
        num += 7
    elif string[0].upper() == 'T':
        num += 8
    elif string[0].upper() == 'J':
        num += 9
    elif string[0].upper() == 'Q':
        num += 10
    elif string[0].upper() == 'K':
        num += 11
    elif string[0].upper() == 'A':
        num += 12
    else:
        raise ValueError
    return Card(num)

def best_score(hand, board):
    if len(hand) != 4 or len(board) < 3:
        raise ValueError
    max_score = 0
    for hand_pair in combinations(hand, 2):
        combined = board.add(list(hand_pair))
        max_score = max(max_score, combined.score())
    return max_score

def best_scoring_hand(hand, board):
    if len(hand) != 4 or len(board) < 3:
        raise ValueError
    max_score = 0
    best_combined = None
    for hand_pair in combinations(hand, 2):
        combined = board.add(list(hand_pair))
        if (combined.score() > max_score):
            max_score = combined.score()
            best_combined = combined
    return best_combined, max_score

def relative_strength(hand, board):
    base_deck = Deck()
    for card in hand.cards:
        base_deck.remove(card)
    for card in board.cards:
        base_deck.remove(card)
    base_cards = base_deck.cards
    wins = 0
    total = 0
    
    for i in range(300):   
        deck = Deck(initial=base_cards)
        this_board = Hand(list(board.cards))
        opp_hand = Hand([deck.deal(), deck.deal(), deck.deal(), deck.deal()])
        while(len(this_board) < 5):
            this_board = this_board.add(deck.deal())
        opp_score = best_score(opp_hand, board)
        my_score = best_score(hand, board)
        if my_score >= opp_score:
            wins += 1
        total += 1
    
    return float(wins)/total
    

def relative_strength_exact(hand, board):
    base_deck = Deck()
    for card in hand.cards:
        base_deck.remove(card)
    for card in board.cards:
        base_deck.remove(card)
    base_cards = base_deck.cards
    wins = 0
    total = 0
    my_score = best_score(hand, board)
    
    for opp_tuple in combinations(base_cards, 4):
        opp_score = best_score(Hand(cards=list(opp_tuple)), board)
        if my_score >= opp_score:
            wins += 1
        total += 1
    return float(wins)/total
    

class Card:
    def __init__(self, num):
        self.num = num
        self.suit = num/13
        self.suit_str = get_suit_str(self.suit)
        self.val = num%13 + 2
        self.val_str = get_val_str(self.val)
    
    def __eq__(self, card):
        return self.num == card.num
    
    def __ne__(self, card):
        return not self.__eq__(card)
    
    def __str__(self):
        return "[{}{}]".format(self.val_str, self.suit_str)
    
    def __repr__(self):
        return "[{}{}]".format(self.val_str, self.suit_str)
    
    def __hash__(self):
        return self.num

class Deck:
    def __init__(self, initial=None):
        if initial is None:
            self.cards = list(Card(i) for i in range(52))
        else:
            self.cards = list(initial)
    
    def __iter__(self):
        return self.cards.__iter__()
    
    def __getitem__(self, i):
        return self.cards[i]
    
    def remove(self, card):
        self.cards.remove(card)
    
    def deal(self):
        return self.cards.pop(random.randint(0,len(self.cards)-1))
    
    def __repr__(self):
        return "DECK{}".format(self.cards.__repr__())
    
    def __str__(self):
        return "DECK{}".format(self.cards.__str__())
    
    def __len__(self):
        return self.cards.__len__()
    
class Hand:
    def __init__(self, cards = []):
        self.cards = cards
        self.stored_score = None
    
    def __iter__(self):
        return self.cards.__iter__()
    
    def __getitem__(self, i):
        return self.cards[i]
    
    def __repr__(self):
        return "HAND{}".format(self.cards.__repr__())
    
    def __str__(self):
        return "HAND{}".format(self.cards.__str__())
    
    def __len__(self):
        return self.cards.__len__()
    
    def add(self, c):
        if isinstance(c, Card):
            return Hand(self.cards + [c])
        elif isinstance(c, list):
            return Hand(self.cards + c)
        elif isinstance(c, Hand):
            return Hand(self.cards + c.cards)
        else:
            raise TypeError
    
    def add_cards(self, cds):
        return Hand(self.cards + cds)
    
    def score(self):
        if self.stored_score is not None:
            return self.stored_score
        else:
            self.stored_score = self.calculate_score()
            return self.stored_score
    
    def calculate_score(self):
        if (len(self.cards) < 5):
            return 0
        has_flush = False
        suit_counts = [[], [], [], []]
        flush_suit = None
        for card in self.cards:
            suit_counts[card.suit].append(card.val)
        for i in range(4):
            if len(suit_counts[i]) >= 5:
                flush_suit = i
                has_flush = True
        
        if has_flush:
            # STRAIGHT FLUSH (8)
            cardsbynum = sorted(self.cards, key=lambda c: -c.num)
            for top in range(len(cardsbynum)-4):
                topnum = cardsbynum[top].num
                if (cardsbynum[top].val >= 6 and cardsbynum[top+1].num == topnum-1 
                        and cardsbynum[top+2].num == topnum-2 and cardsbynum[top+3].num == topnum-3 
                        and cardsbynum[top+4].num == topnum-4):
                    return 8000000+cardsbynum[top].val
            has_card = [False]*52
            for card in self.cards:
                has_card[card.num] = True
            for suit in range(4):
                if (has_card[suit*13+12] and has_card[suit*13] and has_card[suit*13+1] and has_card[suit*13+2] and has_card[suit*13+3]):
                    return 8000005
            
        val_counts = [None, None, 0,0,0,0,0,0,0,0,0,0,0,0,0]
        vals = [None, None, 2,3,4,5,6,7,8,9,10,11,12,13,14]
        first = (0,0)
        second = (0,0)
        for card in self.cards:
            val_counts[card.val] += 1
        for i in range(14,1,-1):
            if val_counts[i] > second[0]:
                second = (val_counts[i], i)
            if second[0] > first[0] or second[0] == first[0] and second[1] > first[1]:
                temp = first
                first = second
                second = temp
        
        if (first[0] >= 4):
            # FOUR OF A KIND (7)
            return 7000000+first[1]*1000 + second[1]
        elif (first[0] >= 3 and second[0] >= 2):
            # FULL HOUSE (6)
            return 6000000+first[1]*1000 + second[1]
        elif (has_flush):
            # FLUSH (5)
            nums = sorted(suit_counts[flush_suit])
            return 5000000 + 50625*nums[-1] + 3375*nums[-2] + 225*nums[-3] + 15*nums[-4] + nums[-5]
        
        # STRAIGHT (4)
        val_counts[1] = val_counts[14]
        for i in range(14,4,-1):
            if (val_counts[i] > 0 and val_counts[i-1] > 0 and val_counts[i-2] > 0 and val_counts[i-3] > 0 and val_counts[i-4] > 0):
                return 4000000+i
        val_counts[1] = None
        
        # make valpairs for lower checks
        val_pairs = [(val_counts[i], i) for i in range(2,15) if val_counts[i] > 0]
        list.sort(val_pairs, key=lambda p: -p[0]*100-p[1])

        # TRIPLE (3)
        if (val_pairs[0][0] >= 3):
            return 3000000+10000*val_pairs[0][1]+100*val_pairs[1][1]+val_pairs[2][1]
        # TWO PAIR (2)
        if (val_pairs[1][0] >= 2):
            return 2000000+10000*val_pairs[0][1]+100*val_pairs[1][1]+val_pairs[2][1]
        # ONE PAIR (1)
        if (val_pairs[0][0] >= 2):
            return 1000000+3075*val_pairs[0][1]+225*val_pairs[1][1]+15*val_pairs[2][1]+val_pairs[3][1]
        return 50625*val_pairs[0][1] + 3375*val_pairs[1][1] + 225*val_pairs[2][1] + 15*val_pairs[3][1] + val_pairs[4][1]
    
    def unique_id(self):
        suit_counts = [[],[],[],[]]
        for card in self.cards:
            suit_counts[card.suit].append(card.val)
        for suit in suit_counts:
            list.sort(suit)
        list.sort(suit_counts, key=lambda m: suit_id(m))
        return (tuple(reversed(suit_counts[3])), tuple(reversed(suit_counts[2])), tuple(reversed(suit_counts[1])), tuple(reversed(suit_counts[0])))


def suit_id(sortedvals):
    return sum(15**i * val for i, val in enumerate(sortedvals))

def rev_suit_id(sortedvals):
    return sum(15**i * val for i,val in enumerate(reversed(sortedvals)))

def get_unique_hands():
    # 16432 unique hands!
    deck = Deck()
    
    unique_hand_ids = set()
    
    for starting_hand in list(combinations(deck.cards, 4)):
        suit_counts = [[],[],[],[]]
        for card in starting_hand:
            suit_counts[card.suit].append(card.val)
        for suit in suit_counts:
            list.sort(suit)
        list.sort(suit_counts, key=lambda m: suit_id(m))
        hand_id = (tuple(reversed(suit_counts[3])), tuple(reversed(suit_counts[2])), tuple(reversed(suit_counts[1])), tuple(reversed(suit_counts[0])))
        unique_hand_ids.add(hand_id)

    sorted_hand_ids = sorted(unique_hand_ids, key=lambda x: (rev_suit_id(x[0]), rev_suit_id(x[1]), rev_suit_id(x[2]), rev_suit_id(x[3])), reverse=True)

    fout = open('starting_hands/unique_hands.json', 'w')
    fout.write(json.dumps(sorted_hand_ids))
    fout.close()