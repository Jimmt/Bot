package fxtest;

import java.io.ByteArrayOutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;


public class BrowserBar extends HBox
{
    Browser browser;

    URLField urlField;

    Button back, forward, refresh, source, login, search;

    boolean selectAll = false;


    public BrowserBar( int width, int height, Browser browser )
    {
        this.browser = browser;

        back = new Button( "<" );
        back.setOnAction( ( ActionEvent e ) -> {
            browser.back();
        } );
        forward = new Button( ">" );
        forward.setOnAction( ( ActionEvent e ) -> {
            browser.forward();
        } );
        refresh = new Button( "Refresh" );
        refresh.setOnAction( ( ActionEvent e ) -> {
            browser.refresh();
        } );
        source = new Button( "Source" );
        // getChildren().add( source );
        source.setOnAction( ( ActionEvent e ) -> {
            browser.showSource();
        } );
        login = new Button( "Auto-login" );
        login.setOnAction( ( ActionEvent e ) -> {
            browser.load( "https://login.live.com/" );
            browser.webEngine.getLoadWorker().stateProperty().addListener( new ChangeListener<State>()
            {
                public void changed( ObservableValue ov, State oldState, State newState )
                {
                    if ( newState == Worker.State.SUCCEEDED )
                    {
                        if ( browser.webEngine.getLocation() == "https://login.live.com/" )
                        {
                            String str = browser.fileToString( "login.js" );
                            browser.webEngine.executeScript( str );
                            browser.webEngine.getLoadWorker().stateProperty().removeListener( this );
                        }
                    }
                }
            } );
        } );
        search = new Button( "Start Searching" );
        search.setOnAction( ( ActionEvent e ) -> {
            browser.load( "https://bing.com" );
            browser.webEngine.getLoadWorker().stateProperty().addListener( new ChangeListener<State>()
            {
                public void changed( ObservableValue ov, State oldState, State newState )
                {
                    if ( newState == Worker.State.SUCCEEDED )
                    {
                        if ( browser.webEngine.getLocation().endsWith( "bing.com/" ) )
                        {
                            String str = browser.fileToString( "firstSearch.js" );
                            browser.webEngine.executeScript( str );
                        }
                        else if ( browser.webEngine.getLocation().startsWith( "https://www.bing.com/search?q=" ) )
                        {
                            String str = browser.fileToString( "search.js" );
                            browser.webEngine.executeScript( str );
                        }
                    }
                }
            } );
        } );

        urlField = new URLField( width, this );
        HBox.setHgrow( urlField, Priority.SOMETIMES );
        getChildren().addAll( back, forward, refresh, login, search );
        getChildren().add( urlField );

    }


    public void displayURL( String url )
    {
        urlField.setText( url );
    }


    public void processURL()
    {
        String text = urlField.getText();
        if ( text.contains( "." ) )
        {
            if ( !text.startsWith( "http://" ) )
            {
                text = "http://" + text;
            }
        }
        else
        {
            String[] strs = text.split( " " );
            text = "https://www.google.com/webhp#q=";
            for ( int i = 0; i < strs.length; i++ )
            {
                text += strs[i] + "+";
            }

        }
        browser.load( text );
    }


    public void update( Browser browser )
    {
        this.browser = browser;
        urlField.update();
    }
}
