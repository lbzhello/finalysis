from pystock.database.tool import date_tool

class KLine:
    def __init__(self, stock_code, start_time=date_tool.now(), end_time=date_tool.now(), 
    open=0.00, close=0.00, high=0.00, low=0.00, 
    volume=0, turnover=0.00, 
    volume_ratio=0.00, turnover_rate=0.00, committee=0.00, 
    selling=0.00, buying=0.00):
        self.stock_code = stock_code
        self.start_time = start_time
        self.end_time = end_time
        self.open = open
        self.close = close
        self.high = high
        self.low = low
        self.volume = volume
        self.turnover = turnover
        self.volume_ratio = volume_ratio
        self.turnover_rate = turnover_rate
        self.committee = committee
        self.selling = selling
        self.buying = buying
