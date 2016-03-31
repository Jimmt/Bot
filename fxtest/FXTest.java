package fxtest;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import com.sun.webkit.dom.HTMLDocumentImpl;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import netscape.javascript.JSObject;


public class FXTest extends Application
{
    Browser browser;

    int width = 1366, height = 768;

    Stage primaryStage;


    @Override
    public void start( Stage primaryStage )
    {
        this.primaryStage = primaryStage;

        BorderPane root = new BorderPane();
        browser = new Browser( width, height );
        root.getChildren().add( browser );

        Scene scene = new Scene( root, width, height );
        // scene.getStylesheets().add( "style.css" );

        primaryStage.setTitle( "" );
        primaryStage.setScene( scene );
        primaryStage.show();

        final Timeline timeline = new Timeline( new KeyFrame( Duration.ZERO, new EventHandler()
        {
            @Override
            public void handle( Event event )
            {
                update();
            }
        } ), new KeyFrame( Duration.millis( 17 ) ) );
        timeline.setCycleCount( Timeline.INDEFINITE );
        timeline.play();

        // CookieManager cmrCookieMan = new CookieManager(new
        // MyCookieStore(this.objContext), CookiePolicy.ACCEPT_ALL);
        // CookieHandler.setDefault(cmrCookieMan);
    }


    public void update()
    {
        browser.update();
        browser.width = (int)primaryStage.getWidth();
        browser.height = (int)primaryStage.getHeight();
    }


    public static void main( String[] args )
    {
        launch( args );
    }

}
