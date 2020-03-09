package io.github.krzyczak.camelbeans;

import java.util.List;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.CsvDataFormat;
import org.springframework.stereotype.Component;

/**
 * A simple Camel route that triggers from a timer and calls a bean and prints to system out.
 * <p/>
 * Use <tt>@Component</tt> to make Camel auto detect this route when starting.
 */
@Component
public class CamelBeansRouter extends RouteBuilder {
    
    @Override
    public void configure() {
//        CsvDataFormat csv = new CsvDataFormat();
//        csv.setSkipHeaderRecord("true");

//          CsvDataFormat csvParser = new CsvDataFormat();
//          csvParser.setSkipHeaderRecord(true);
//          csvParser.setQuoteMode(QuoteMode.ALL);

//        csv.setSkipFirstLine(true);
//        from("timer:hello?period={{timer.period}}").routeId("hello")
//            .transform().method("myBean", "saySomething")
//            .filter(simple("${body} contains 'foo'"))
//                .to("log:foo: ${body}")
//                .to("log:foo")
////                .log("BEGIN-${body}-END")
//            .end()
//            .to("stream:out");
        
        from("file:///Users/krzyczak/NetBeansProjects/CamelBeans?fileName=data.csv&charset=utf-8&noop=true")
            .log("\n${body}")
            .unmarshal().csv()
            .split(body()).streaming()
            .bean(Utils.class, "standardizeCase")
            .log("\n\n\n ----------- LIST ${body} ------------ \n\n\n")
            .aggregate(constant(true), new AggregationStrategy() {
                @Override
                public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                    if (oldExchange == null) {
                        System.out.println("newExchange ----- " + newExchange.getIn().getBody());
                        // we start a new correlation group, so complete all previous groups
//                        newExchange.setProperty(Exchange.AGGREGATION_COMPLETE_ALL_GROUPS, true);
                        return newExchange;
                    }

                    String oldBody = oldExchange.getIn().getBody(String.class);
                    String newBody = newExchange.getIn().getBody(String.class);
                    
                    System.out.println("oldExchange ----- " + oldBody);

                    oldExchange.getIn().setBody(oldBody + "\n" + newBody);
                    return oldExchange;
                }
            })
            .completionInterval(5000)
            .log("\n\n\n ----------- exchangeProperty: ${exchangeProperty.completionSize} ------------ \n\n\n")
            .log("\n\n\n ----------- LIST ${body} ------------ \n\n\n")
//            .bean(Utils.class, "standardizeCase")
            .to("file://?fileName=out.csv&charset=utf-8")
            .end();
          
    }

}
