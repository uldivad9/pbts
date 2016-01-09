class PlayerProfile:
    def __init__(self):
        
        self.check_obs = [8, 10, 6, 6]
        self.call_obs = [10, 8, 5, 5]
        self.calls = [2, 2, 1, 1]
        self.bets = [2, 3, 2, 2]
        self.raises = [2, 1, 1, 1]
        self.wins = [6, 6, 3, 3]
        self.showdowns = 5
        self.showdown_wins = 3
        
    
    def pretty_print(self):
        print("Check obs: {}".format(self.check_obs))
        print("Call obs: {}".format(self.call_obs))
        print("Calls: {}".format(self.calls))
        print("Bets: {}".format(self.bets))
        print("Raises: {}".format(self.raises))
        print("Wins: {}".format(self.wins))
        print("Showdowns: {}".format(self.showdowns))
        print("Showdown wins: {}".format(self.showdown_wins))
    
    
    def reset(self):
        self.check_obs = [0, 0, 0, 0]
        self.call_obs = [0, 0, 0, 0]
        self.calls = [0, 0, 0, 0]
        self.bets = [0, 0, 0, 0]
        self.raises = [0, 0, 0, 0]
        self.wins = [0, 0, 0, 0]
        self.showdowns = 0
        self.showdown_wins = 0