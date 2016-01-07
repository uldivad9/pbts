import time
import pickle
import itertools
from cards import Hand, make_hand, make_card, relative_strength

start = time.clock()
board = make_hand("5s 6s 8s")
hand = make_hand("8c 8d ac ah")
print(relative_strength(hand,board))
hand = make_hand("7s 9s ah ac")
print(relative_strength(hand,board))
hand = make_hand("7s 9d td jd")
print(relative_strength(hand,board))
hand = make_hand("2d 3c 7s tc")
print(relative_strength(hand,board))


end = time.clock()
print("Completed in {}".format(end-start))