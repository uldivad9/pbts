from cards import Hand, Deck, Card, best_score
import cPickle
import csv
import time
import json
from itertools import combinations

start = time.clock()


pcklin = open("hand_score/hand_scores.pickle", "r")
score_dict = cPickle.load(pcklin)
'''
deck = Deck()
all_cards = deck.cards
for combined in combinations(all_cards, 6):
    hand = Hand(list(combined))
    unique_id = hand.unique_id()
    if unique_id not in score_dict:
        score_dict[unique_id] = hand.score()


pcklout = open("hand_score/hand_scores.pickle", "w")
pickle.dump(score_dict, pcklout)
'''
'''

pcklin = open("hand_score/hand_scores.pickle", "r")
score_dict = pickle.loads(pcklin.read())

pairs = [(k,v) for k,v in score_dict.items()]
list.sort(pairs, key=lambda m: m[1], reverse=True)
fout = open("hand_score/hand_scores.txt", "w")
for i in pairs:
    fout.write("{}  {}\n".format(i[0], i[1]))

'''

end = time.clock()
print("ran in {} time".format(end-start))