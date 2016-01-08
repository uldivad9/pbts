import time
from cards import make_hand, Hand, Deck, best_score


start = time.clock()

h1 = make_hand("Qc, Qs, Td, 5c")
h2 = make_hand("Qh, Th, 7s, 2c")

base_deck = Deck()
for card in h1:
    base_deck.remove(card)
for card in h2:
    base_deck.remove(card)
base_cards = base_deck.cards
wins = 0
for i in range(10000):
    deck = Deck(initial=base_cards)
    board = Hand([deck.deal(), deck.deal(), deck.deal(), deck.deal(), deck.deal()])
    if (best_score(h1, board) >= best_score(h2, board)):
        wins += 1
print(wins/10000.0)


end = time.clock()
print("Completed in {}".format(end-start))