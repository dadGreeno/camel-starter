package camel_starter.route;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class FileMoveRoute extends RouteBuilder {


    @Override
	public void configure() throws Exception {
		from("file://d:/data/testmove")
        .routeId("uniqueRouteName")
        .log("Firing My First Route!")
        .choice()
            .when(fileIsStart)
            .to("direct:GoToMyFileMover")
        .endChoice()
        .otherwise()
        .choice()
            .when(fileIsEdit)
            .to("direct:EditMyFileFirst")
        .endChoice()
        .otherwise()
        .to("direct:GoToMyFailureRoute");

        from("direct:GoToMyFileMover")
        .to("file://d:/data/moved")
        .log("File Moved Successfully");

        from("direct:EditMyFileFirst")
        .process(new Processor(){
        
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("Edited file has been written!");                
            }
        })
        .to("file://d:/data/edited?fileName=Edited.txt")
        .log("Edit File Created!");

        from("direct:GoToMyFailureRoute")
        .log("Your File Didn't go anywhere, but was processed.");
		
    }

    Predicate fileIsStart = new Predicate(){
    
        @Override
        public boolean matches(Exchange exchange) {
            File file = exchange.getIn().getBody(File.class);
            return file.getName().contains("start");
        }
    };

    Predicate fileIsEdit = new Predicate(){
    
        @Override
        public boolean matches(Exchange exchange) {
            File file = exchange.getIn().getBody(File.class);
            return file.getName().contains("edit");
        }
    };
}