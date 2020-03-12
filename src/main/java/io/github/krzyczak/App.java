package io.github.krzyczak;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class App
{
    public static void main( String[] args ) throws Exception {
        org.apache.camel.CamelContext context = new DefaultCamelContext();

        context.addRoutes(new RouteBuilder() {
          @Override
          public void configure() {
            from("file://./?fileName=in.csv&noop=true")
              .to("file://./?fileName=out.csv")
              .end();
          }
        });

        // System.out.println( "Hello World!" );

        context.start();

        System.out.println("Application context started");

        try {
          System.out.println("Wating 2s...");
          Thread.sleep(2000);
          System.out.println("Done Wating. Exiting.");
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        context.stop();

        context.close();
    }
}
