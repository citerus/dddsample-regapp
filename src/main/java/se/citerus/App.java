package se.citerus;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import se.citerus.registerapp.RegisterApp;
import se.citerus.registerapp.RegisterAppConfiguration;

@SpringBootApplication
@Import(RegisterAppConfiguration.class)
public class App {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(App.class).headless(false).run(args);
        RegisterApp app = (RegisterApp) ctx.getBean("registerApp");

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(app::show);
    }
}