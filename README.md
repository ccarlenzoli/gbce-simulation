# gbce-simulation


In order to Run this Example you can use class

com.jpmorgan.stock.calculator.test.JPMSStockMarketTest

or from command line write

java -jar jpMorganGBCEStockMarket.jar


This start the example with some configuration information defined.
In particular example it starts with:

2 Sessions dedicated to enrich trades values
3 Sessions dedicated to read the single queue filled by the 2 sessions of enrichment
1 internal HttpServer responding at the port 9090.
 
In order to read the status of the underlying "market" one can use a Web Browser and digit

http://127.0.0.1:9090/stockMarketStatus




