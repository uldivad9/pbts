from enum import Enum

class Suit(Enum):
    club = 0
    diamond = 1
    heart = 2
    spade = 3

class Card:
    __init__(num):
        self.num = num
        self.suit = Suit(num/13)
        self.val = num%13
    
    
    
    __hash__():
        return num

class Deck:
    __init():
        self.cards = set(Card(i) for i in range(52))

deck = Deck()
print(deck.cards)