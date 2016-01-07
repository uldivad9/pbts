from cards import Hand, Deck, Card, best_score
import pickle
import csv
import time
import json
from itertools import combinations

start = time.clock()

while (True):
    print("Beginning new loop - running for {} seconds".format(time.clock()-start))
    unique_hands = json.loads(open('starting_hands/unique_hands.pickle', 'r').read())
    
    try:
        pin = open('starting_hands/hand_strengths.pickle', 'r')
        hand_strengths = pickle.loads(pin.read())
    except:
        hand_strengths = {}
    
    PLAYS = 5
    for i, hand_id in enumerate(unique_hands):
        if i == 0:
            print("0%")
        if i == 4108:
            print("25%")
        elif i == 8216:
            print("50%")
        elif i == 12324:
            print("75%")
            
        hand = Hand()
        for i, suit in enumerate(hand_id):
            for val in suit:
                hand = hand.add(Card(i*13+val-2))
        remaining_deck = Deck()
        for card in hand:
            remaining_deck.remove(card)
        remaining_cards = remaining_deck.cards
        wins = 0
        for i in range(PLAYS):
            deck = Deck(initial=remaining_cards)
            opp_hand = Hand(cards=[deck.deal(), deck.deal(), deck.deal(), deck.deal()])
            board = Hand(cards=[deck.deal(), deck.deal(), deck.deal(), deck.deal(), deck.deal()])
            my_score = best_score(hand, board)
            opp_score = best_score(opp_hand, board)
            if my_score >= opp_score:
                wins += 1
        tupled_hand_id = tuple(tuple(thing) for thing in hand_id)
        
        if tupled_hand_id in hand_strengths:
            prev_record = hand_strengths[tupled_hand_id]
        else:
            prev_record = (0,0)
        hand_strengths[tupled_hand_id] = (prev_record[0]+wins, prev_record[1]+PLAYS)
    
    print("PICKLING: DO NOT BREAK")
    pout = open('starting_hands/hand_strengths.pickle', 'w')
    pickle.dump(hand_strengths, pout)
    pout.close()
    print("Pickling complete")

'''

jsin = open('starting_hands/hand_strengths.pickle', 'r')
test = pickle.loads(jsin.read())

writer = csv.writer(open('starting_hands/hand_strengths.csv', 'wb'))
hand_strengths = [(k,v) for k,v in test.items()]
list.sort(hand_strengths, key=lambda p: p[1], reverse=True)
for pair in hand_strengths:
    writer.writerow([pair[0].__str__(), float(pair[1][0])/pair[1][1]])
'''

end = time.clock()
print("ran in {} time".format(end-start))