package io.github.krzyczak;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class App
{
    static int count = 0;
    
    public static void main( String[] args ) throws Exception {
        org.apache.camel.CamelContext context = new DefaultCamelContext();

        context.addRoutes(new RouteBuilder() {
          @Override
          public void configure() {
            from("file://./?fileName=in.csv&noop=true")
              .split(body().tokenize("\n"))
              .filter().method(MyBean.class, "isGoldCustomer")
                    .to("file://./gold/?fileName=${date:now:yyyy-MM-dd}_${exchangeId}.csv")
                    .to("stream:out")
              .end()
              .aggregate(constant(true), new AggregationStrategy() {
                  @Override
                  public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                      if (oldExchange == null) {
//                          System.out.println("newExchange ----- " + newExchange.getIn().getBody());
                          // we start a new correlation group, so complete all previous groups
  //                        newExchange.setProperty(Exchange.AGGREGATION_COMPLETE_ALL_GROUPS, true);
                          return newExchange;
                      }

                      String oldBody = oldExchange.getIn().getBody(String.class);
                      String newBody = newExchange.getIn().getBody(String.class);

                      System.out.println("oldExchange ----- " + oldBody + " filter-prop: " + newExchange.getProperty("CamelFilterMatched"));

                      boolean exchangeWasFiltered = (boolean) newExchange.getProperty("CamelFilterMatched");
                      if (exchangeWasFiltered) {
                          oldExchange.getIn().setBody(oldBody);
                      } else {
                          oldExchange.getIn().setBody(oldBody + "\n" + newBody);
                      }
                      
                      return oldExchange;
                  }
              })
              .completionSize(exchangeProperty("CamelSplitSize"))
//              .completionSize(MyBean.class, "completionSize")
//              .completionSize(new Expression() {
//                @Override
//                public <T> T evaluate(Exchange exchange, Class<T> type) {
//                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//                }
//              })

              .to("file://./?fileName=out.csv")
              .end();
          }
        });

        // System.out.println( "Hello World!" );

        context.start();

        System.out.println("Application context started");

        try {
          System.out.println("Wating 2s...");
          Thread.sleep(3000);
          System.out.println("Done Wating. Exiting.");
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        context.stop();

        context.close();
    }
}
