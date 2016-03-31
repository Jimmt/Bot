package fxtest;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


public class URLField extends TextField
{
    boolean selectAll;


    public URLField( int width, final BrowserBar browserBar )
    {
        setMaxWidth( width );

        setOnKeyReleased( new EventHandler<KeyEvent>()
        {
            public void handle( KeyEvent ke )
            {
                if ( ke.getCode() == KeyCode.ENTER )
                {
                    browserBar.processURL();
                }
            }
        } );
        focusedProperty().addListener( new ChangeListener<Boolean>()
        {
            @Override
            public void changed( ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue )
            {
                if ( newValue )
                {
                    selectAll = true;
                }
                else
                {
                    selectAll = false;
                }
            }
        } );
    }


    public void update()
    {
        if ( selectAll )
        {
            selectAll();
            selectAll = false;
        }
    }

}
