import imp
import argparse
import socket
import sys
import pickle
import os
local = os.getcwd()
sys.path.append("{}\..\..".format(local))
from cards import Hand, Deck, make_hand, make_card, relative_strength
from playerprofile import PlayerProfile

"""
Simple example pokerbot, written in python.

This is an example of a bare bones pokerbot. It only sets up the socket
necessary to connect with the engine and then always returns the same action.
It is meant as an example of how a pokerbot should communicate with the engine.
"""
class Player:    
    def run(self, input_socket):
        # Get a file-object for reading packets from the socket.
        # Using this ensures that you get exactly one packet per read.
        f_in = input_socket.makefile()
        
        debug = True
        
        hand = Hand()
        strength = 0
        board = Hand()
        pip = 0
        
        num_raises = 0
        
        pcklin = open('../../starting_hands/hand_strengths.pickle', 'r')
        hand_strengths = pickle.loads(pcklin.read())
        pcklin.close()
        
        opp_profile = PlayerProfile()
        
        while True:
            # Block until the engine sends us a packet.
            data = f_in.readline().strip()
            # If data is None, connection has closed.
            if not data:
                print "Gameover, engine disconnected."
                break
            if debug:
                print data
            
            words = data.split()
            
            if words[0] == "NEWGAME":
                my_name = words[1]
                opp_name = words[2]
                stack_size = int(words[3])
                bb = int(words[4])
                num_hands = int(words[5])
                time_bank = float(words[6])
            elif words[0] == "NEWHAND":
                hand_id = int(words[1])
                button = (words[2].lower() == 'true')
                hand = make_hand(' '.join(words[3:7]))
                board = Hand()
                my_bank = int(words[7])
                opp_bank = int(words[8])
                time_bank = float(words[9])
                pip = 0
                hand_strength_data = hand_strengths[hand.unique_id()]
                strength = float(hand_strength_data[0])/hand_strength_data[1]
                num_raises = 0
                street = 0
                my_last_action = "CHECK"
                
                if(debug):
                    opp_profile.reset()
            elif words[0] == "HANDOVER":
                opp_profile.pretty_print()
                print
            elif words[0] == "GETACTION":
                pot_size = int(words[1])
                board_size = int(words[2])
                for i in range(len(board.cards), board_size):
                    board = board.add(make_card(words[3 + i]))
                curr_token = 3 + board_size
                num_last_actions = int(words[curr_token])
                last_actions_raw = words[curr_token + 1 : curr_token + num_last_actions + 1]
                print("Last actions: {}".format(last_actions_raw))
                curr_token += num_last_actions + 1
                num_legal_actions = int(words[curr_token])
                legal_actions_raw = words[curr_token + 1 : curr_token + num_legal_actions + 1]
                print("Legal actions: {}".format(legal_actions_raw))
                curr_token += num_legal_actions + 1
                timeBank = float(words[curr_token])
                
                can_check = False
                can_bet = False
                min_bet = 0
                max_bet = 0
                can_raise = False
                min_raise = 0
                max_raise = 0
                can_call = False
                to_call = 0
                
                for action in last_actions_raw:
                    print("processing action {}".format(action))
                    print("street: {}".format(street))
                    split_action = action.split(':')
                    if split_action[0] == 'DEAL':
                        # New card has been dealt, pip resets.
                        pip = 0
                        street += 1
                    elif split_action[-1] != my_name:
                        if split_action[0] == 'CHECK':
                            opp_profile.check_obs[street] += 1
                        elif split_action[0] == 'CALL':
                            opp_profile.call_obs[street] += 1
                            opp_profile.calls[street] += 1
                        elif split_action[0] == 'BET':
                            to_call = int(split_action[1])
                            opp_profile.check_obs[street] += 1
                            opp_profile.bets[street] += 1
                        elif split_action[0] == 'RAISE':
                            to_call = int(split_action[1])
                            opp_profile.call_obs[street] += 1
                            opp_profile.raises[street] += 1
                        elif split_action[0] == 'FOLD':
                            if my_last_action == 'CHECK':
                                opp_profile.check_obs[street] += 1
                            else:
                                opp_profile.call_obs[street] += 1
                        elif split_action[0] == 'POST':
                            to_call = 2
                    else:
                        my_last_action = split_action[0]
                        if split_action[0] == 'POST':
                            pip = int(split_action[1])
                
                for action in legal_actions_raw:
                    split_action = action.split(':')
                    if split_action[0] == 'CHECK':
                        can_check = True
                    elif split_action[0] == 'BET':
                        can_bet = True
                        min_bet = int(split_action[1])
                        max_bet = int(split_action[2])
                    elif split_action[0] == 'CALL':
                        can_call = True
                    elif split_action[0] == 'RAISE':
                        can_raise = True
                        min_raise = int(split_action[1])
                        max_raise = int(split_action[2])
                
                output = None
                
                if board_size == 0:
                    # Pre-flop strategy #
                    print("My hand strength is {}".format(strength))
                    if can_check:
                        if strength > 0.6:
                            # Premium hand #
                            if max_bet > 0:
                                output = "BET:{}".format(max_bet)
                            elif max_raise > 0:
                                output = "RAISE:{}".format(max_raise)
                            else:
                                output = "CHECK"
                        elif strength > 0.53:
                            # Good hand #
                            if max_bet > 0:
                                output = "BET:{}".format((min_bet+max_bet)/2)
                            elif max_raise > 0:
                                output = "RAISE:{}".format((min_raise+max_raise)/2)
                            else:
                                output = "CHECK"
                        else:
                            output = "CHECK"
                    else:
                        if strength > 0.7 and max_raise > 0:
                            output = "RAISE:{}".format(max_raise)
                        elif strength > 0.65 and max_raise > 0 and num_raises < 3:
                            output = "RAISE:{}".format(max_raise)
                        elif strength > 0.58 and max_raise > 0 and num_raises < 2:
                            output = "RAISE:{}".format(max_raise)
                        elif strength > 0.55:
                            output = "CALL"
                        else:
                            output = "FOLD"
                        
                else:
                    # Post-flop strategy #
                    strength = relative_strength(hand, board)
                    print("My relative strength is {}".format(strength))
                    
                    if can_check:
                        if max_bet == 0:
                            output = "CHECK"
                        else:
                            if strength > 0.9:
                                # Uber hand #
                                output = "BET:{}".format(max_bet)
                            elif strength > 0.75:
                                # Strong hand#
                                output = "BET:{}".format((max_bet+min_bet)/2)
                            else:
                                output = "CHECK"
                    elif num_raises == 0:
                        if strength > 0.95:
                            if max_raise > 0:
                                output = "RAISE:{}".format(max_raise)
                            else:
                                output = "CALL"
                        elif strength > 0.8:
                            raise_amount = int(pot_size * strength / 1.5)
                            if max_raise > 0 and raise_amount >= min_raise:
                                output = "RAISE:{}".format(min(max_raise, raise_amount))
                            else:
                                call_amount = int(pot_size * strength)
                                if to_call < call_amount:
                                    output = "CALL"
                                else:
                                    output = "FOLD"
                        elif strength > 0.65:
                            call_amount = int(pot_size * strength / 1.5)
                            if to_call < call_amount:
                                output = "CALL"
                            else:
                                output = "FOLD"
                        else:
                            output = "FOLD"
                    else:
                        if strength > 0.96 + 0.01 * num_raises:
                            if max_raise > 0:
                                output = "RAISE:{}".format(max_raise)
                            else:
                                output = "CALL"
                        elif strength > 0.85:
                            output = "CALL"
                        else:
                            output = "FOLD"
                    
                if (debug):
                    print("My hand: {}".format(hand))
                    print("The board: {}".format(board))
                    print("my name: {}".format(my_name))
                    if can_check:
                        print("I can CHECK.")
                    if can_call:
                        print("I can CALL {}".format(to_call))
                    if can_bet:
                        print("I can BET {} to {}".format(min_bet, max_bet))
                    if can_raise:
                        print("I can RAISE {} to {}".format(min_raise, max_raise))
                    if pip > 0:
                        print("I have {} in the pot".format(pip))
                    print("I choose to {}".format(output.strip()))
                
                # Calculate pip (how much money I have in the pot)
                split_output = output.split(':')
                if split_output[0] == 'RAISE' or split_output[0] == 'BET':
                    pip = int(split_output[1])
                
                # Add newline to output for the engine
                if (output[-1] != '\n'):
                    s.send("{}\n".format(output))
                else:
                    s.send(output)
                
                if(debug):
                    print
                    
            elif words[0] == "REQUESTKEYVALUES":
                # At the end, the engine will allow your bot save key/value pairs.
                # Send FINISH to indicate you're done.
                s.send("FINISH\n")
        # Clean up the socket.
        s.close()

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='A Pokerbot.', add_help=False, prog='pokerbot')
    parser.add_argument('-h', dest='host', type=str, default='localhost', help='Host to connect to, defaults to localhost')
    parser.add_argument('port', metavar='PORT', type=int, help='Port on host to connect to')
    args = parser.parse_args()
    
    # Create a socket connection to the engine.
    print 'Connecting to %s:%d' % (args.host, args.port)
    try:
        s = socket.create_connection((args.host, args.port))
    except socket.error as e:
        print 'Error connecting! Aborting'
        exit()

    bot = Player()
    bot.run(s)
