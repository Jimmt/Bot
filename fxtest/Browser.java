package fxtest;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.sun.glass.ui.Screen;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


public class Browser extends BorderPane
{
    Stack<String> backStack, forwardStack;

    WebView browser = new WebView();

    WebEngine webEngine = browser.getEngine();

    BrowserBar browserBar;

    String pageSource;

    int width, height;


    public Browser( int width, int height )
    {
        getStyleClass().add( "browser" );
        this.width = width;
        this.height = height;

        backStack = new Stack<String>();
        forwardStack = new Stack<String>();

        browserBar = new BrowserBar( width, height, this );
        browser.setMinSize( width, height );

        cacheSource();
        load( "https://login.live.com/" ); // load homepage
        backStack.add( webEngine.getLocation() );

        webEngine.getLoadWorker().stateProperty().addListener( new ChangeListener<State>()
        {
            public void changed( ObservableValue ov, State oldState, State newState )
            {
                if ( newState == Worker.State.SUCCEEDED )
                {
                    browserBar.displayURL( webEngine.getLocation() );
                    cacheSource();
                }
            }
        } );
        setTop( browserBar );
        setCenter( browser );

    }


    public String fileToString( String path )
    {
        String str = "";
        try (BufferedReader reader = new BufferedReader( new FileReader( new File( path ) ) ))
        {
            String line = reader.readLine();
            while ( line != null )
            {
                str += line;
                line = reader.readLine();
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return str;
    }


    public void showSource()
    {
        // Alert alert = new Alert( AlertType.INFORMATION );
        // alert.setTitle( "Source" );
        // alert.setResizable( true );
        // alert.setGraphic( null );
        // alert.getDialogPane().setPrefSize( width * 0.66, height * 0.8 );
        // alert.setHeaderText( null );
        // // alert.setContentText( pageSource );
        // TextArea textArea = new TextArea( pageSource );
        // textArea.setEditable( false );
        // textArea.setWrapText( false );
        //
        // textArea.setMaxWidth( Double.MAX_VALUE );
        // textArea.setMaxHeight( Double.MAX_VALUE );
        // GridPane.setVgrow( textArea, Priority.ALWAYS );
        // GridPane.setHgrow( textArea, Priority.ALWAYS );
        //
        // GridPane expContent = new GridPane();
        // expContent.setMaxWidth( Double.MAX_VALUE );
        // expContent.add( textArea, 0, 1 );
        // alert.getDialogPane().setExpandableContent( expContent );
        // alert.show();
    }


    public void cacheSource()
    {
        webEngine.getLoadWorker().stateProperty().addListener( new ChangeListener<State>()
        {
            public void changed( ObservableValue ov, State oldState, State newState )
            {
                if ( newState == Worker.State.SUCCEEDED )
                {
                    Document doc = webEngine.getDocument();
                    try
                    {
                        Transformer transformer = TransformerFactory.newInstance().newTransformer();
                        transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "no" );
                        transformer.setOutputProperty( OutputKeys.METHOD, "xml" );
                        transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
                        transformer.setOutputProperty( OutputKeys.ENCODING, "UTF-8" );
                        transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "4" );

                        ByteArrayOutputStream boas = new ByteArrayOutputStream();
                        transformer.transform( new DOMSource( doc ), new StreamResult( boas ) );
                        pageSource = boas.toString();
                    }
                    catch ( Exception ex )
                    {
                        ex.printStackTrace();
                    }
                }
            }
        } );
    }


    public void back()
    {
        if ( backStack.size() > 0 )
        {
            forwardStack.add( webEngine.getLocation() );
            webEngine.load( backStack.pop() );
        }
        else
        {
            browserBar.back.setDisable( true );
        }
    }


    public void forward()
    {
        if ( forwardStack.size() > 0 )
        {
            backStack.add( webEngine.getLocation() );
            webEngine.load( forwardStack.pop() );
        }
        else
        {
            browserBar.forward.setDisable( true );
        }
    }


    public void load( String text )
    {
        backStack.add( webEngine.getLocation() );
        webEngine.load( text );
    }


    public void refresh()
    {
        webEngine.reload();
    }


    public void update()
    {
        browserBar.update( this );
        browser.setMinSize( width, height );
    }
}
